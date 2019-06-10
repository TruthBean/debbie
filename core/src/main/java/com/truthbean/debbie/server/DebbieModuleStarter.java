package com.truthbean.debbie.server;

import com.truthbean.debbie.bean.BeanFactoryHandler;
import com.truthbean.debbie.properties.DebbieConfigurationFactory;

/**
 * @author TruthBean
 * @since 0.0.2
 */
public interface DebbieModuleStarter {

    void starter(DebbieConfigurationFactory configurationFactory, BeanFactoryHandler beanFactoryHandler);
}
