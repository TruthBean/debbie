package com.truthbean.code.debbie.core.aop;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;

/**
 * @author TruthBean
 * @since 0.0.1
 * Created on 2019/3/23 10:36.
 */
public class InterfaceDynamicProxy {
    private static final Logger LOGGER = LoggerFactory.getLogger(InterfaceDynamicProxy.class);

    public <T> T doProxy(T target) {
        return doProxy((Class<T>) target.getClass(), target);
    }

    public <T, K extends T> T doProxy(Class<T> targetInterface, K target) {
        var classLoader = target.getClass().getClassLoader();
        var interfaces = new Class[]{targetInterface};
        InvocationHandler invocationHandler = (proxy, method, args) -> {
            String methodName = method.getName();
            LOGGER.debug(methodName);
            //before
            Object invoke = null;
            try {
                invoke = method.invoke(target, args);
            } catch (Exception e) {
                LOGGER.debug("invoke error", e);
            }
            //after
            if (invoke != null) {
                LOGGER.debug(invoke.toString());
            } else {
                LOGGER.debug(methodName + " return null or void");
            }
            return invoke;
        };

        T result = (T) Proxy.newProxyInstance(classLoader, interfaces, invocationHandler);
        //handle result
        LOGGER.debug(result.toString());
        return result;
    }

    private static class TargetProxy<T> {

    }
}
