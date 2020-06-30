/**
 * Copyright (c) 2020 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *         http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.proxy;

import com.truthbean.debbie.bean.BeanFactoryContext;
import com.truthbean.debbie.bean.DebbieBeanInfo;
import com.truthbean.debbie.reflection.ReflectionHelper;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.*;

/**
 * @author truthbean/Rogar·Q
 * @since 0.0.2
 * Created on 2020-04-26 18:12.
 */
public class MethodProxyResolver {
    private final BeanFactoryContext applicationContext;
    private final DebbieBeanInfo<?> classInfo;
    public MethodProxyResolver(BeanFactoryContext applicationContext, DebbieBeanInfo<?> classInfo) {
        this.applicationContext = applicationContext;
        this.classInfo = classInfo;
    }

    public List<MethodProxyHandler<? extends Annotation>> getMethodProxyHandler(Method method) {
        List<MethodProxyHandler<? extends Annotation>> methodProxyHandlers = new ArrayList<>();
        MethodProxyHandlerRegister methodProxyHandlerRegister = applicationContext.getMethodProxyHandlerRegister();
        Map<Class<? extends Annotation>, List<Class<? extends MethodProxyHandler<? extends Annotation>>>> classListMap = methodProxyHandlerRegister.getAllMethodProxyHandlers();
        if (classListMap != null && !classListMap.isEmpty()) {
            classListMap.forEach((key, value) -> {
                getMethodProxyHandler(method, key, value, methodProxyHandlers);
            });
        }
        return methodProxyHandlers;
    }

    public List<MethodProxyHandler<? extends Annotation>> getMethodProxyHandler(Method method,
                                                                                Collection<Annotation> annotations) {
        List<MethodProxyHandler<? extends Annotation>> methodProxyHandlers = new ArrayList<>();
        MethodProxyHandlerRegister methodProxyHandlerRegister = applicationContext.getMethodProxyHandlerRegister();
        Map<Class<? extends Annotation>, List<Class<? extends MethodProxyHandler<? extends Annotation>>>> classListMap
                = methodProxyHandlerRegister.getAllMethodProxyHandlers();
        if (classListMap != null && !classListMap.isEmpty()) {
            classListMap.forEach((key, value) -> {
                getMethodProxyHandler(method, key, value, methodProxyHandlers, annotations);
            });
        }
        return methodProxyHandlers;
    }

    @SuppressWarnings("unchecked")
    public MethodProxyHandler<MethodProxy> getMethodProxyHandler(Method method, MethodProxy methodProxy) {
        var proxyHandler = methodProxy.proxyHandler();
        MethodProxyHandler<MethodProxy> methodProxyHandler = ReflectionHelper.newInstance(proxyHandler);
        methodProxyHandler.setMethodAnnotation(methodProxy);
        methodProxyHandler.setMethod(method);
        methodProxyHandler.setOrder(methodProxy.order());
        methodProxyHandler.setApplicationContext(applicationContext);
        return methodProxyHandler;
    }

    @SuppressWarnings("unchecked")
    private void addMethodProxyHandler(Method method, Class<? extends Annotation> annotationType,
                                       List<Class<? extends MethodProxyHandler<? extends Annotation>>> proxyHandlers,
                                       Annotation annotation, List<MethodProxyHandler<? extends Annotation>> methodProxyHandlers) {
        MethodProxy methodProxy = annotationType.getAnnotation(MethodProxy.class);
        if (methodProxy != null) {
            List<MethodProxyHandler<? extends Annotation>> tmp = new ArrayList<>();
            for (var proxyHandler : proxyHandlers) {
                MethodProxyHandler handler = ReflectionHelper.newInstance(proxyHandler);
                handler.setOrder(methodProxy.order());
                handler.setClassAnnotation(annotation);
                handler.setApplicationContext(this.applicationContext);
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
                                       List<Class<? extends MethodProxyHandler<? extends Annotation>>> proxyHandlers,
                                       List<MethodProxyHandler<? extends Annotation>> methodProxyHandlers,
                                       Collection<? extends Annotation> declaredAnnotations) {
        if (declaredAnnotations != null && !declaredAnnotations.isEmpty()) {
            for (Annotation annotation : declaredAnnotations) {
                var annotationType = annotation.annotationType();
                if (annotationType == MethodProxy.class) {
                    methodProxyHandlers.add(getMethodProxyHandler(method, (MethodProxy) annotation));
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
                    methodProxyHandler.setApplicationContext(applicationContext);
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

    private void getMethodProxyHandler(Method method, Class<? extends Annotation> annotationClass,
                                       List<Class<? extends MethodProxyHandler<? extends Annotation>>> proxyHandlers,
                                       List<MethodProxyHandler<? extends Annotation>> methodProxyHandlers) {
        if (method == null) return;
        Annotation[] declaredAnnotations = method.getDeclaredAnnotations();
        getMethodProxyHandler(method, annotationClass, proxyHandlers, methodProxyHandlers, Arrays.asList(declaredAnnotations));
    }

    @SuppressWarnings("unchecked")
    public MethodProxyHandler<? extends Annotation> getMethodProxyHandler(Class<? extends Annotation> annotationType,
                                                                          Annotation value, Method method) {
        MethodProxy methodProxy = annotationType.getAnnotation(MethodProxy.class);
        if (methodProxy != null) {
            var proxyHandler = methodProxy.proxyHandler();
            MethodProxyHandler<Annotation> handler = ReflectionHelper.newInstance(proxyHandler);
            handler.setOrder(methodProxy.order());
            handler.setClassAnnotation(value);
            handler.setApplicationContext(this.applicationContext);
            handler.setMethod(method);
            return handler;
        }
        return null;
    }
}
