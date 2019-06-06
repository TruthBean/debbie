package com.truthbean.debbie.jdbc.datasource;

import com.truthbean.debbie.bean.BeanFactoryHandler;
import com.truthbean.debbie.bean.BeanInitialization;
import com.truthbean.debbie.bean.SingletonBeanRegister;
import com.truthbean.debbie.properties.DebbieConfigurationFactory;

/**
 * @author TruthBean
 * @since 0.0.2
 */
public class DataSourceFactoryBeanRegister extends SingletonBeanRegister {

    private BeanFactoryHandler beanFactoryHandler;
    private BeanInitialization initialization;
    private DebbieConfigurationFactory configurationFactory;

    public DataSourceFactoryBeanRegister(DebbieConfigurationFactory configurationFactory, BeanFactoryHandler beanFactoryHandler) {
        super(beanFactoryHandler);
        this.beanFactoryHandler = beanFactoryHandler;
        this.initialization = beanFactoryHandler.getBeanInitialization();
        this.configurationFactory = configurationFactory;
    }

    public void registerDataSourceFactory() {
        DataSourceFactory dataSourceFactory = initialization.getRegisterBean(DataSourceFactory.class);
        if (dataSourceFactory == null) {
            dataSourceFactory = DataSourceFactory.factory(configurationFactory, beanFactoryHandler);
            registerSingletonBean(dataSourceFactory, DataSourceFactory.class, "dataSourceFactory");
        }
    }
}
