package com.truthbean.debbie.proxy.interfaces.jdk;

import com.truthbean.debbie.proxy.bean.TargetInterface;
import com.truthbean.debbie.proxy.interfaces.TargetProxyHandler;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

public class TargetInvocationHandler implements InvocationHandler {

    private TargetProxyHandler interceptor;
    private TargetInterface target;

    public TargetInvocationHandler(TargetInterface target) {
        this.interceptor = new TargetProxyHandler();
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
