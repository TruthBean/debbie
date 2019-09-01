package com.truthbean.debbie.core;

import com.truthbean.debbie.bean.BeanFactoryHandler;
import com.truthbean.debbie.bean.BeanInitialization;
import com.truthbean.debbie.boot.DebbieModuleStarter;
import com.truthbean.debbie.properties.DebbieConfigurationFactory;
import com.truthbean.debbie.properties.PropertiesConfigurationRegister;

public class DebbieCoreModuleStarter implements DebbieModuleStarter {
    @Override
    public void registerBean(BeanInitialization beanInitialization) {
    }

    @Override
    public void starter(DebbieConfigurationFactory configurationFactory, BeanFactoryHandler beanFactoryHandler) {
        BeanInitialization beanInitialization = beanFactoryHandler.getBeanInitialization();
        beanInitialization.addAnnotationRegister(new PropertiesConfigurationRegister());
        beanFactoryHandler.refreshBeans();
    }
}
