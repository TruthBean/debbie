package com.truthbean.debbie.hikari;

import com.truthbean.debbie.bean.BeanFactoryHandler;
import com.truthbean.debbie.bean.BeanInitialization;
import com.truthbean.debbie.boot.DebbieModuleStarter;
import com.truthbean.debbie.properties.DebbieConfigurationFactory;

public class HikariModuleStarter implements DebbieModuleStarter {

    @Override
    public void registerBean(BeanInitialization beanInitialization) {
        beanInitialization.init(HikariConfiguration.class);
    }

    @Override
    public void starter(DebbieConfigurationFactory configurationFactory, BeanFactoryHandler beanFactoryHandler) {
    }
}
