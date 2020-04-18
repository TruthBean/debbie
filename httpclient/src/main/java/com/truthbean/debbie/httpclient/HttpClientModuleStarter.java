package com.truthbean.debbie.httpclient;

import com.truthbean.debbie.bean.BeanFactoryHandler;
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
    public void registerBean(BeanFactoryHandler beanFactoryHandler, BeanInitialization beanInitialization) {
        beanInitialization.addAnnotationRegister(new HttpClientRouterRegister(beanInitialization));
    }

    @Override
    public void configure(DebbieConfigurationFactory configurationFactory, BeanFactoryHandler beanFactoryHandler) {
        configurationFactory.register(HttpClientProperties.class, HttpClientConfiguration.class);

        HttpClientBeanRegister register = new HttpClientBeanRegister(beanFactoryHandler);
        register.registerHttpClient();
        register.registerHttpClientFactory();
    }

    @Override
    public int getOrder() {
        return 21;
    }
}
