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

import com.truthbean.debbie.reflection.ReflectionExceptionUtils;

import java.lang.invoke.MethodHandles;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Map;

/**
 * @author TruthBean
 * @since 0.0.1
 */
public class InterfaceProxy<T> implements InvocationHandler {

    private final Object object;
    private final Class<T> interfaceType;
    private final Class<? extends AbstractMethodExecutor> executorClass;
    private final Map<Method, AbstractMethodExecutor> methodCache;
    private final Object configuration;

    public InterfaceProxy(Class<? extends AbstractMethodExecutor> executorClass, Object object, Class<T> interfaceType,
                          Map<Method, AbstractMethodExecutor> methodCache, Object configuration) {
        this.executorClass = executorClass;
        this.object = object;
        this.interfaceType = interfaceType;
        this.methodCache = methodCache;
        this.configuration = configuration;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        try {
            if (Object.class.equals(method.getDeclaringClass())) {
                return method.invoke(this, args);
            } else if (isDefaultMethod(method)) {
                return invokeDefaultMethod(proxy, method, args);
            }
        } catch (Throwable t) {
            throw ReflectionExceptionUtils.unwrapThrowable(t);
        }
        Class<?> returnType = method.getReturnType();
        final AbstractMethodExecutor interfaceMethod = cachedMethodExecutor(method);
        return interfaceMethod.execute(object, returnType, args);
    }

    private AbstractMethodExecutor cachedMethodExecutor(Method method) {
        return methodCache.computeIfAbsent(method, v ->
                MethodExecutorFactory.factory(executorClass, interfaceType, method, configuration));
    }

    private Object invokeDefaultMethod(Object proxy, Method method, Object[] args)
            throws Throwable {
        final Constructor<MethodHandles.Lookup> constructor = MethodHandles.Lookup.class
                .getDeclaredConstructor(Class.class, int.class);
        if (!constructor.canAccess(null)) {
            constructor.setAccessible(true);
        }
        final Class<?> declaringClass = method.getDeclaringClass();
        return constructor
                .newInstance(declaringClass,
                        MethodHandles.Lookup.PRIVATE | MethodHandles.Lookup.PROTECTED
                                | MethodHandles.Lookup.PACKAGE | MethodHandles.Lookup.PUBLIC)
                .unreflectSpecial(method, declaringClass).bindTo(proxy).invokeWithArguments(args);
    }

    /**
     * Backport of java.lang.reflect.Method#isDefault()
     */
    private boolean isDefaultMethod(Method method) {
        return (method.getModifiers()
                & (Modifier.ABSTRACT | Modifier.PUBLIC | Modifier.STATIC)) == Modifier.PUBLIC
                && method.getDeclaringClass().isInterface();
    }
}
