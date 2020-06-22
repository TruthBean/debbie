package com.truthbean.debbie.proxy.interfaces.cglib;

import com.truthbean.debbie.proxy.MethodCallBack;
import com.truthbean.debbie.proxy.MethodProxyHandlerHandler;
import com.truthbean.debbie.proxy.interfaces.TargetProxyHandler;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;
import com.truthbean.Logger;

import java.lang.reflect.Method;

public class TargetMethodInterceptor implements MethodInterceptor {
    private final Object target;
    private final MethodProxyHandlerHandler handler;

    public TargetMethodInterceptor(Object target, TargetProxyHandler interceptor, Logger logger) {
        this.target = target;
        handler = new MethodProxyHandlerHandler(logger);
        handler.addInterceptor(interceptor);
    }

    @Override
    public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
        MethodCallBack<Void> callBack = new MethodCallBack<Void>(target, method);
        return handler.proxy(callBack);
    }
}
