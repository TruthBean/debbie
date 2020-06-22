package com.truthbean.debbie.proxy;

import com.truthbean.debbie.bean.BeanFactoryHandler;
import com.truthbean.debbie.bean.DebbieBeanInfo;
import com.truthbean.debbie.reflection.ClassLoaderUtils;
import com.truthbean.debbie.reflection.ReflectionHelper;
import com.truthbean.Logger;
import com.truthbean.logger.LoggerFactory;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author TruthBean
 * @since 0.0.1
 * Created on 2019/3/23 10:15.
 */
public class ProxyInvocationHandler<Target> implements InvocationHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(ProxyInvocationHandler.class);

    private Target target;

    private final DebbieBeanInfo<Target> classInfo;
    private final ClassLoader classLoader;

    private final BeanFactoryHandler beanFactoryHandler;

    private final MethodProxyHandlerHandler handler;
    private final MethodProxyResolver methodProxyResolver;

    public ProxyInvocationHandler(Class<Target> targetClass, BeanFactoryHandler beanFactoryHandler) {
        this.classLoader = ClassLoaderUtils.getClassLoader(targetClass);
        this.beanFactoryHandler = beanFactoryHandler;
        var target = ReflectionHelper.newInstance(targetClass);
        classInfo = new DebbieBeanInfo<>(targetClass);
        if (target == null) {
            LOGGER.error("new instance by default constructor error");
        } else {
            this.target = target;
        }

        this.handler = new MethodProxyHandlerHandler(LOGGER);
        this.methodProxyResolver = new MethodProxyResolver(beanFactoryHandler, classInfo);
    }

    @SuppressWarnings("unchecked")
    public ProxyInvocationHandler(Target target, BeanFactoryHandler beanFactoryHandler) {
        LOGGER.debug(() -> "init ProxyInvocationHandler with " + target);
        this.beanFactoryHandler = beanFactoryHandler;
        this.target = target;
        Class<Target> targetClass = (Class<Target>) target.getClass();
        this.classLoader = ClassLoaderUtils.getClassLoader(targetClass);
        this.classInfo = new DebbieBeanInfo<>(targetClass);

        this.handler = new MethodProxyHandlerHandler(LOGGER);
        this.methodProxyResolver = new MethodProxyResolver(beanFactoryHandler, classInfo);
    }

    public Target getRealTarget() {
        return target;
    }

    protected DebbieBeanInfo<Target> getBeanInfo() {
        return classInfo;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        var proxyClassName = proxy.getClass().getName();
        Class<?> targetClass = target.getClass();
        LOGGER.trace(() -> proxyClassName + " proxy " + targetClass.getName());
        Method targetMethod;
        Class<?>[] parameterClass = method.getParameterTypes();
        try {
            targetMethod = ReflectionHelper.getDeclaredMethod(targetClass, method.getName(), parameterClass);
        } catch (Exception e) {
            LOGGER.warn(() -> targetClass + " has no method(" + method.getName() + "). ");
            targetMethod = method;
        }
        List<MethodProxyHandler<? extends Annotation>> methodInterceptors =
                this.methodProxyResolver.getMethodProxyHandler(targetMethod);
        if (!methodInterceptors.isEmpty()) {
            methodInterceptors.sort(MethodProxyHandler::compareTo);
        }
        this.handler.setInterceptors(methodInterceptors);
        var methodName = method.getName();
        return this.handler.proxy(methodName, () -> method.invoke(target, args), () -> method.invoke(target, args));
    }

}
