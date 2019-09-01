package com.truthbean.debbie.task;

import com.truthbean.debbie.bean.BeanFactoryHandler;
import com.truthbean.debbie.bean.BeanInitialization;
import com.truthbean.debbie.bean.DebbieBeanApplication;
import com.truthbean.debbie.bean.DebbieBeanInfo;
import com.truthbean.debbie.reflection.ReflectionHelper;

import java.lang.reflect.Method;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class DebbieTaskFactory implements DebbieBeanApplication {

    private BeanFactoryHandler beanFactoryHandler;

    @Override
    public void setBeanFactoryHandler(BeanFactoryHandler beanFactoryHandler) {
        this.beanFactoryHandler = beanFactoryHandler;
    }

    private Set<DebbieBeanInfo> taskBeans = new LinkedHashSet<>();

    public void registerTask() {
        BeanInitialization beanInitialization = beanFactoryHandler.getBeanInitialization();
        Set<DebbieBeanInfo> tasks = beanInitialization.getAnnotatedMethodBean(DebbieTask.class);
        if (tasks != null && tasks.isEmpty()) {
            taskBeans.addAll(tasks);
        }
    }

    public void doTask() {
        for (DebbieBeanInfo<?> taskBean : taskBeans) {
            Object task = beanFactoryHandler.factory(taskBean.getServiceName());
            List<Method> methods = taskBean.getAnnotationMethod(DebbieTask.class);
            for (Method method : methods) {
                DebbieTask annotation = method.getAnnotation(DebbieTask.class);
                if (!annotation.async()) {
                    ReflectionHelper.invokeMethod(task, method);
                }
            }

        }
    }

    public void destroy() {
        taskBeans.clear();
        // TODO: 清除正在运行的任务
    }
}
