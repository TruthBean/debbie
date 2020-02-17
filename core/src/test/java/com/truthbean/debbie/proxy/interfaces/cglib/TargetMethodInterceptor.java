package com.truthbean.debbie.proxy.interfaces.cglib;

import com.truthbean.debbie.proxy.interfaces.TargetProxyHandler;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;

public class TargetMethodInterceptor implements MethodInterceptor {
    private TargetProxyHandler interceptor;
    private Object target;

    public TargetMethodInterceptor(Object target) {
        this.interceptor = new TargetProxyHandler();
        this.target = target;
    }

    @Override
    public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
        interceptor.before();
        Object invoke = method.invoke(target, args);
        interceptor.after();
        return invoke;
    }
}
