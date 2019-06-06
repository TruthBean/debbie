package com.truthbean.debbie.httpclient;

import com.truthbean.debbie.bean.BeanFactory;

/**
 * @author TruthBean
 * @since 0.0.2
 */
public class HttpClientBeanFactory<HttpClientBean> implements BeanFactory<HttpClientBean> {

    private Class<HttpClientBean> httpClientBeanClass;
    private HttpClientFactory httpClientFactory;

    public HttpClientBeanFactory(Class<HttpClientBean> httpClientBeanClass, HttpClientFactory httpClientFactory) {
        this.httpClientBeanClass = httpClientBeanClass;
        this.httpClientFactory = httpClientFactory;
    }

    @Override
    public HttpClientBean getBean() {
        return httpClientFactory.factory(httpClientBeanClass);
    }

    @Override
    public Class<HttpClientBean> getBeanType() {
        return httpClientBeanClass;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }
}
