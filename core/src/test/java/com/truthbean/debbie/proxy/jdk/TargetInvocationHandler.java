package com.truthbean.debbie.proxy.jdk;

import com.truthbean.debbie.proxy.TargetInterceptor;
import com.truthbean.debbie.proxy.TargetInterface;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

public class TargetInvocationHandler implements InvocationHandler {

    private TargetInterceptor interceptor;
    private TargetInterface target;

    public TargetInvocationHandler(TargetInterface target) {
        this.interceptor = new TargetInterceptor();
        this.target = target;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        interceptor.before();
        Object invoke = method.invoke(target, args);
        interceptor.after();
        return invoke;
    }
}
