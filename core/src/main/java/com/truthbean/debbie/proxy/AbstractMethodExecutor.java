/**
 * Copyright (c) 2026 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *         http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.proxy;

import java.lang.reflect.Method;

/**
 * Base executor for invoking a method on a proxied interface, typically
 * used by configuration proxies that delegate method calls to an
 * underlying configuration object.
 *
 * @author TruthBean
 * @since 0.0.1
 */
public abstract class AbstractMethodExecutor {

    /** the interface type being proxied */
    private final Class<?> interfaceType;
    /** the method to execute */
    private final Method method;
    /** the underlying configuration object */
    private final Object configuration;

    /**
     * Creates an executor for the given interface method and configuration.
     *
     * @param interfaceType the interface type being proxied
     * @param method        the method to execute
     * @param configuration the underlying configuration object
     */
    public AbstractMethodExecutor(Class<?> interfaceType, Method method, Object configuration) {
        this.interfaceType = interfaceType;
        this.method = method;
        this.configuration = configuration;
    }

    /** Returns the proxied interface type. */
    public Class<?> getInterfaceType() {
        return interfaceType;
    }

    /** Returns the method to execute. */
    public Method getMethod() {
        return method;
    }

    /** Returns the underlying configuration object. */
    public Object getConfiguration() {
        return configuration;
    }

    /**
     * execute method
     *
     * @param <T> the return type
     * @param object proxy
     * @param args method args
     * @param returnType method return type
     * @return method result
     */
    protected abstract <T> T execute(Object object, Class<T> returnType, Object...args);
}
