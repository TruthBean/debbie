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
import java.util.function.Supplier;

/**
 * @author TruthBean/Rogar·Q
 * @since 0.1.0
 * Created on 2020-08-10 19:37
 */
public class MethodTaskInfo {
    private Supplier<?> taskBean;
    private Method taskMethod;
    private DebbieTask taskAnnotation;

    public MethodTaskInfo() {
    }

    public MethodTaskInfo(Supplier<?> taskBean, Method taskMethod, DebbieTask taskAnnotation) {
        this.taskBean = taskBean;
        this.taskMethod = taskMethod;
        this.taskAnnotation = taskAnnotation;
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
