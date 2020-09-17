/**
 * Copyright (c) 2020 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 * http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.task;

import java.lang.reflect.Method;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * @author TruthBean/Rogar·Q
 * @since 0.1.0
 * Created on 2020-08-10 19:37
 */
public class MethodTaskInfo {
    private Class<?> taskBeanClass;
    private Supplier<?> taskBean;
    private Method taskMethod;
    private DebbieTask taskAnnotation;
    private Consumer<MethodTaskInfo> consumer;

    public MethodTaskInfo() {
    }

    public MethodTaskInfo(Class<?> taskBeanClass, Supplier<?> taskBean, Method taskMethod, DebbieTask taskAnnotation,
                          Consumer<MethodTaskInfo> consumer) {
        this.taskBeanClass = taskBeanClass;
        this.taskBean = taskBean;
        this.taskMethod = taskMethod;
        this.taskAnnotation = taskAnnotation;
        this.consumer = consumer;
    }

    public Class<?> getTaskBeanClass() {
        return taskBeanClass;
    }

    public void setTaskBeanClass(Class<?> taskBeanClass) {
        this.taskBeanClass = taskBeanClass;
    }

    public Supplier<?> getTaskBean() {
        return taskBean;
    }

    public void setTaskBean(Supplier<?> taskBean) {
        this.taskBean = taskBean;
    }

    public Method getTaskMethod() {
        return taskMethod;
    }

    public void setTaskMethod(Method taskMethod) {
        this.taskMethod = taskMethod;
    }

    public DebbieTask getTaskAnnotation() {
        return taskAnnotation;
    }

    public void setTaskAnnotation(DebbieTask taskAnnotation) {
        this.taskAnnotation = taskAnnotation;
    }

    public Consumer<MethodTaskInfo> getConsumer() {
        return consumer;
    }

    public void setConsumer(Consumer<MethodTaskInfo> consumer) {
        this.consumer = consumer;
    }

    public void accept() {
        this.consumer.accept(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MethodTaskInfo taskInfo = (MethodTaskInfo) o;
        return Objects.equals(taskMethod, taskInfo.taskMethod);
    }

    @Override
    public int hashCode() {
        return Objects.hash(taskMethod);
    }
}
