package com.truthbean.debbie.httpclient;

import com.truthbean.debbie.proxy.InterfaceProxyFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * @author TruthBean
 * @since 0.0.2
 */
public class HttpClientFactory {

    private final Map<Class<?>, InterfaceProxyFactory<?>> knownInterfaces = new HashMap<>();

    private final HttpClientConfiguration httpClientConfiguration;
    private final ClassLoader classLoader;

    public HttpClientFactory(ClassLoader classLoader) {
        this.httpClientConfiguration = HttpClientProperties.toConfiguration();
        this.classLoader = classLoader;
    }

    @SuppressWarnings("unchecked")
    public <HttpClientBean> HttpClientBean factory(Class<HttpClientBean> beanClass) {
        InterfaceProxyFactory<?> interfaceProxyFactory = knownInterfaces.get(beanClass);
        if (interfaceProxyFactory == null) {
            interfaceProxyFactory = new InterfaceProxyFactory<>(beanClass, httpClientConfiguration, classLoader);
            knownInterfaces.put(beanClass, interfaceProxyFactory);
        }

        return  (HttpClientBean) interfaceProxyFactory.newInstance(this, HttpClientExecutor.class);
    }

    public void destroy() {
        knownInterfaces.clear();
    }
}
