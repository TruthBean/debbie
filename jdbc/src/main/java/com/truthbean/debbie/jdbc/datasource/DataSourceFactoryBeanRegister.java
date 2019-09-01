package com.truthbean.debbie.jdbc.datasource;

import com.truthbean.debbie.bean.BeanFactoryHandler;
import com.truthbean.debbie.bean.BeanInitialization;
import com.truthbean.debbie.bean.DebbieBeanInfo;
import com.truthbean.debbie.bean.SingletonBeanRegister;
import com.truthbean.debbie.properties.DebbieConfigurationFactory;
import com.truthbean.debbie.reflection.ClassLoaderUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author TruthBean
 * @since 0.0.2
 */
public class DataSourceFactoryBeanRegister extends SingletonBeanRegister {

    private BeanFactoryHandler beanFactoryHandler;
    private BeanInitialization initialization;
    private DebbieConfigurationFactory configurationFactory;

    private final Logger logger = LoggerFactory.getLogger(DataSourceFactoryBeanRegister.class);

    public DataSourceFactoryBeanRegister(DebbieConfigurationFactory configurationFactory, BeanFactoryHandler beanFactoryHandler) {
        super(beanFactoryHandler);
        this.beanFactoryHandler = beanFactoryHandler;
        this.initialization = beanFactoryHandler.getBeanInitialization();
        this.configurationFactory = configurationFactory;
    }

    public void registerDataSourceFactory() {
        DebbieBeanInfo<DataSourceFactory> dataSourceFactoryBeanInfo = initialization.getRegisterRawBean(DataSourceFactory.class);
        Class<? extends DataSourceConfiguration> configurationClass = DataSourceConfiguration.class;
        try {
            configurationClass = (Class<? extends DataSourceConfiguration>) Class.forName("com.truthbean.debbie.hikari.HikariConfiguration");
        } catch (ClassNotFoundException e) {
            logger.info("com.truthbean.debbie:debbie-hikari jar not be depended. ");
        }
        if (dataSourceFactoryBeanInfo == null) {
            DataSourceFactory dataSourceFactory = DataSourceFactory.factory(configurationFactory, beanFactoryHandler, configurationClass);
            registerSingletonBean(dataSourceFactory, DataSourceFactory.class, "dataSourceFactory");
        } else if (dataSourceFactoryBeanInfo.getBean() == null) {
            DataSourceFactory dataSourceFactory = DataSourceFactory.factory(configurationFactory, beanFactoryHandler, configurationClass);
            dataSourceFactoryBeanInfo.setBean(dataSourceFactory);
            dataSourceFactoryBeanInfo.setBeanName("dataSourceFactory");
            registerSingletonBean(dataSourceFactoryBeanInfo);
        }
    }
}
