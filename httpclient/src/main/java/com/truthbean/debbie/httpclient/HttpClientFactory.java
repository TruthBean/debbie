/**
 * Copyright (c) 2026 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 * http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.httpclient;

import com.truthbean.debbie.proxy.InterfaceProxyFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Factory for creating proxied HTTP client bean instances from
 * router-annotated interfaces, caching {@link InterfaceProxyFactory}
 * per interface type.
 *
 * @author TruthBean
 * @since 0.0.2
 */
public class HttpClientFactory {

    /** cached proxy factories keyed by interface class */
    private final Map<Class<?>, InterfaceProxyFactory<?>> knownInterfaces = new ConcurrentHashMap<>();

    /** the HTTP client configuration */
    private final HttpClientConfiguration httpClientConfiguration;
    /** class loader for proxy creation */
    private final ClassLoader classLoader;

    /**
     * Creates a factory that loads default configuration from
     * {@link HttpClientProperties}.
     *
     * @param classLoader the class loader for proxy creation
     */
    public HttpClientFactory(ClassLoader classLoader) {
        HttpClientProperties properties = new HttpClientProperties();
        this.httpClientConfiguration = properties.getDefaultConfiguration();
        this.classLoader = classLoader;
    }

    /**
     * Creates or retrieves a proxied HTTP client bean for the given
     * interface class, using the given failure action instance.
     *
     * @param beanClass    the router-annotated interface
     * @param failureAction the fallback bean instance
     * @param <HttpClientBean> the bean type
     * @return a proxied instance implementing the interface
     */
    @SuppressWarnings("unchecked")
    public <HttpClientBean> HttpClientBean factory(Class<HttpClientBean> beanClass, HttpClientBean failureAction) {
        InterfaceProxyFactory<?> interfaceProxyFactory = knownInterfaces.computeIfAbsent(beanClass, k ->
                new InterfaceProxyFactory<>(beanClass, new HttpClientProperties(), classLoader, failureAction));

        return (HttpClientBean) interfaceProxyFactory.newInstance(this, HttpClientExecutor.class);
    }

    /**
     * Creates or retrieves a proxied HTTP client bean for the given
     * interface class with no failure action.
     *
     * @param beanClass the router-annotated interface
     * @param <HttpClientBean> the bean type
     * @return a proxied instance implementing the interface
     */
    @SuppressWarnings("unchecked")
    public <HttpClientBean> HttpClientBean factory(Class<HttpClientBean> beanClass) {
        InterfaceProxyFactory<?> interfaceProxyFactory = knownInterfaces.computeIfAbsent(beanClass, k ->
                new InterfaceProxyFactory<>(beanClass, new HttpClientProperties(), classLoader, null));

        return (HttpClientBean) interfaceProxyFactory.newInstance(this, HttpClientExecutor.class);
    }

    /** Clears all cached proxy factories. */
    public void destroy() {
        knownInterfaces.clear();
    }
}
