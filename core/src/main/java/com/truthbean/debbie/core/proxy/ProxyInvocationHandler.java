package com.truthbean.debbie.core.proxy;

import com.truthbean.debbie.core.reflection.ClassInfo;
import com.truthbean.debbie.core.reflection.ReflectionHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Map;

/**
 * @author TruthBean
 * @since 0.0.1
 * Created on 2019/3/23 10:15.
 */
public class ProxyInvocationHandler<Target> implements InvocationHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(ProxyInvocationHandler.class);

    private Target target;

    private ClassInfo<Target> classInfo;

    public ProxyInvocationHandler(Class<Target> targetClass) {
        var target = ReflectionHelper.newInstance(targetClass);
        classInfo = new ClassInfo<>(targetClass);
        if (target == null) {
            LOGGER.error("new instance by default constructor error");
        } else {
            this.target = target;
        }
    }

    public ProxyInvocationHandler(Target target) {
        LOGGER.debug("init ProxyInvocationHandler with " + target);
        this.target = target;
        classInfo = new ClassInfo<>((Class<Target>) target.getClass());
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        var proxyClassName = proxy.getClass().getName();
        Class<?> targetClass = target.getClass();
        LOGGER.debug(proxyClassName + " proxy " + targetClass.getName());
        Method targetMethod;
        try {
            targetMethod = targetClass.getDeclaredMethod(method.getName(), method.getParameterTypes());
        } catch (NoSuchMethodException e) {
            LOGGER.warn(targetClass + " has no method(" + method.getName() + "). ");
            targetMethod = method;
        }
        MethodProxyHandler proxyHandler = getMethodProxyHandler(targetMethod);

        var methodName = method.getName();
        Object invoke = null;
        try {
            //before
            proxyHandler.before();
            invoke = method.invoke(target, args);
            //after
            proxyHandler.after();
        } catch (Exception e) {
            Throwable throwable = e.getCause();
            if (throwable == null) {
                throwable = e;
            }
            LOGGER.error(" invoke method(" + methodName + ") error. \n", throwable);
            proxyHandler.whenExceptionCatched(throwable);
        } finally {
            proxyHandler.finallyRun();
        }
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
                    methodProxyHandler.setMethodAnnotation(methodProxy);
                    return methodProxyHandler;
                } else {
                    MethodProxy methodProxy = annotationType.getAnnotation(MethodProxy.class);
                    if (methodProxy != null) {
                        var proxyHandler = methodProxy.proxyHandler();
                        MethodProxyHandler handler = ReflectionHelper.newInstance(proxyHandler);
                        handler.setMethodAnnotation(annotation);
                        handler.setMethod(method);
                        return handler;
                    }
                }
            }
        } else {
            Annotation classAnnotation = classInfo.getClassAnnotation(MethodProxy.class);
            if (classAnnotation != null) {
                MethodProxy methodProxy = (MethodProxy) classAnnotation;
                var proxyHandler = methodProxy.proxyHandler();
                MethodProxyHandler<MethodProxy> methodProxyHandler = ReflectionHelper.newInstance(proxyHandler);
                methodProxyHandler.setClassAnnotation(methodProxy);
                methodProxyHandler.setMethod(method);
                return methodProxyHandler;
            } else {
                Map<Class<? extends Annotation>, Annotation> classAnnotations = classInfo.getClassAnnotations();
                if (classAnnotations != null && !classAnnotations.isEmpty()) {
                    for (Map.Entry<Class<? extends Annotation>, Annotation> classAnnotationEntry : classAnnotations.entrySet()) {
                        Annotation value = classAnnotationEntry.getValue();
                        var annotationType = classAnnotationEntry.getKey();
                        if (annotationType == MethodProxy.class) {
                            MethodProxy methodProxy = (MethodProxy) value;
                            var proxyHandler = methodProxy.proxyHandler();
                            MethodProxyHandler<MethodProxy> methodProxyHandler = ReflectionHelper.newInstance(proxyHandler);
                            methodProxyHandler.setClassAnnotation(methodProxy);
                            methodProxyHandler.setMethod(method);
                            return methodProxyHandler;
                        } else {
                            MethodProxy methodProxy = annotationType.getAnnotation(MethodProxy.class);
                            if (methodProxy != null) {
                                var proxyHandler = methodProxy.proxyHandler();
                                MethodProxyHandler handler = ReflectionHelper.newInstance(proxyHandler);
                                handler.setClassAnnotation(value);
                                handler.setMethod(method);
                                return handler;
                            }
                        }
                    }
                }
            }
        }
        return new DefaultMethodProxyHandler(method.getName());
    }
}
