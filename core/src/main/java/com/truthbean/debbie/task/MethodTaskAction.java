/**
 * Copyright (c) 2026 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *         http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.task;

import com.truthbean.Logger;
import com.truthbean.LoggerFactory;
import com.truthbean.core.util.ReflectionUtils;
import com.truthbean.debbie.bean.BeanInfo;
import com.truthbean.debbie.reflection.ClassInfo;
import com.truthbean.debbie.reflection.ExecutableArgument;
import com.truthbean.debbie.reflection.ExecutableArgumentHandler;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Set;

/**
 * @author TruthBean/Rogar·Q
 * @since 0.5.7
 */
public class MethodTaskAction extends AbstractTaskAction {

    @Override
    protected String getTaskThreadName() {
        return "MethodTask";
    }

    @Override
    public void prepare() {
        Set<BeanInfo<?>> tasks = applicationContext.getBeanInfoManager().getAnnotatedMethodsBean(DebbieTask.class);
        if (tasks != null && !tasks.isEmpty()) {
            for (BeanInfo taskBean : tasks) {
                Object task = applicationContext.getGlobalBeanFactory().factory(taskBean.getName());
                LOGGER.trace(() -> "task bean " + taskBean.getBeanClass());
                if (taskBean instanceof ClassInfo classInfo) {
                    Set<Method> methods = classInfo.getAnnotationMethod(DebbieTask.class);
                    for (Method method : methods) {
                        DebbieTask annotation = method.getAnnotation(DebbieTask.class);
                        var taskInfo = new MethodTaskInfo(taskBean.getBeanClass(), task, method, annotation, this::doTask);
                        taskList.add(taskInfo);
                    }
                }
            }
        }
    }

    private void doTask(TaskInfo taskInfo) {
        if (applicationContext.isExiting()) {
            taskInfo.setRunning(false);
            return;
        }
        if (taskInfo instanceof MethodTaskInfo methodTaskInfo) {
            List<ExecutableArgument> methodParams = ExecutableArgumentHandler.typeOf(methodTaskInfo.getTaskMethod(), applicationContext.getGlobalBeanFactory(), applicationContext.getClassLoader());
            Object[] params = new Object[methodParams.size()];
            for (int i = 0; i < methodParams.size(); i++) {
                params[i] = methodParams.get(i).getValue();
            }
            taskInfo.setRunning(true);
            try {
                ReflectionUtils.invokeMethod(methodTaskInfo.getTaskBean(), methodTaskInfo.getTaskMethod(), params);
            } catch (Exception e) {
                LOGGER.error("task(" + taskInfo);
            }
        }
        taskInfo.setRunning(false);
    }

    @Override
    protected Logger getLogger() {
        return LOGGER;
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodTaskAction.class);
}
