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
    public void registerBean(BeanFactoryHandler beanFactoryHandler) {
        BeanInitialization beanInitialization = beanFactoryHandler.getBeanInitialization();
        beanInitialization.addAnnotationRegister(new HttpClientRouterRegister());

        DebbieConfigurationFactory configurationFactory = beanFactoryHandler.getConfigurationFactory();
        configurationFactory.register(HttpClientProperties.class);
    }

    @Override
    public void starter(DebbieConfigurationFactory configurationFactory, BeanFactoryHandler beanFactoryHandler) {
        HttpClientBeanRegister register = new HttpClientBeanRegister(beanFactoryHandler);
        register.registerHttpClient();
        register.registerHttpClientFactory();
    }

    @Override
    public int getOrder() {
        return 21;
    }

    @Override
    public void release() {

    }
}
