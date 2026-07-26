package com.truthbean.debbie.proxy;

import com.truthbean.Logger;
import com.truthbean.core.util.ReflectionUtils;
import com.truthbean.debbie.bean.DebbieReflectionBeanFactory;
import com.truthbean.debbie.core.ApplicationContext;
import com.truthbean.LoggerFactory;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

/**
 * @author TruthBean
 * @since 0.0.1
 * Created on 2019/3/23 10:15.
 */
public class ProxyInvocationHandler<Target> implements InvocationHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(ProxyInvocationHandler.class);

    private Target target;

    private final DebbieReflectionBeanFactory<Target> classInfo;

    private final MethodProxyHandlerHandler handler;
    private final MethodProxyResolver methodProxyResolver;

    private final ApplicationContext applicationContext;

    public ProxyInvocationHandler(Class<Target> targetClass, ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
        var target = ReflectionUtils.newInstance(targetClass);
        classInfo = new DebbieReflectionBeanFactory<>(targetClass);
        if (target == null) {
            LOGGER.error("new instance by default constructor error");
        } else {
            this.target = target;
        }

        this.handler = new MethodProxyHandlerHandler(LOGGER);
        this.methodProxyResolver = new MethodProxyResolver(applicationContext, classInfo);
    }

    @SuppressWarnings("unchecked")
    public ProxyInvocationHandler(Target target, ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
        LOGGER.debug(() -> "init ProxyInvocationHandler with " + target);
        this.target = target;
        Class<Target> targetClass = (Class<Target>) target.getClass();
        this.classInfo = new DebbieReflectionBeanFactory<>(targetClass);

        this.handler = new MethodProxyHandlerHandler(LOGGER);
        this.methodProxyResolver = new MethodProxyResolver(applicationContext, classInfo);
    }

    public Target getRealTarget() {
        return target;
    }

    protected DebbieReflectionBeanFactory<Target> getBeanInfo() {
        return classInfo;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if (applicationContext.isExiting()) {
            return ReflectionUtils.getDefaultValue(method.getReturnType());
        }
        var proxyClassName = proxy.getClass().getName();
        Class<?> targetClass = target.getClass();
        LOGGER.trace(() -> proxyClassName + " proxy " + targetClass.getName());
        Method targetMethod;
        Class<?>[] parameterClass = method.getParameterTypes();
        var methodName = method.getName();
        try {
            targetMethod = ReflectionUtils.getDeclaredMethod(targetClass, methodName, parameterClass);
            if (targetMethod == null) {
                if ("toString".equals(methodName)) {
                    return target.toString();
                } else if ("getClass".equals(methodName)) {
                    return target.getClass();
                } else if ("hashCode".equals(methodName)) {
                    return target.hashCode();
                } else if ("equals".equals(methodName)) {
                    return target.equals(args);
                } else if ("notify".equals(methodName)) {
                    target.notify();
                } else if ("notifyAll".equals(methodName)) {
                    target.notifyAll();
                } else if ("wait".equals(methodName)) {
                    target.wait();
                }
                LOGGER.warn(() -> targetClass + " has no method(" + methodName + "). ");
                targetMethod = method;
            }
        } catch (Exception e) {
            LOGGER.warn(() -> targetClass + " has no method(" + methodName + "). ");
            targetMethod = method;
        }
        List<MethodProxyHandler<? extends Annotation>> methodInterceptors =
                this.methodProxyResolver.getMethodProxyHandler(targetMethod, Arrays.asList(targetClass.getAnnotations()));
        if (!methodInterceptors.isEmpty()) {
            methodInterceptors.sort(MethodProxyHandler::compareTo);
        }
        this.handler.setInterceptors(methodInterceptors);
        return this.handler.proxy(methodName, () -> {
            if (!applicationContext.isExiting()) {
                return method.invoke(target, args);
            }
            return ReflectionUtils.getDefaultValue(method.getReturnType());
        }, () -> {
            if (!applicationContext.isExiting()) {
                return method.invoke(target, args);
            }
            return ReflectionUtils.getDefaultValue(method.getReturnType());
        });
    }

}
