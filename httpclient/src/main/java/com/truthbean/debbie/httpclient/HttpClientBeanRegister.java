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

import com.truthbean.debbie.bean.BeanInitialization;
import com.truthbean.debbie.bean.DebbieBeanInfo;
import com.truthbean.debbie.bean.SingletonBeanRegister;
import com.truthbean.debbie.core.ApplicationContext;
import com.truthbean.debbie.httpclient.annotation.HttpClientRouter;

import java.util.Set;

/**
 * @author TruthBean
 * @since 0.0.2
 */
public class HttpClientBeanRegister extends SingletonBeanRegister {
    private final HttpClientFactory handler;
    private final ApplicationContext applicationContext;
    private final BeanInitialization initialization;

    public HttpClientBeanRegister(ApplicationContext applicationContext) {
        super(applicationContext);
        handler = new HttpClientFactory(applicationContext.getClassLoader());
        this.applicationContext = applicationContext;
        initialization = applicationContext.getBeanInitialization();
    }

    public void registerHttpClient() {
        Set<DebbieBeanInfo<?>> annotatedClass = initialization.getAnnotatedClass(HttpClientRouter.class);
        if (annotatedClass != null && !annotatedClass.isEmpty()) {
            for (DebbieBeanInfo<?> httpClientRouter : annotatedClass) {
                httpClientRouter.setBeanFactory(new HttpClientBeanFactory<>(httpClientRouter.getBeanClass(), handler));
                initialization.refreshBean(httpClientRouter);
                applicationContext.refreshBeans();
            }
        }
    }

    public void registerHttpClientFactory() {
        DebbieBeanInfo<HttpClientFactory> httpClientFactoryBeanInfo = initialization.getRegisterRawBean(HttpClientFactory.class);
        if (httpClientFactoryBeanInfo == null) {
            registerSingletonBean(handler, HttpClientFactory.class, "httpClientFactory");
        } else if (httpClientFactoryBeanInfo.isEmpty()) {
            httpClientFactoryBeanInfo.addBeanName("httpClientFactory");
            httpClientFactoryBeanInfo.setBean(handler);
            registerSingletonBean(httpClientFactoryBeanInfo);
        }
    }
}
