package com.truthbean.debbie.task;

import com.truthbean.debbie.bean.*;
import com.truthbean.debbie.reflection.ExecutableArgument;
import com.truthbean.debbie.reflection.ExecutableArgumentHandler;
import com.truthbean.debbie.reflection.ReflectionHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

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

    public void doTask() {
        LOGGER.trace("do task....");
        ThreadPooledExecutor executor = beanFactoryHandler.factory("threadPooledExecutor");
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
        LOGGER.info("destroy tasks bean");
        taskBeans.clear();
        // TODO: 清除正在运行的任务
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(TaskFactory.class);
}
