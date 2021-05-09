/**
 * Copyright (c) 2021 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *         http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.proxy;


import com.truthbean.debbie.reflection.ReflectionHelper;

import java.lang.reflect.Method;
import java.util.concurrent.Callable;

/**
 * @author truthbean/Rogar·Q
 * @since 0.0.2
 * Created on 2020-04-21 15:44.
 */
public class MethodCallBack<T> implements Callable<T> {
    private final Object target;
    private Method method;

    private String methodName;
    private Class<?>[] parameterTypes;

    private final Object[] parameters;

    public MethodCallBack(Object target, Method method, Object... parameters) {
        this.target = target;
        this.method = method;
        this.parameters = parameters;
    }

    public MethodCallBack(Object target, String methodName) {
        this.target = target;
        this.methodName = methodName;
        this.parameterTypes = new Class[0];
        this.parameters = new Object[0];
    }

    public MethodCallBack(Object target, String methodName, Class<?>[] parameterTypes, Object[] parameters) {
        this.target = target;
        this.methodName = methodName;
        this.parameterTypes = parameterTypes;
        this.parameters = parameters;
    }

    public String getMethodName() {
        if (method != null)
            return method.getName();
        else
            return methodName;
    }

    @Override
    @SuppressWarnings("unchecked")
    public T call() {
        if (method != null) {
            return ReflectionHelper.invokeMethod(target, method, parameters);
        }
        else
            return (T) ReflectionHelper.invokeMethod(target, methodName, parameters, parameterTypes);
    }
}
