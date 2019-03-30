package com.truthbean.code.debbie.core.aop;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @author TruthBean
 * @since 0.0.1
 * Created on 2019/3/23 10:15.
 */
public class InterfaceInvocationHandler<Target> implements InvocationHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(InterfaceInvocationHandler.class);

    private Target target;

    public InterfaceInvocationHandler(Class<Target> targetClass) {
        try {
            var constructor = targetClass.getDeclaredConstructor();
            constructor.setAccessible(true);
            this.target = constructor.newInstance();
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            LOGGER.error("new instance by default constructor error", e);
        }
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        var methodName = method.getName();
        LOGGER.debug(methodName);
        //before
        Object invoke = null;
        try {
            invoke = method.invoke(target, args);
        } catch (Exception e) {
            LOGGER.error(methodName + " invoke error", e);
        }
        //after
        if (invoke != null) {
            LOGGER.debug(invoke.toString());
        } else {
            LOGGER.debug(methodName + " return null or void");
        }
        return invoke;
    }
}
