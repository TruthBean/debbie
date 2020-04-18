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
    public void configure(DebbieConfigurationFactory configurationFactory, BeanFactoryHandler beanFactoryHandler) {
        configurationFactory.register(TomcatProperties.class, TomcatConfiguration.class);
    }

    @Override
    public int getOrder() {
        return 30;
    }

}
