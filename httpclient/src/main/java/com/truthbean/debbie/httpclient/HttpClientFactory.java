package com.truthbean.debbie.httpclient;

import com.truthbean.debbie.proxy.InterfaceProxyFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * @author TruthBean
 * @since 0.0.2
 */
public class HttpClientFactory {

    private final Map<Class<?>, InterfaceProxyFactory<?>> knownMappers = new HashMap<>();

    private HttpClientConfiguration httpClientConfiguration;
    public HttpClientFactory() {
        this.httpClientConfiguration = HttpClientProperties.toConfiguration();
    }

    public <HttpClientBean> HttpClientBean factory(Class<HttpClientBean> beanClass) {
        InterfaceProxyFactory<?> interfaceProxyFactory = knownMappers.get(beanClass);
        if (interfaceProxyFactory == null) {
            interfaceProxyFactory = new InterfaceProxyFactory<>(beanClass, httpClientConfiguration);
            knownMappers.put(beanClass, interfaceProxyFactory);
        }

        return  (HttpClientBean) interfaceProxyFactory.newInstance(this, HttpClientExecutor.class);
    }
}
