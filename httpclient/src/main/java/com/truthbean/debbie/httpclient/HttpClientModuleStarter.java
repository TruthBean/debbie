package com.truthbean.debbie.httpclient;

import com.truthbean.debbie.bean.BeanFactoryHandler;
import com.truthbean.debbie.server.DebbieModuleStarter;
import com.truthbean.debbie.properties.DebbieConfigurationFactory;

/**
 * @author TruthBean
 * @since 0.0.2
 */
public class HttpClientModuleStarter implements DebbieModuleStarter {
    @Override
    public void starter(DebbieConfigurationFactory configurationFactory, BeanFactoryHandler beanFactoryHandler) {
        HttpClientBeanRegister register = new HttpClientBeanRegister(beanFactoryHandler);
        register.registerHttpClient();
        register.registerHttpClientFactory();
    }
}
