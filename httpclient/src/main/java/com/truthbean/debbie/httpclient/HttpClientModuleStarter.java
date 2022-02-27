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

import com.truthbean.debbie.bean.*;
import com.truthbean.debbie.boot.DebbieModuleStarter;
import com.truthbean.debbie.core.ApplicationContext;
import com.truthbean.debbie.env.EnvironmentContent;
import com.truthbean.debbie.httpclient.annotation.HttpClientRouter;
import com.truthbean.debbie.httpclient.annotation.HttpClientRouterBeanRegister;
import com.truthbean.debbie.properties.PropertiesConfigurationBeanFactory;
import com.truthbean.debbie.proxy.BeanProxyType;

/**
 * @author TruthBean
 * @since 0.0.2
 */
public class HttpClientModuleStarter implements DebbieModuleStarter {

    private static final String ENABLE_KEY = "debbie.httpclient.enable";

    @Override
    public boolean enable(EnvironmentContent envContent) {
        return envContent.getBooleanValue(ENABLE_KEY, true);
    }

    @Override
    public void registerBean(ApplicationContext applicationContext, BeanInfoManager beanInfoManager) {
        var httpClientFactory = new HttpClientFactory(applicationContext.getClassLoader());
        var beanFactory = new SimpleBeanFactory<>(httpClientFactory, HttpClientFactory.class, BeanType.SINGLETON, BeanProxyType.NO, "httpClientFactory");
        beanInfoManager.registerBeanInfo(beanFactory);
        beanInfoManager.registerBeanAnnotation(HttpClientRouter.class, DefaultBeanComponentParser.INSTANCE);
        beanInfoManager.registerBeanRegister(new HttpClientRouterBeanRegister(httpClientFactory));

        var configurationBeanFactory = new PropertiesConfigurationBeanFactory<>(new HttpClientProperties(), HttpClientConfiguration.class);
        beanInfoManager.registerBeanInfo(configurationBeanFactory);
    }

    @Override
    public void configure(ApplicationContext applicationContext) {
    }

    @Override
    public int getOrder() {
        return 21;
    }
}
