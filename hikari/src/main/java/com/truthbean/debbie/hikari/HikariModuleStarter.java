package com.truthbean.debbie.hikari;

import com.truthbean.debbie.bean.BeanFactoryHandler;
import com.truthbean.debbie.bean.BeanInitialization;
import com.truthbean.debbie.boot.DebbieModuleStarter;
import com.truthbean.debbie.properties.DebbieConfigurationFactory;

/**
 * @author truthbean
 * @since 0.0.2
 */
public class HikariModuleStarter implements DebbieModuleStarter {

    @Override
    public void registerBean(BeanFactoryHandler beanFactoryHandler, BeanInitialization beanInitialization) {
        beanInitialization.init(HikariConfiguration.class);
    }

    @Override
    public void configure(DebbieConfigurationFactory configurationFactory, BeanFactoryHandler beanFactoryHandler) {
        configurationFactory.register(HikariProperties.class, HikariConfiguration.class);
    }

    @Override
    public int getOrder() {
        return 22;
    }

}
