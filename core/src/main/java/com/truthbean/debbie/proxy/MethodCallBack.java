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

    private final Object[] parameters;

    public MethodCallBack(Object target, Method method, Object... parameters) {
        this.target = target;
        this.method = method;
        this.parameters = parameters;
    }

    public MethodCallBack(Object target, String methodName, Object... parameters) {
        this.target = target;
        this.methodName = methodName;
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
        if (method != null)
            return ReflectionHelper.invokeMethod(target, method, parameters);
        else
            return (T) ReflectionHelper.invokeMethod(target, methodName, parameters);
    }
}
