package com.truthbean.debbie.undertow;

import com.truthbean.debbie.bean.BeanFactoryHandler;
import com.truthbean.debbie.boot.DebbieModuleStarter;
import com.truthbean.debbie.properties.DebbieConfigurationFactory;

/**
 * @author truthbean
 * @since 0.0.2
 */
public class UndertowModuleStarter implements DebbieModuleStarter {
    @Override
    public void registerBean(BeanFactoryHandler beanFactoryHandler) {
        DebbieConfigurationFactory configurationFactory = beanFactoryHandler.getConfigurationFactory();
        configurationFactory.register(UndertowProperties.class);
    }

    @Override
    public void starter(DebbieConfigurationFactory configurationFactory, BeanFactoryHandler beanFactoryHandler) {

    }

    @Override
    public int getOrder() {
        return 32;
    }

    @Override
    public void release() {

    }
}
