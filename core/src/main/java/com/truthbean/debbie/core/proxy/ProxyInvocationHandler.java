package com.truthbean.debbie.core.proxy;

import com.truthbean.debbie.core.reflection.ReflectionHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.annotation.Annotation;
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
        var proxyClassName = proxy.getClass().getName();
        LOGGER.debug(proxyClassName + " proxy " + target.getClass().getName());
        MethodProxyHandler proxyHandler = getMethodProxyHandler(method);

        var methodName = method.getName();
        proxyHandler.before();
        //before
        Object invoke = null;
        try {
            invoke = method.invoke(target, args);
            proxyHandler.after();
        } catch (Exception e) {
            LOGGER.error(methodName + " invoke error. \n", e);
            proxyHandler.whenExceptionCached(e);
        } finally {
            proxyHandler.finallyRun();
        }
        //after
        return invoke;
    }

    private MethodProxyHandler getMethodProxyHandler(Method method) {
        Annotation[] declaredAnnotations = method.getDeclaredAnnotations();
        if (declaredAnnotations != null && declaredAnnotations.length > 0) {
            for (Annotation annotation: declaredAnnotations) {
                var annotationType = annotation.annotationType();
                if (annotationType == MethodProxy.class) {
                    MethodProxy methodProxy = (MethodProxy) annotation;
                    var proxyHandler = methodProxy.proxyHandler();
                    MethodProxyHandler<MethodProxy> methodProxyHandler = ReflectionHelper.newInstance(proxyHandler);
                    methodProxyHandler.setMethodAnnotation((MethodProxy) annotation);
                    return methodProxyHandler;
                } else {
                    MethodProxy methodProxy = annotationType.getAnnotation(MethodProxy.class);
                    if (methodProxy != null) {
                        var proxyHandler = methodProxy.proxyHandler();
                        MethodProxyHandler handler = ReflectionHelper.newInstance(proxyHandler);
                        handler.setMethodAnnotation(annotation);
                        return handler;
                    }
                }
            }
        }
        return new DefaultMethodProxyHandler(method.getName());
    }
}
