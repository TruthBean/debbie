package com.truthbean.debbie.core.proxy;

import com.truthbean.debbie.core.reflection.ReflectionExceptionUtils;

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
        final AbstractMethodExecutor mapperMethod = cachedMethodExecutor(method);
        return mapperMethod.execute(object, args);
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
