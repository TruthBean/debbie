package com.truthbean.debbie.httpclient.annotation;

import com.truthbean.debbie.bean.ClassBeanInfo;
import com.truthbean.debbie.bean.BeanFactory;
import com.truthbean.debbie.bean.BeanRegister;
import com.truthbean.debbie.httpclient.HttpClientBeanFactory;
import com.truthbean.debbie.httpclient.HttpClientFactory;

/**
 * @author TruthBean
 * @since 0.5.3
 * Created on 2021/12/08 22:49.
 */
public class HttpClientRouterBeanRegister implements BeanRegister {

    private final HttpClientFactory httpClientFactory;

    public HttpClientRouterBeanRegister(HttpClientFactory httpClientFactory) {
        this.httpClientFactory = httpClientFactory;
    }

    @Override
    public <Bean> boolean support(ClassBeanInfo<Bean> beanInfo) {
        return support(beanInfo, HttpClientRouter.class);
    }

    @Override
    public <Bean> BeanFactory<Bean> getBeanFactory(ClassBeanInfo<Bean> beanInfo) {
        return new HttpClientBeanFactory<>(httpClientFactory, beanInfo);
    }

    @Override
    public int getOrder() {
        return 0;
    }
}
