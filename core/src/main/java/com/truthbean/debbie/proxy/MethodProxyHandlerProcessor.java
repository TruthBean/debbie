package com.truthbean.debbie.proxy;

import com.truthbean.debbie.bean.BeanFactoryContext;
import com.truthbean.debbie.bean.DebbieBeanInfo;
import com.truthbean.debbie.proxy.asm.AbstractProxy;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class MethodProxyHandlerProcessor<T> {
    private boolean noProxy;
    private final DebbieBeanInfo<T> beanInfo;
    private final MethodProxyHandlerHandler handler;
    private final BeanFactoryContext applicationContext;

    public MethodProxyHandlerProcessor(BeanFactoryContext applicationContext, MethodProxyHandlerHandler handler,
                                       DebbieBeanInfo<T> beanInfo) {
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
        if (methodWithAnnotations == null || methodWithAnnotations.isEmpty()) {
            noProxy = true;
            return this;
        }
        MethodProxyResolver methodProxyResolver = new MethodProxyResolver(applicationContext, beanInfo);
        methodWithAnnotations.forEach((method, annotations) -> {
            List<MethodProxyHandler<? extends Annotation>> methodProxyHandler =
                    methodProxyResolver.getMethodProxyHandler(method, annotations);
            handler.addInterceptors(methodProxyHandler);
        });
        if (!handler.hasInterceptor()) {
            noProxy = true;
            return this;
        }
        noProxy = false;
        return this;
    }

    public T proxy(AbstractProxy<T> proxy) {
        return proxy.proxy(beanInfo::getBean);
    }
}