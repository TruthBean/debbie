package com.truthbean.debbie.servlet;

import com.truthbean.debbie.bean.BeanFactoryHandler;
import com.truthbean.debbie.boot.DebbieModuleStarter;
import com.truthbean.debbie.properties.DebbieConfigurationFactory;

/**
 * @author truthbean
 * @since 0.0.2
 */
public class ServletModuleStarter implements DebbieModuleStarter {

    @Override
    public void configure(DebbieConfigurationFactory configurationFactory, BeanFactoryHandler beanFactoryHandler) {
        configurationFactory.register(ServletProperties .class, ServletConfiguration.class);
    }

    @Override
    public int getOrder() {
        return 20;
    }
}
