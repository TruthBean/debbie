package com.truthbean.debbie.jdbc;

import com.truthbean.debbie.bean.BeanFactoryHandler;
import com.truthbean.debbie.server.DebbieModuleStarter;
import com.truthbean.debbie.jdbc.datasource.DataSourceFactoryBeanRegister;
import com.truthbean.debbie.properties.DebbieConfigurationFactory;

/**
 * @author TruthBean
 * @since 0.0.2
 */
public class JdbcModuleStarter implements DebbieModuleStarter {
    @Override
    public void starter(DebbieConfigurationFactory configurationFactory, BeanFactoryHandler beanFactoryHandler) {
        var register = new DataSourceFactoryBeanRegister(configurationFactory, beanFactoryHandler);
        register.registerDataSourceFactory();
        beanFactoryHandler.refreshBeans();
    }
}
