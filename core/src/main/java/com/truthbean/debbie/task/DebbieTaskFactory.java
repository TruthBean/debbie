package com.truthbean.debbie.task;

import com.truthbean.debbie.bean.BeanFactoryHandler;
import com.truthbean.debbie.bean.BeanInitialization;
import com.truthbean.debbie.bean.DebbieBeanApplication;
import com.truthbean.debbie.bean.DebbieBeanInfo;
import com.truthbean.debbie.reflection.ExecutableArgument;
import com.truthbean.debbie.reflection.ExecutableArgumentHandler;
import com.truthbean.debbie.reflection.ReflectionHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.*;

/**
 * @author TruthBean
 * @since 0.0.2
 */
public class DebbieTaskFactory implements DebbieBeanApplication {

    private BeanFactoryHandler beanFactoryHandler;
    private volatile boolean taskRunning;

    @Override
    public void setBeanFactoryHandler(BeanFactoryHandler beanFactoryHandler) {
        this.beanFactoryHandler = beanFactoryHandler;
    }

    private Set<DebbieBeanInfo<?>> taskBeans = new LinkedHashSet<>();

    public void registerTask() {
        BeanInitialization beanInitialization = beanFactoryHandler.getBeanInitialization();
        Set<DebbieBeanInfo<?>> tasks = beanInitialization.getAnnotatedMethodBean(DebbieTask.class);
        if (tasks != null && !tasks.isEmpty()) {
            taskBeans.addAll(tasks);
        }
    }

    public void doTask() {
        LOGGER.trace("do task....");
        for (DebbieBeanInfo<?> taskBean : taskBeans) {
            Object task = beanFactoryHandler.factory(taskBean.getServiceName());
            LOGGER.trace("task bean " + taskBean.getBeanClass());
            List<Method> methods = taskBean.getAnnotationMethod(DebbieTask.class);
            for (Method method : methods) {
                DebbieTask annotation = method.getAnnotation(DebbieTask.class);
                if (!annotation.async()) {
                    List<ExecutableArgument> methodParams = ExecutableArgumentHandler.typeOf(method, beanFactoryHandler);
                    Object[] params = new Object[methodParams.size()];
                    for (int i = 0; i < methodParams.size(); i++) {
                        params[i] = methodParams.get(i).getValue();
                    }
                    taskRunning = true;
                    ReflectionHelper.invokeMethod(task, method, params);
                    taskRunning = false;
                }
            }
        }
    }

    public boolean isTaskRunning() {
        return taskRunning;
    }

    public void destroy() {
        taskBeans.clear();
        // TODO: 清除正在运行的任务
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(DebbieTaskFactory.class);
}
