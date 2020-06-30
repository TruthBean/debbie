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

import com.truthbean.debbie.bean.BeanFactoryContext;
import com.truthbean.debbie.bean.BeanInitialization;
import com.truthbean.debbie.boot.DebbieModuleStarter;
import com.truthbean.debbie.httpclient.annotation.HttpClientRouterRegister;
import com.truthbean.debbie.properties.DebbieConfigurationFactory;

/**
 * @author TruthBean
 * @since 0.0.2
 */
public class HttpClientModuleStarter implements DebbieModuleStarter {

    @Override
    public void registerBean(BeanFactoryContext applicationContext, BeanInitialization beanInitialization) {
        beanInitialization.addAnnotationRegister(new HttpClientRouterRegister(beanInitialization));
    }

    @Override
    public void configure(DebbieConfigurationFactory configurationFactory, BeanFactoryContext applicationContext) {
        configurationFactory.register(HttpClientProperties.class, HttpClientConfiguration.class);

        HttpClientBeanRegister register = new HttpClientBeanRegister(applicationContext);
        register.registerHttpClient();
        register.registerHttpClientFactory();
    }

    @Override
    public int getOrder() {
        return 21;
    }
}
