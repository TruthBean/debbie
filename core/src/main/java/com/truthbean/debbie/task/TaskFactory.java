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
import com.truthbean.debbie.reflection.ExecutableArgument;
import com.truthbean.debbie.reflection.ExecutableArgumentHandler;
import com.truthbean.debbie.reflection.ReflectionHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ThreadFactory;

/**
 * @author TruthBean
 * @since 0.0.2
 */
public class TaskFactory implements BeanFactoryHandlerAware, BeanClosure {

    private BeanFactoryHandler beanFactoryHandler;
    private volatile boolean taskRunning;

    @Override
    public void setBeanFactoryHandler(BeanFactoryHandler beanFactoryHandler) {
        this.beanFactoryHandler = beanFactoryHandler;
    }

    private final Set<DebbieBeanInfo<?>> taskBeans = new LinkedHashSet<>();

    public void registerTask() {
        BeanInitialization beanInitialization = beanFactoryHandler.getBeanInitialization();
        Set<DebbieBeanInfo<?>> tasks = beanInitialization.getAnnotatedMethodBean(DebbieTask.class);
        if (tasks != null && !tasks.isEmpty()) {
            taskBeans.addAll(tasks);
        }
    }

    private final ThreadFactory namedThreadFactory = new NamedThreadFactory("DebbieTask", true);
    private final ThreadPooledExecutor taskThreadPool = new ThreadPooledExecutor(1, 1, namedThreadFactory);

    public void doTask() {
        final ThreadPooledExecutor executor = beanFactoryHandler.factory("threadPooledExecutor");
        final Set<DebbieBeanInfo<?>> taskBeans = new LinkedHashSet<>(this.taskBeans);
        taskThreadPool.execute(() -> {
            LOGGER.trace("do task....");
            for (DebbieBeanInfo<?> taskBean : taskBeans) {
                Object task = beanFactoryHandler.factory(taskBean.getServiceName());
                LOGGER.trace("task bean " + taskBean.getBeanClass());
                Set<Method> methods = taskBean.getAnnotationMethod(DebbieTask.class);
                for (Method method : methods) {
                    DebbieTask annotation = method.getAnnotation(DebbieTask.class);
                    if (!annotation.async()) {
                        doMethodTask(task, method);
                    } else {
                        try {
                            executor.execute(() -> {
                                try {
                                    doMethodTask(task, method);
                                } catch (Throwable ex) {
                                    LOGGER.error("", ex);
                                }
                            });
                        } catch (Exception e) {
                            LOGGER.error("", e);
                        }
                    }
                }
            }
        });
    }

    private void doMethodTask(Object task, Method method) {
        List<ExecutableArgument> methodParams = ExecutableArgumentHandler.typeOf(method, beanFactoryHandler);
        Object[] params = new Object[methodParams.size()];
        for (int i = 0; i < methodParams.size(); i++) {
            params[i] = methodParams.get(i).getValue();
        }
        taskRunning = true;
        ReflectionHelper.invokeMethod(task, method, params);
        taskRunning = false;
    }

    public boolean isTaskRunning() {
        return taskRunning;
    }

    @Override
    public void destroy() {
        if (!isTaskRunning()) {
            LOGGER.info("destroy tasks bean");
            taskThreadPool.destroy();
            taskBeans.clear();
            // TODO: 清除正在运行的任务
        }
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(TaskFactory.class);
}
