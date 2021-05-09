package com.truthbean.debbie.proxy.interfaces.jdk;

import com.truthbean.debbie.proxy.MethodCallBack;
import com.truthbean.debbie.proxy.MethodProxyHandlerHandler;
import com.truthbean.debbie.proxy.bean.TargetInterface;
import com.truthbean.debbie.proxy.interfaces.TargetProxyHandler;
import com.truthbean.Logger;
import com.truthbean.LoggerFactory;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public class JdkInvocationHandler<I> implements InvocationHandler {

    private final MethodProxyHandlerHandler interceptor;
    private final I target;
    private final Class<I> targetClass;

    public JdkInvocationHandler(Class<I> targetClass, I target) {
        this.interceptor = new MethodProxyHandlerHandler(LOGGER);
        this.interceptor.addInterceptor(new TargetProxyHandler());
        this.target = target;
        this.targetClass = targetClass;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        MethodCallBack<Object> callBack = new MethodCallBack<Object>(target, method, args);
        return interceptor.proxy(callBack);
    }

    @SuppressWarnings("unchecked")
    public I newProxyInstance() {
        return (I) Proxy.newProxyInstance(targetClass.getClassLoader(), new Class[]{targetClass}, this);
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(JdkInvocationHandler.class);
}
