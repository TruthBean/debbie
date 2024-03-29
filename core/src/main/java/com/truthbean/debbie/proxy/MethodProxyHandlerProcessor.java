package com.truthbean.debbie.proxy;

import com.truthbean.debbie.bean.ClassBeanInfo;
import com.truthbean.debbie.core.ApplicationContext;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.*;

public class MethodProxyHandlerProcessor<T> {
    private boolean noProxy;
    private final ClassBeanInfo<T> beanInfo;
    private final MethodProxyHandlerHandler handler;
    private final ApplicationContext applicationContext;

    public MethodProxyHandlerProcessor(ApplicationContext applicationContext, MethodProxyHandlerHandler handler,
                                       ClassBeanInfo<T> beanInfo) {
        this.beanInfo = beanInfo;
        this.handler = handler;
        this.applicationContext = applicationContext;
    }

    public boolean hasNoProxy() {
        return noProxy;
    }

    public MethodProxyHandlerHandler getHandler() {
        return handler;
    }

    public MethodProxyHandlerProcessor<T> process() {
        Map<Method, Set<Annotation>> methodWithAnnotations = beanInfo.getMethodWithAnnotations();
        boolean classAnnotationContainMethodProxy = beanInfo.containClassAnnotation(MethodProxy.class);
        if (methodWithAnnotations.isEmpty() && !classAnnotationContainMethodProxy) {
            noProxy = true;
            return this;
        }
        MethodProxyResolver methodProxyResolver = new MethodProxyResolver(applicationContext, beanInfo);
        if (!classAnnotationContainMethodProxy) {
            methodWithAnnotations.forEach((method, annotations) -> {
                List<MethodProxyHandler<? extends Annotation>> methodProxyHandler =
                        methodProxyResolver.getMethodProxyHandler(method, null);
                handler.addInterceptors(methodProxyHandler);
            });
        } else {
            Set<Method> methods = beanInfo.getMethods();
            methods.forEach(method -> {
                List<MethodProxyHandler<? extends Annotation>> methodProxyHandler =
                        methodProxyResolver.getMethodProxyHandler(method, Arrays.asList(beanInfo.getClazz().getAnnotations()));
                handler.addInterceptors(methodProxyHandler);
            });
        }
        if (!handler.hasInterceptor()) {
            noProxy = true;
            return this;
        }
        noProxy = false;
        return this;
    }

    public T proxy(BeanProxy<T> proxy) {
        return proxy.proxy(beanInfo.getBean());
    }
}