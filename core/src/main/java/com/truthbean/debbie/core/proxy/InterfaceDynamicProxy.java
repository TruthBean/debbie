package com.truthbean.debbie.core.proxy;

import com.truthbean.debbie.core.reflection.ClassLoaderUtils;
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

    public static <T, K extends T> T doJdkProxy(Class<T> targetInterface, K target) {
        var targetClass = target.getClass();
        var classLoader = ClassLoaderUtils.getClassLoader(targetClass);
        var interfaces = new Class[]{targetInterface};
        InvocationHandler invocationHandler = new ProxyInvocationHandler<>(target);

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

}
