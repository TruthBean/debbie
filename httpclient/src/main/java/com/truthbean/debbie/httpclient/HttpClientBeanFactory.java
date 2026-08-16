/**
 * Copyright (c) 2026 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *         http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.httpclient;

import com.truthbean.debbie.bean.ClassBeanInfo;
import com.truthbean.debbie.bean.BeanFactory;
import com.truthbean.debbie.bean.BeanType;
import com.truthbean.debbie.core.ApplicationContext;
import com.truthbean.debbie.httpclient.annotation.HttpClientRouter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * {@link BeanFactory} implementation that creates singleton HTTP client
 * beans from router-annotated interfaces via {@link HttpClientFactory}.
 *
 * @param <HttpClientBean> the bean type
 * @author TruthBean
 * @since 0.0.2
 */
public class HttpClientBeanFactory<HttpClientBean> implements BeanFactory<HttpClientBean> {

    /** the created bean instance (lazy) */
    private HttpClientBean httpClientBean;
    /** the bean class */
    private final Class<HttpClientBean> httpClientBeanClass;
    /** the underlying HTTP client factory */
    private final HttpClientFactory httpClientFactory;
    /** bean metadata */
    private final ClassBeanInfo<HttpClientBean> beanInfo;

    /** all registered bean names */
    private final Set<String> beanNames = new HashSet<>();


    /**
     * Creates a bean factory for the given bean info, registering
     * additional {@code *factory} alias names.
     *
     * @param httpClientFactory the underlying HTTP client factory
     * @param beanInfo          the bean metadata
     */
    @SuppressWarnings("unchecked")
    public HttpClientBeanFactory(HttpClientFactory httpClientFactory,
                                 ClassBeanInfo<HttpClientBean> beanInfo) {
        this.httpClientBeanClass = (Class<HttpClientBean>) beanInfo.getBeanClass();
        this.httpClientFactory = httpClientFactory;
        this.beanInfo = beanInfo;
        Set<String> allName = this.beanInfo.getAllName();
        List<String> copy = new ArrayList<>(allName);
        for (String name : copy) {
            allName.add(name + "*factory");
        }
    }

    /**
     * Creates the HTTP client bean, honouring {@link HttpClientRouter#failureAction()}
     * if present on the bean class.
     *
     * @param applicationContext the application context
     * @return the created (or cached) singleton bean
     */
    @Override
    @SuppressWarnings("unchecked")
    public HttpClientBean factoryBean(ApplicationContext applicationContext) {
        HttpClientRouter annotation = beanInfo.getClassAnnotation(HttpClientRouter.class);
        if (annotation != null) {
            Class<?> action = annotation.failureAction();
            if (httpClientBeanClass.isAssignableFrom(action)) {
                HttpClientBean o = (HttpClientBean) applicationContext.getGlobalBeanFactory().factoryByNoBean(action);
                httpClientBean = httpClientFactory.factory(httpClientBeanClass, o);
            } else if (action == Void.class) {
                httpClientBean = httpClientFactory.factory(httpClientBeanClass);
            } else {
                throw new HttpClientException("@HttpClientRouter.failureAction() is not " + httpClientBeanClass);
            }
        } else {
            httpClientBean = httpClientFactory.factory(httpClientBeanClass);
        }
        return httpClientBean;
    }

    @Override
    public Set<String> getAllName() {
        return beanNames;
    }

    @Override
    public Class<?> getBeanClass() {
        return httpClientBeanClass;
    }

    @Override
    public BeanType getBeanType() {
        return BeanType.SINGLETON;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

    @Override
    public void destruct(ApplicationContext applicationContext) {
        httpClientFactory.destroy();
    }

    @Override
    public boolean isCreated() {
        return httpClientBean != null;
    }

    @Override
    public HttpClientBean getCreatedBean() {
        return httpClientBean;
    }

    @Override
    public boolean equals(Object o) {
        return isEquals(o);
    }

    @Override
    public int hashCode() {
        return getHashCode(super.hashCode());
    }
}
