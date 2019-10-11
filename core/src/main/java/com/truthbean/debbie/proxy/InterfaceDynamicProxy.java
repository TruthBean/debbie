package com.truthbean.debbie.proxy;

import com.truthbean.debbie.bean.BeanFactoryHandler;
import com.truthbean.debbie.reflection.ClassLoaderUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;

/**
 * @author TruthBean
 * @since 0.0.1
 * Created on 2019/3/23 10:36.
 */
public class InterfaceDynamicProxy<T, K extends T> {
    private static final Logger LOGGER = LoggerFactory.getLogger(InterfaceDynamicProxy.class);

    public T invokeJdkProxy(BeanFactoryHandler handler, Class<T> targetInterface, K target) {
        InvocationHandler invocationHandler = new ProxyInvocationHandler<>(target, handler);

        return doJdkProxy(targetInterface, target, invocationHandler);
    }

    public T doJdkProxy(Class<T> targetInterface, K target, InvocationHandler invocationHandler) {
        var targetClass = target.getClass();
        var classLoader = ClassLoaderUtils.getClassLoader(targetClass);
        var interfaces = new Class[]{targetInterface};

        var proxyInstance = Proxy.newProxyInstance(classLoader, interfaces, invocationHandler);
        var proxyClass = proxyInstance.getClass();
        LOGGER.debug(targetInterface.getName());
        LOGGER.debug(targetClass.getName());
        LOGGER.debug(proxyClass.getName());

        T result;

        try {
            result = targetInterface.cast(proxyInstance);
        } catch (Exception e) {
            LOGGER.warn(proxyClass.getName() + " cast to " + targetClass.getName() + " error", e);
            // handle error
            result = target;
        }

        // do after
        LOGGER.debug("after proxy ....");

        return result;
    }

    public K getRealValue(Object proxyValue) {
        InvocationHandler invocationHandler = Proxy.getInvocationHandler(proxyValue);
        if (invocationHandler instanceof ProxyInvocationHandler) {
            ProxyInvocationHandler<K> proxyInvocationHandler = (ProxyInvocationHandler) invocationHandler;
            return proxyInvocationHandler.getRealTarget();
        }
        return null;
    }

}
