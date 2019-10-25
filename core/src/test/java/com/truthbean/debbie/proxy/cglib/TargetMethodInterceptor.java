package com.truthbean.debbie.proxy.cglib;

import com.truthbean.debbie.proxy.TargetInterceptor;
import com.truthbean.debbie.proxy.TargetInterface;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;

public class TargetMethodInterceptor implements MethodInterceptor {
    private TargetInterceptor interceptor;
    private TargetInterface target;

    public TargetMethodInterceptor(TargetInterface target) {
        this.interceptor = new TargetInterceptor();
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
