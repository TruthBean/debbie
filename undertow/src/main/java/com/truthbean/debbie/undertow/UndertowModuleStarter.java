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
    public void configure(DebbieConfigurationFactory configurationFactory, BeanFactoryHandler beanFactoryHandler) {
        configurationFactory.register(UndertowProperties.class, UndertowConfiguration.class);
    }

    @Override
    public int getOrder() {
        return 32;
    }

}
