/**
 * Copyright (c) 2020 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *         http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.task;

import com.truthbean.debbie.bean.*;
import com.truthbean.debbie.concurrent.NamedThreadFactory;
import com.truthbean.debbie.concurrent.ThreadPooledExecutor;
import com.truthbean.debbie.core.ApplicationContext;
import com.truthbean.debbie.core.ApplicationContextAware;
import com.truthbean.debbie.reflection.ClassLoaderUtils;
import com.truthbean.debbie.reflection.ExecutableArgument;
import com.truthbean.debbie.reflection.ExecutableArgumentHandler;
import com.truthbean.debbie.reflection.ReflectionHelper;
import com.truthbean.Logger;
import com.truthbean.debbie.spi.SpiLoader;
import com.truthbean.debbie.util.StringUtils;
import com.truthbean.logger.LoggerFactory;

import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.ThreadFactory;

/**
 * @author TruthBean
 * @since 0.0.2
 */
public class TaskFactory implements ApplicationContextAware, BeanClosure {

    private ApplicationContext applicationContext;
    private GlobalBeanFactory globalBeanFactory;
    private volatile boolean taskRunning;

    private final Set<TaskAction> taskActions;
    private final Set<DebbieBeanInfo<?>> taskBeans = new LinkedHashSet<>();
    private final Set<MethodTaskInfo> taskList = new LinkedHashSet<>();

    TaskFactory() {
        var classLoader = ClassLoaderUtils.getClassLoader(TaskAction.class);
        this.taskActions = SpiLoader.loadProviderSet(TaskAction.class, classLoader);;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
        this.globalBeanFactory = applicationContext.getGlobalBeanFactory();
        for (TaskAction taskAction : this.taskActions) {
            taskAction.setApplicationContext(applicationContext);
        }
    }


    void registerTask() {
        BeanInitialization beanInitialization = applicationContext.getBeanInitialization();
        Set<DebbieBeanInfo<?>> tasks = beanInitialization.getAnnotatedMethodBean(DebbieTask.class);
        if (tasks != null && !tasks.isEmpty()) {
            taskBeans.addAll(tasks);
        }
    }

    private final ThreadFactory namedThreadFactory = new NamedThreadFactory("DebbieTask", true);
    private final ThreadPooledExecutor taskThreadPool = new ThreadPooledExecutor(1, 1, namedThreadFactory);
    private final Timer timer = new Timer();

    public void prepare() {
        final Set<DebbieBeanInfo<?>> taskBeanSet = new LinkedHashSet<>(this.taskBeans);
        for (DebbieBeanInfo<?> taskBean : taskBeanSet) {
            Object task = globalBeanFactory.factory(taskBean.getServiceName());
            LOGGER.trace(() -> "task bean " + taskBean.getBeanClass());
            Set<Method> methods = taskBean.getAnnotationMethod(DebbieTask.class);
            for (Method method : methods) {
                DebbieTask annotation = method.getAnnotation(DebbieTask.class);
                var taskInfo = new MethodTaskInfo(taskBean.getBeanClass(), () -> task, method, annotation, this::doMethodTask);
                taskList.add(taskInfo);
                for (TaskAction taskAction : taskActions) {
                    taskAction.prepare(taskInfo);
                }
            }
        }
    }

    public void doTask() {
        final ThreadPooledExecutor executor = globalBeanFactory.factory("threadPooledExecutor");
        final Set<MethodTaskInfo> taskSet = new LinkedHashSet<>(this.taskList);
        taskThreadPool.execute(() -> {
            LOGGER.trace("do task....");
            for (MethodTaskInfo taskInfo : taskSet) {
                doTask(executor, taskInfo, timer);
            }
        });
        for (TaskAction taskAction : taskActions) {
            taskAction.doTask();
        }
    }

    private void doTask(final ThreadPooledExecutor executor, final MethodTaskInfo taskInfo, final Timer timer) {
        DebbieTask annotation = taskInfo.getTaskAnnotation();
        if (!annotation.async()) {
            doMethodTask(taskInfo, annotation, timer);
        } else {
            try {
                executor.execute(() -> {
                    try {
                        doMethodTask(taskInfo, annotation, timer);
                    } catch (Throwable ex) {
                        LOGGER.error("", ex);
                    }
                });
            } catch (Exception e) {
                LOGGER.error("", e);
            }
        }
    }

    private void doMethodTask(final MethodTaskInfo taskInfo, final DebbieTask annotation, final Timer timer) {
        var fixedRate = annotation.fixedRate();
        var cron = annotation.cron();
        if (fixedRate > -1) {
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    taskInfo.accept();
                }
            }, 0, fixedRate);
        } else if (StringUtils.hasText(cron)) {
            // todo cron
        } else {
            taskInfo.accept();
        }
    }

    private void doMethodTask(MethodTaskInfo taskInfo) {
        List<ExecutableArgument> methodParams = ExecutableArgumentHandler.typeOf(taskInfo.getTaskMethod(), globalBeanFactory);
        Object[] params = new Object[methodParams.size()];
        for (int i = 0; i < methodParams.size(); i++) {
            params[i] = methodParams.get(i).getValue();
        }
        taskRunning = true;
        ReflectionHelper.invokeMethod(taskInfo.getTaskBean().get(), taskInfo.getTaskMethod(), params);
        taskRunning = false;
    }

    public boolean isTaskRunning() {
        return taskRunning;
    }

    @Override
    public synchronized void destroy() {
        if (!isTaskRunning()) {
            LOGGER.info("destroy tasks bean");
            timer.cancel();
            taskThreadPool.destroy();
            taskBeans.clear();
            // TODO: 清除正在运行的任务
        }
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(TaskFactory.class);
}
