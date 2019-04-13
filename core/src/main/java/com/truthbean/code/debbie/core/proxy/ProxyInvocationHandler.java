package com.truthbean.code.debbie.core.proxy;

import com.truthbean.code.debbie.core.reflection.ReflectionHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * @author TruthBean
 * @since 0.0.1
 * Created on 2019/3/23 10:15.
 */
public class ProxyInvocationHandler<Target> implements InvocationHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(ProxyInvocationHandler.class);

    private Target target;

    public ProxyInvocationHandler(Class<Target> targetClass) {
        var target = ReflectionHelper.newInstance(targetClass);
        if (target == null) {
            LOGGER.error("new instance by default constructor error");
        } else {
            this.target = target;
        }
    }

    public ProxyInvocationHandler(Target target) {
        LOGGER.debug("init ProxyInvocationHandler with " + target);
        this.target = target;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) {
        LOGGER.debug(proxy.getClass().getName());
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
            LOGGER.debug(invoke.getClass().getName());
        } else {
            LOGGER.debug(methodName + " return null or void");
        }
        return target;
    }
}
