/**
 * Copyright (c) 2020 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *         http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.proxy.asm;

import com.truthbean.debbie.proxy.MethodProxyHandlerHandler;
import com.truthbean.debbie.reflection.ClassInfo;
import com.truthbean.debbie.reflection.asm.AsmClassInfo;
import com.truthbean.debbie.reflection.asm.AsmConstructorInfo;
import com.truthbean.debbie.reflection.asm.AsmMethodInfo;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * @author truthbean/Rogar·Q
 * @since 0.0.2
 * Created on 2020-04-21 18:11.
 */
public abstract class AbstractProxy<B> {

    private final MethodProxyHandlerHandler handler;

    private final ClassInfo<B> beanClassInfo;
    private final Class<MethodProxyHandlerHandler> handlerClass;
    private final Class<? extends Annotation> methodAnnotation;

    private final AsmClassInfo asmClassInfo;
    private final ClassLoader classLoader;

    protected static final String JAVA_LANG_OBJECT = "java/lang/Object";
    protected static final String NAME_CTOR = "<init>";

    public AbstractProxy(ClassInfo<B> beanClassInfo, ClassLoader classLoader, MethodProxyHandlerHandler handler,
                         Class<? extends Annotation> methodAnnotation) {
        this.handler = handler;

        this.beanClassInfo = beanClassInfo;
        this.handlerClass = MethodProxyHandlerHandler.class;

        this.classLoader = classLoader;

        this.methodAnnotation = methodAnnotation;

        this.asmClassInfo = new AsmClassInfo(beanClassInfo.getClazz());
    }

    public AbstractProxy(Class<B> beanClass, ClassLoader classLoader, MethodProxyHandlerHandler handler,
                          Class<? extends Annotation> methodAnnotation) {
        this.handler = handler;

        this.beanClassInfo = new ClassInfo<>(beanClass);
        this.handlerClass = MethodProxyHandlerHandler.class;

        this.classLoader = classLoader;

        this.methodAnnotation = methodAnnotation;

        this.asmClassInfo = new AsmClassInfo(beanClass);
    }

    public ClassLoader getClassLoader() {
        return classLoader;
    }

    public Set<Method> getMethods() {
        return beanClassInfo.getMethods();
    }

    public Constructor<B>[] getConstructors() {
        return beanClassInfo.getConstructors();
    }

    public List<AsmConstructorInfo> getConstructorInfoList() {
        Constructor<B>[] constructors = getConstructors();
        List<AsmConstructorInfo> constructorInfoArray = new ArrayList<>();
        for (Constructor<B> constructor : constructors) {
            constructorInfoArray.add(new AsmConstructorInfo(constructor));
        }
        return constructorInfoArray;
    }

    public Set<AsmMethodInfo> getMethodInfoList() {
        Set<Method> declaredMethods = getMethods();
        return declaredMethods.stream()
                .map(AsmMethodInfo::new)
                .collect(Collectors.toSet());
    }

    public Set<Method> getProxyMethods() {
        Set<Method> declaredMethods = getMethods();
        return declaredMethods.stream()
                .filter(method ->
                        beanClassInfo.containMethodAnnotation(methodAnnotation, method)
                                || beanClassInfo.containClassAnnotation(methodAnnotation))
                .collect(Collectors.toSet());
    }

    public Set<AsmMethodInfo> getProxyMethodInfo() {
        Set<Method> declaredMethods = getProxyMethods();
        return declaredMethods.stream()
                .map(AsmMethodInfo::new)
                .collect(Collectors.toSet());
    }

    public Class<B> getBeanClass() {
        return beanClassInfo.getClazz();
    }

    public AsmClassInfo getAsmClassInfo() {
        return asmClassInfo;
    }

    public MethodProxyHandlerHandler getHandler() {
        return handler;
    }

    public Class<MethodProxyHandlerHandler> getHandlerClass() {
        return handlerClass;
    }

    public boolean isInterface() {
        return getBeanClass().isInterface();
    }

    public Class<? extends Annotation> getMethodAnnotation() {
        return methodAnnotation;
    }

    public boolean isAnnotatedMethod(Method method) {
        return beanClassInfo.containMethodAnnotation(methodAnnotation, method)
                || beanClassInfo.containClassAnnotation(methodAnnotation);
    }

    public boolean isAnnotatedMethod(AsmMethodInfo method) {
        return beanClassInfo.containMethodAnnotation(methodAnnotation, method.getMethod())
                || beanClassInfo.containClassAnnotation(methodAnnotation);
    }

    public B proxy(B bean) {
        return proxy(() -> bean);
    }

    public abstract B proxy(Supplier<B> bean);
}
