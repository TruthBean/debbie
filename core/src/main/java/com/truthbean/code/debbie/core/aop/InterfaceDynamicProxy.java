package com.truthbean.code.debbie.core.aop;

import net.sf.cglib.proxy.InterfaceMaker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Proxy;

/**
 * @author TruthBean
 * @since 0.0.1
 * Created on 2019/3/23 10:36.
 */
public class InterfaceDynamicProxy {
    private static final Logger LOGGER = LoggerFactory.getLogger(InterfaceDynamicProxy.class);

    public <T> T doProxy(Class<T> targetClass) {
        try {
            T target = targetClass.getConstructor().newInstance();
            InterfaceMaker interfaceMaker = new InterfaceMaker();
            //抽取某个类的方法生成接口方法
            interfaceMaker.add(targetClass);
            Class<T> targetInterface = interfaceMaker.create();
            return doProxy(targetInterface, target);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            e.printStackTrace();
        }
        return null;
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

        var proxyInstance = Proxy.newProxyInstance(classLoader, interfaces, invocationHandler);

         T result = (T) proxyInstance;

        //handle result
         LOGGER.debug(result.toString());
        return target;
    }

    private static class TargetProxy<T> {

    }
}
