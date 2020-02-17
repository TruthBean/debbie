package com.truthbean.debbie.tomcat;

import com.truthbean.debbie.bean.BeanFactoryHandler;
import com.truthbean.debbie.boot.DebbieModuleStarter;
import com.truthbean.debbie.properties.DebbieConfigurationFactory;

/**
 * @author truthbean
 * @since 0.0.2
 */
public class TomcatModuleStarter implements DebbieModuleStarter {
    @Override
    public void registerBean(BeanFactoryHandler beanFactoryHandler) {
        DebbieConfigurationFactory configurationFactory = beanFactoryHandler.getConfigurationFactory();
        configurationFactory.register(TomcatProperties.class);
    }

    @Override
    public void starter(DebbieConfigurationFactory configurationFactory, BeanFactoryHandler beanFactoryHandler) {

    }

    @Override
    public int getOrder() {
        return 30;
    }

    @Override
    public void release() {

    }
}
