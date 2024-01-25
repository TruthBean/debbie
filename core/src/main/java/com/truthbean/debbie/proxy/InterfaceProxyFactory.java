/**
 * Copyright (c) 2024 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *         http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
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
    private final Object configuration;

    private final ClassLoader classLoader;
    private final T failureAction;

    public InterfaceProxyFactory(Class<T> interfaceType, Object configuration, ClassLoader classLoader, T failureAction) {
        this.interfaceType = interfaceType;
        this.configuration = configuration;

        if (classLoader == null)
            classLoader = ClassLoaderUtils.getClassLoader(interfaceType);
        this.classLoader = classLoader;
        this.failureAction = failureAction;
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
            new InterfaceProxy<>(executorClass, object, interfaceType, classLoader, methodCache, configuration, failureAction);
        return newInstance(interfaceProxy);
    }
}
