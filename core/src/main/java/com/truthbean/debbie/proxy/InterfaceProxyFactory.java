package com.truthbean.debbie.proxy;

import com.truthbean.debbie.reflection.ClassLoaderUtils;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author TruthBean
 * @since 0.0.1
 */
public class InterfaceProxyFactory<T> {
    private final Class<T> interfaceType;
    private final Map<Method, AbstractMethodExecutor> methodCache = new ConcurrentHashMap<>();
    private Object configuration;

    private final ClassLoader classLoader;

    public InterfaceProxyFactory(Class<T> interfaceType, Object configuration, ClassLoader classLoader) {
        this.interfaceType = interfaceType;
        this.configuration = configuration;

        if (classLoader == null)
            classLoader = ClassLoaderUtils.getClassLoader(interfaceType);
        this.classLoader = classLoader;
    }

    public Class<T> getInterface() {
        return interfaceType;
    }

    public Map<Method, AbstractMethodExecutor> getMethodCache() {
        return methodCache;
    }

    @SuppressWarnings("unchecked")
    protected T newInstance(InterfaceProxy<T> interfaceProxy) {
        return (T) Proxy.newProxyInstance(classLoader, new Class[] { interfaceType }, interfaceProxy);
    }

    public T newInstance(Object object, Class<? extends AbstractMethodExecutor> executorClass) {
        final InterfaceProxy<T> interfaceProxy =
            new InterfaceProxy<>(executorClass, object, interfaceType, methodCache, configuration);
        return newInstance(interfaceProxy);
    }
}
