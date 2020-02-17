package com.truthbean.debbie.aio;

import com.truthbean.debbie.bean.BeanFactoryHandler;
import com.truthbean.debbie.boot.DebbieModuleStarter;
import com.truthbean.debbie.properties.DebbieConfigurationFactory;

/**
 * @author TruthBean/Rogar·Q
 * @since 0.0.2
 * Created on 2019-12-17 20:35
 */
public class AioModuleStarter implements DebbieModuleStarter {
    @Override
    public void registerBean(BeanFactoryHandler beanFactoryHandler) {
        DebbieConfigurationFactory configurationFactory = beanFactoryHandler.getConfigurationFactory();
        configurationFactory.register(AioServerProperties.class);
    }

    @Override
    public void starter(DebbieConfigurationFactory configurationFactory, BeanFactoryHandler beanFactoryHandler) {

    }

    @Override
    public int getOrder() {
        return 35;
    }

    @Override
    public void release() {

    }
}
