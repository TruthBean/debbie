package com.truthbean.debbie.httpclient;

import com.truthbean.debbie.bean.BeanFactory;
import com.truthbean.debbie.bean.BeanFactoryHandler;

/**
 * @author TruthBean
 * @since 0.0.2
 */
public class HttpClientBeanFactory<HttpClientBean> implements BeanFactory<HttpClientBean> {

    private final Class<HttpClientBean> httpClientBeanClass;
    private final HttpClientFactory httpClientFactory;
    private BeanFactoryHandler beanFactoryHandler;

    public HttpClientBeanFactory(Class<HttpClientBean> httpClientBeanClass, HttpClientFactory httpClientFactory) {
        this.httpClientBeanClass = httpClientBeanClass;
        this.httpClientFactory = httpClientFactory;
    }

    @Override
    public void setBeanFactoryHandler(BeanFactoryHandler beanFactoryHandler) {
        this.beanFactoryHandler = beanFactoryHandler;
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

    @Override
    public void destroy() {
        httpClientFactory.destroy();
    }

}
