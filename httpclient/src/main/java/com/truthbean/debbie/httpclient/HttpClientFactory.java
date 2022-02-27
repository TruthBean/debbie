/**
 * Copyright (c) 2022 TruthBean(Rogar·Q)
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
 * @author TruthBean
 * @since 0.0.2
 */
public class HttpClientFactory {

    private final Map<Class<?>, InterfaceProxyFactory<?>> knownInterfaces = new ConcurrentHashMap<>();

    private final HttpClientConfiguration httpClientConfiguration;
    private final ClassLoader classLoader;

    public HttpClientFactory(ClassLoader classLoader) {
        this.httpClientConfiguration = HttpClientProperties.toConfiguration();
        this.classLoader = classLoader;
    }

    @SuppressWarnings("unchecked")
    public <HttpClientBean> HttpClientBean factory(Class<HttpClientBean> beanClass, HttpClientBean failureAction) {
        InterfaceProxyFactory<?> interfaceProxyFactory = knownInterfaces.computeIfAbsent(beanClass, k ->
                new InterfaceProxyFactory<>(beanClass, new HttpClientProperties(), classLoader, failureAction));

        return (HttpClientBean) interfaceProxyFactory.newInstance(this, HttpClientExecutor.class);
    }

    @SuppressWarnings("unchecked")
    public <HttpClientBean> HttpClientBean factory(Class<HttpClientBean> beanClass) {
        InterfaceProxyFactory<?> interfaceProxyFactory = knownInterfaces.computeIfAbsent(beanClass, k ->
                new InterfaceProxyFactory<>(beanClass, new HttpClientProperties(), classLoader, null));

        return (HttpClientBean) interfaceProxyFactory.newInstance(this, HttpClientExecutor.class);
    }

    public void destroy() {
        knownInterfaces.clear();
    }
}
