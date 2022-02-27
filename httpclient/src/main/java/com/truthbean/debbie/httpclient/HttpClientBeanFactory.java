/**
 * Copyright (c) 2022 TruthBean(Rogar·Q)
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
import com.truthbean.debbie.reflection.ReflectionHelper;

/**
 * @author TruthBean
 * @since 0.0.2
 */
public class HttpClientBeanFactory<HttpClientBean> implements BeanFactory<HttpClientBean> {

    private HttpClientBean httpClientBean;
    private final Class<HttpClientBean> httpClientBeanClass;
    private final HttpClientFactory httpClientFactory;
    private final ClassBeanInfo<HttpClientBean> beanInfo;

    @SuppressWarnings("unchecked")
    public HttpClientBeanFactory(HttpClientFactory httpClientFactory,
                                 ClassBeanInfo<HttpClientBean> beanInfo) {
        this.httpClientBeanClass = (Class<HttpClientBean>) beanInfo.getBeanClass();
        this.httpClientFactory = httpClientFactory;
        this.beanInfo = beanInfo;
    }

    @Override
    @SuppressWarnings("unchecked")
    public HttpClientBean factoryNamedBean(String name, ApplicationContext applicationContext) {
        HttpClientRouter annotation = beanInfo.getClassAnnotation(HttpClientRouter.class);
        if (annotation != null) {
            Class<?> action = annotation.failureAction();
            if (httpClientBeanClass.isAssignableFrom(action)) {
                HttpClientBean o = (HttpClientBean) ReflectionHelper.newInstance(action);
                httpClientBean = httpClientFactory.factory(httpClientBeanClass, o);
            }
        } else {
            httpClientBean = httpClientFactory.factory(httpClientBeanClass);
        }
        return httpClientBean;
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
