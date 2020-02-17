package com.truthbean.debbie.proxy;

import com.truthbean.debbie.bean.BeanFactoryHandler;
import com.truthbean.debbie.bean.DebbieBeanInfo;
import com.truthbean.debbie.reflection.ClassLoaderUtils;
import com.truthbean.debbie.reflection.ReflectionHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

    private DebbieBeanInfo<Target> classInfo;
    private ClassLoader classLoader;

    private BeanFactoryHandler beanFactoryHandler;

    private final MethodProxyHandlerHandler handler;

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

        this.handler = new MethodProxyHandlerHandler();
        this.handler.setLogger(LOGGER);
    }

    @SuppressWarnings("unchecked")
    public ProxyInvocationHandler(Target target, BeanFactoryHandler beanFactoryHandler) {
        LOGGER.debug("init ProxyInvocationHandler with " + target);
        this.beanFactoryHandler = beanFactoryHandler;
        this.target = target;
        Class<Target> targetClass = (Class<Target>) target.getClass();
        this.classLoader = ClassLoaderUtils.getClassLoader(targetClass);
        classInfo = new DebbieBeanInfo<>(targetClass);

        this.handler = new MethodProxyHandlerHandler();
        this.handler.setLogger(LOGGER);
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
        LOGGER.debug(proxyClassName + " proxy " + targetClass.getName());
        Method targetMethod;
        Class<?>[] parameterClass = method.getParameterTypes();
        try {
            targetMethod = ReflectionHelper.getDeclaredMethod(targetClass, method.getName(), parameterClass);
        } catch (Exception e) {
            LOGGER.warn(targetClass + " has no method(" + method.getName() + "). ");
            targetMethod = method;
        }
        List<MethodProxyHandler> methodInterceptors = getMethodProxyHandler(targetMethod);
        if (!methodInterceptors.isEmpty()) {
            methodInterceptors.sort(MethodProxyHandler::compareTo);

            this.handler.setInterceptors(methodInterceptors);
            var methodName = method.getName();

            return this.handler.proxy(methodName, () -> null, () -> method.invoke(target, args));
        }

        // no meothod proxy handler
        return method.invoke(target, args);
    }

    private List<MethodProxyHandler> getMethodProxyHandler(Method method) {
        List<MethodProxyHandler> methodProxyHandlers = new ArrayList<>();
        MethodProxyHandlerRegister methodProxyHandlerRegister = beanFactoryHandler.getMethodProxyHandlerRegister();
        Map<Class<? extends Annotation>, List<Class<? extends MethodProxyHandler>>> classListMap = methodProxyHandlerRegister.getAllMethodProxyHandlers();
        if (classListMap != null && !classListMap.isEmpty()) {
            classListMap.forEach((key, value) -> {
                getMethodProxyHandler(method, key, value, methodProxyHandlers);
            });
        }
        return methodProxyHandlers;
    }

    @SuppressWarnings("unchecked")
    private MethodProxyHandler getMethodProxyHandler(Method method, Annotation annotation) {
        MethodProxy methodProxy = (MethodProxy) annotation;
        var proxyHandler = methodProxy.proxyHandler();
        MethodProxyHandler<MethodProxy> methodProxyHandler = ReflectionHelper.newInstance(proxyHandler);
        methodProxyHandler.setMethodAnnotation(methodProxy);
        methodProxyHandler.setMethod(method);
        methodProxyHandler.setOrder(methodProxy.order());
        methodProxyHandler.setBeanFactoryHandler(beanFactoryHandler);
        return methodProxyHandler;
    }

    @SuppressWarnings("unchecked")
    private void addMethodProxyHandler(Method method, Class<? extends Annotation> annotationType,
                                       List<Class<? extends MethodProxyHandler>> proxyHandlers,
                                       Annotation annotation, List<MethodProxyHandler> methodProxyHandlers) {
        MethodProxy methodProxy = annotationType.getAnnotation(MethodProxy.class);
        if (methodProxy != null) {
            List<MethodProxyHandler> tmp = new ArrayList<>();
            for (var proxyHandler : proxyHandlers) {
                MethodProxyHandler handler = ReflectionHelper.newInstance(proxyHandler);
                handler.setOrder(methodProxy.order());
                handler.setClassAnnotation(annotation);
                handler.setBeanFactoryHandler(this.beanFactoryHandler);
                handler.setMethod(method);
                if (handler.exclusive()) {
                    methodProxyHandlers.add(handler);
                    tmp.clear();
                    break;
                } else {
                    tmp.add(handler);
                }
            }
            if (!tmp.isEmpty()) {
                methodProxyHandlers.addAll(tmp);
            }
        }
    }

    private void getMethodProxyHandler(Method method, Class<? extends Annotation> annotationClass,
                                       List<Class<? extends MethodProxyHandler>> proxyHandlers,
                                       List<MethodProxyHandler> methodProxyHandlers) {
        Annotation[] declaredAnnotations = method.getDeclaredAnnotations();
        if (declaredAnnotations != null && declaredAnnotations.length > 0) {
            for (Annotation annotation : declaredAnnotations) {
                var annotationType = annotation.annotationType();
                if (annotationType == MethodProxy.class) {
                    methodProxyHandlers.add(getMethodProxyHandler(method, annotation));
                } else if (annotationClass == annotationType) {
                    addMethodProxyHandler(method, annotationType, proxyHandlers, annotation, methodProxyHandlers);
                } else {
                    var handler = getMethodProxyHandler(annotationType, annotation, method);
                    if (handler != null) {
                        methodProxyHandlers.add(handler);
                    }
                }
            }
        } else {
            Annotation classAnnotation = classInfo.getClassAnnotation(annotationClass);
            if (classAnnotation == null) {
                classAnnotation = classInfo.getClassAnnotation(MethodProxy.class);
                if (classAnnotation != null) {
                    MethodProxy methodProxy;
                    if (classAnnotation instanceof MethodProxy) {
                        methodProxy = (MethodProxy) classAnnotation;
                    } else {
                        methodProxy = classAnnotation.annotationType().getAnnotation(MethodProxy.class);
                    }
                    var proxyHandler = methodProxy.proxyHandler();
                    @SuppressWarnings("unchecked")
                    MethodProxyHandler<MethodProxy> methodProxyHandler = ReflectionHelper.newInstance(proxyHandler);
                    methodProxyHandler.setOrder(methodProxy.order());
                    methodProxyHandler.setClassAnnotation(methodProxy);
                    methodProxyHandler.setMethod(method);
                    methodProxyHandler.setBeanFactoryHandler(beanFactoryHandler);
                    methodProxyHandlers.add(methodProxyHandler);
                } else {
                    Map<Class<? extends Annotation>, Annotation> classAnnotations = classInfo.getClassAnnotations();
                    if (classAnnotations != null && !classAnnotations.isEmpty()) {
                        for (Map.Entry<Class<? extends Annotation>, Annotation> classAnnotationEntry : classAnnotations.entrySet()) {
                            Annotation annotation = classAnnotationEntry.getValue();
                            var annotationType = classAnnotationEntry.getKey();
                            if (annotationClass == annotationType) {
                                addMethodProxyHandler(method, annotationType, proxyHandlers, annotation, methodProxyHandlers);
                            } else {
                                var handler = getMethodProxyHandler(annotationType, annotation, method);
                                if (handler != null) {
                                    methodProxyHandlers.add(handler);
                                }
                            }
                        }
                    }
                }
            } else {
                addMethodProxyHandler(method, annotationClass, proxyHandlers, classAnnotation, methodProxyHandlers);
            }
        }
    }

    @SuppressWarnings("unchecked")
    private MethodProxyHandler getMethodProxyHandler(Class<? extends Annotation> annotationType, Annotation value, Method method) {
        MethodProxy methodProxy = annotationType.getAnnotation(MethodProxy.class);
        if (methodProxy != null) {
            var proxyHandler = methodProxy.proxyHandler();
            MethodProxyHandler handler = ReflectionHelper.newInstance(proxyHandler);
            handler.setOrder(methodProxy.order());
            handler.setClassAnnotation(value);
            handler.setBeanFactoryHandler(this.beanFactoryHandler);
            handler.setMethod(method);
            return handler;
        }
        return null;
    }

}
