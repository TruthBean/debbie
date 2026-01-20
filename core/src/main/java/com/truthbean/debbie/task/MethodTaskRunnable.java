/**
 * Copyright (c) 2026 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 * http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.task;

import com.truthbean.Logger;
import com.truthbean.LoggerFactory;
import com.truthbean.debbie.core.ApplicationContext;
import com.truthbean.debbie.reflection.ExecutableArgument;
import com.truthbean.debbie.reflection.ExecutableArgumentHandler;
import com.truthbean.debbie.reflection.ReflectionHelper;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Objects;

/**
 * @author TruthBean/Rogar·Q
 * @since 0.2.0
 * Created on 2020-12-21 11:38
 */
public class MethodTaskRunnable implements TaskRunnable {
    private final Method method;
    private final Class<?> taskBeanClass;
    private final Object taskBean;
    private final Logger logger;

    private volatile boolean running;

    public MethodTaskRunnable(Method method, Class<?> taskBeanClass, Object taskBean) {
        this.method = method;
        this.taskBeanClass = taskBeanClass;
        this.taskBean = taskBean;
        this.logger = LoggerFactory.getLogger(taskBeanClass);
    }

    @Override
    public String getName() {
        return this.method.getName();
    }

    public Method getMethod() {
        return method;
    }

    public Class<?> getTaskBeanClass() {
        return taskBeanClass;
    }

    public Object getTaskBean() {
        return taskBean;
    }

    @Override
    public void run(ApplicationContext applicationContext) {
        if (applicationContext.isExiting() || running) {
            return;
        }
        List<ExecutableArgument> methodParams = ExecutableArgumentHandler.typeOf(method,
                applicationContext.getGlobalBeanFactory(), applicationContext.getClassLoader());
        Object[] params = new Object[methodParams.size()];
        for (int i = 0; i < methodParams.size(); i++) {
            params[i] = methodParams.get(i).getValue();
        }
        running = true;
        try {
            ReflectionHelper.invokeMethod(taskBean, method, params);
        } catch (Exception e) {
            logger.error("task(" + getName() + ") error");
        }
        running = false;
    }

    public boolean isRunning() {
        return running;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MethodTaskRunnable that = (MethodTaskRunnable) o;
        return Objects.equals(method, that.method);
    }

    @Override
    public int hashCode() {
        return Objects.hash(method);
    }
}
