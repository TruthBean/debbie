package com.truthbean.debbie.jdbc.datasource;

import com.truthbean.debbie.bean.BeanFactoryContext;
import com.truthbean.debbie.bean.BeanInitialization;
import com.truthbean.debbie.bean.DebbieBeanInfo;
import com.truthbean.debbie.bean.SingletonBeanRegister;
import com.truthbean.debbie.properties.DebbieConfigurationFactory;
import com.truthbean.Logger;
import com.truthbean.logger.LoggerFactory;

/**
 * @author TruthBean
 * @since 0.0.2
 */
@SuppressWarnings({"unchecked"})
public class DataSourceFactoryBeanRegister extends SingletonBeanRegister {

    private BeanFactoryContext applicationContext;
    private BeanInitialization initialization;
    private DebbieConfigurationFactory configurationFactory;

    private final Logger logger = LoggerFactory.getLogger(DataSourceFactoryBeanRegister.class);

    public DataSourceFactoryBeanRegister(DebbieConfigurationFactory configurationFactory, BeanFactoryContext applicationContext) {
        super(applicationContext);
        this.applicationContext = applicationContext;
        this.initialization = applicationContext.getBeanInitialization();
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
            DataSourceFactory dataSourceFactory = DataSourceFactory.factory(configurationFactory, applicationContext, configurationClass);
            registerSingletonBean(dataSourceFactory, DataSourceFactory.class, "dataSourceFactory");
        } else if (dataSourceFactoryBeanInfo.isEmpty()) {
            DataSourceFactory dataSourceFactory = DataSourceFactory.factory(configurationFactory, applicationContext, configurationClass);
            dataSourceFactoryBeanInfo.setBean(dataSourceFactory);
            dataSourceFactoryBeanInfo.setBeanName("dataSourceFactory");
            registerSingletonBean(dataSourceFactoryBeanInfo);
        }
    }
}
