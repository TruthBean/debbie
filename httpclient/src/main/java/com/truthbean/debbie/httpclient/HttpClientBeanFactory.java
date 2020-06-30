/**
 * Copyright (c) 2020 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *         http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.httpclient;

import com.truthbean.debbie.bean.BeanFactory;
import com.truthbean.debbie.bean.BeanFactoryContext;

/**
 * @author TruthBean
 * @since 0.0.2
 */
public class HttpClientBeanFactory<HttpClientBean> implements BeanFactory<HttpClientBean> {

    private final Class<HttpClientBean> httpClientBeanClass;
    private final HttpClientFactory httpClientFactory;
    private BeanFactoryContext applicationContext;

    public HttpClientBeanFactory(Class<HttpClientBean> httpClientBeanClass, HttpClientFactory httpClientFactory) {
        this.httpClientBeanClass = httpClientBeanClass;
        this.httpClientFactory = httpClientFactory;
    }

    @Override
    public void setBeanFactoryContext(BeanFactoryContext applicationContext) {
        this.applicationContext = applicationContext;
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
