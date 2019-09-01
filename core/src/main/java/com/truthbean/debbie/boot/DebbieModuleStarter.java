package com.truthbean.debbie.boot;

import com.truthbean.debbie.bean.BeanFactoryHandler;
import com.truthbean.debbie.bean.BeanInitialization;
import com.truthbean.debbie.properties.DebbieConfigurationFactory;

/**
 * @author TruthBean
 * @since 0.0.2
 */
public interface DebbieModuleStarter {

    void registerBean(BeanFactoryHandler beanFactoryHandler);

    void starter(DebbieConfigurationFactory configurationFactory, BeanFactoryHandler beanFactoryHandler);
}
