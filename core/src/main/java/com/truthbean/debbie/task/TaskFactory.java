/**
 * Copyright (c) 2024 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 * http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.task;

import com.truthbean.core.concurrent.NamedThreadFactory;
import com.truthbean.debbie.bean.*;
import com.truthbean.debbie.concurrent.*;
import com.truthbean.debbie.core.ApplicationContext;
import com.truthbean.debbie.core.ApplicationContextAware;
import com.truthbean.debbie.proxy.BeanProxyType;
import com.truthbean.debbie.reflection.*;
import com.truthbean.Logger;
import com.truthbean.debbie.spi.SpiLoader;
import com.truthbean.core.util.StringUtils;
import com.truthbean.LoggerFactory;

import java.lang.reflect.Method;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ThreadFactory;

/**
 * @author TruthBean
 * @since 0.0.2
 */
public class TaskFactory implements TaskRegister, ApplicationContextAware, BeanClosure {

    private ApplicationContext applicationContext;
    private GlobalBeanFactory globalBeanFactory;

    private final Set<TaskAction> taskActions;
    private final Set<BeanInfo> taskBeans = new LinkedHashSet<>();
    private final Set<TaskInfo> taskList = new LinkedHashSet<>();

    TaskFactory() {
        var classLoader = ClassLoaderUtils.getClassLoader(TaskAction.class);
        this.taskActions = SpiLoader.loadProviderSet(TaskAction.class, classLoader);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
        this.globalBeanFactory = applicationContext.getGlobalBeanFactory();
        for (TaskAction taskAction : this.taskActions) {
            taskAction.setApplicationContext(applicationContext);
        }
    }

    public void registerTask() {
        var beanInfoManager = applicationContext.getBeanInfoManager();
        Set<BeanInfo<?>> tasks = beanInfoManager.getAnnotatedMethodsBean(DebbieTask.class);
        if (tasks != null && !tasks.isEmpty()) {
            taskBeans.addAll(tasks);
        }
    }

    @Override
    public TaskRegister registerTask(TaskInfo taskInfo) {
        TaskRunnable taskRunnable = taskInfo.getTaskExecutor();
        BeanFactory<?> taskBeanInfo = new SimpleBeanFactory<>(taskRunnable, BeanType.SINGLETON, BeanProxyType.NO, taskRunnable.getName());
        var beanInfoManager = applicationContext.getBeanInfoManager();
        beanInfoManager.registerBeanInfo(taskBeanInfo);
        taskBeans.add(taskBeanInfo);
        taskInfo.setConsumer(this::doTask);
        taskList.add(taskInfo);
        return this;
    }

    private final ThreadFactory namedThreadFactory = new NamedThreadFactory("DebbieTask", true);
    private final PooledExecutor taskThreadPool = new ThreadPooledExecutor(1, 1, namedThreadFactory);
    private final ScheduledPooledExecutor scheduledPooledExecutor = new ScheduledThreadPooledExecutor(Runtime.getRuntime().availableProcessors(), namedThreadFactory);

    public void prepare() {
        final Set<BeanInfo> taskBeanSet = new LinkedHashSet<>(this.taskBeans);
        for (BeanInfo taskBean : taskBeanSet) {
            Object task = globalBeanFactory.factory(taskBean.getName());
            LOGGER.trace(() -> "task bean " + taskBean.getBeanClass());
            if (taskBean instanceof ClassInfo classInfo) {
                Set<Method> methods = classInfo.getAnnotationMethod(DebbieTask.class);
                for (Method method : methods) {
                    DebbieTask annotation = method.getAnnotation(DebbieTask.class);
                    var taskInfo = new MethodTaskInfo(taskBean.getBeanClass(), task, method, annotation, this::doTask);
                    taskList.add(taskInfo);
                    for (TaskAction taskAction : taskActions) {
                        taskAction.prepare(taskInfo);
                    }
                }
            }
        }
    }

    public void doTask() {
        final ThreadPooledExecutor executor = globalBeanFactory.factory("threadPooledExecutor");
        final Set<TaskInfo> taskSet = new LinkedHashSet<>(this.taskList);
        taskThreadPool.execute(() -> {
            LOGGER.trace("do task....");
            for (TaskInfo taskInfo : taskSet) {
                doTask(executor, taskInfo);
            }
        });
        for (TaskAction taskAction : taskActions) {
            taskAction.doTask();
        }
    }

    private void doTask(final ThreadPooledExecutor executor, final TaskInfo taskInfo) {
        DebbieTaskConfig annotation = taskInfo.getTaskConfig();
        if (!annotation.isAsync()) {
            doMethodTask(taskInfo, annotation);
        } else {
            try {
                executor.execute(() -> {
                    try {
                        doMethodTask(taskInfo, annotation);
                    } catch (Throwable ex) {
                        LOGGER.error("", ex);
                    }
                });
            } catch (Exception e) {
                LOGGER.error("", e);
            }
        }
    }

    private void doMethodTask(final TaskInfo taskInfo, final DebbieTaskConfig annotation) {
        var fixedRate = annotation.getFixedRate();
        var cron = annotation.getCron();
        long delay = annotation.getInitialDelay();
        if (fixedRate > -1) {
            if (delay <= 0) {
                delay = 0;
            }
            final long finalDelay = delay;
            taskInfo.getTaskRunnableIfPresent(LOGGER, timerTask -> {
                scheduledPooledExecutor.scheduleAtFixedRate(() -> {
                    if (!applicationContext.isExiting()) {
                        timerTask.run(applicationContext);
                    }
                }, finalDelay, fixedRate);
            });
        } else if (fixedRate == -1 && delay > -1) {
            final long finalDelay = delay;
            taskInfo.getTaskRunnableIfPresent(LOGGER, timerTask -> {
                scheduledPooledExecutor.schedule(() -> {
                    if (!applicationContext.isExiting()) {
                        timerTask.run(applicationContext);
                    }
                }, finalDelay);
            });
        } else if (StringUtils.hasText(cron)) {
            // todo cron
        } else {
            taskInfo.accept();
        }
    }

    private void doTask(TaskInfo taskInfo) {
        if (applicationContext.isExiting()) {
            taskInfo.setRunning(false);
            return;
        }
        if (taskInfo instanceof MethodTaskInfo methodTaskInfo) {
            List<ExecutableArgument> methodParams = ExecutableArgumentHandler.typeOf(methodTaskInfo.getTaskMethod(), globalBeanFactory, applicationContext.getClassLoader());
            Object[] params = new Object[methodParams.size()];
            for (int i = 0; i < methodParams.size(); i++) {
                params[i] = methodParams.get(i).getValue();
            }
            taskInfo.setRunning(true);
            try {
                ReflectionHelper.invokeMethod(methodTaskInfo.getTaskBean(), methodTaskInfo.getTaskMethod(), params);
            } catch (Exception e) {
                LOGGER.error("task(" + taskInfo);
            }
        } else {
            taskInfo.setRunning(true);
            taskInfo.getTaskExecutor().run(applicationContext);
        }
        taskInfo.setRunning(false);
    }

    @Override
    public void destruct(ApplicationContext applicationContext) {
        synchronized (TaskFactory.class) {
            LOGGER.info("destroy tasks bean");
            for (TaskAction taskAction : taskActions) {
                taskAction.stop();
            }
            scheduledPooledExecutor.destroy();
            scheduledPooledExecutor.destroy();
            taskThreadPool.destroy();
            taskBeans.clear();
            taskList.clear();
        }
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(TaskFactory.class);
}
