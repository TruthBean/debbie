package com.truthbean.code.debbie.core.proxy;

import com.truthbean.code.debbie.core.reflection.ClassLoaderUtils;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author TruthBean
 * @since 0.0.1
 */
public class InterfaceProxyFactory<T, E extends AbstractMethodExecutor> {
    private final Class<T> interfaceType;
    private final Map<Method, AbstractMethodExecutor> methodCache = new ConcurrentHashMap<>();
    private Object configuration;

    public InterfaceProxyFactory(Class<T> interfaceType, Object configuration) {
        this.interfaceType = interfaceType;
        this.configuration = configuration;
    }

    public Class<T> getMapperInterface() {
        return interfaceType;
    }

    public Map<Method, AbstractMethodExecutor> getMethodCache() {
        return methodCache;
    }

    @SuppressWarnings("unchecked")
    protected T newInstance(InterfaceProxy<T> mapperProxy) {
        var classLoader = ClassLoaderUtils.getClassLoader(interfaceType);
        return (T) Proxy.newProxyInstance(classLoader, new Class[] { interfaceType }, mapperProxy);
    }

    public T newInstance(Object object, Class<? extends AbstractMethodExecutor> executorClass) {
        final InterfaceProxy<T> mapperProxy = new InterfaceProxy<>(executorClass, object, interfaceType, methodCache,
                configuration);
        return newInstance(mapperProxy);
    }
}
