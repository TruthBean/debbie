package com.truthbean.debbie.jdbc.datasource;

import com.truthbean.debbie.bean.BeanFactoryContext;
import com.truthbean.debbie.jdbc.datasource.pool.DefaultDataSourcePoolConfiguration;
import com.truthbean.debbie.jdbc.datasource.pool.DefaultDataSourcePoolProperties;
import com.truthbean.debbie.properties.DebbieConfigurationFactory;
import com.truthbean.Logger;
import com.truthbean.logger.LoggerFactory;

import java.util.Set;

/**
 * @author TruthBean
 * @since 0.0.1
 */
public class DataSourceConfigurationFactory {

    @SuppressWarnings({"unchecked"})
    public static <Configuration extends DataSourceConfiguration> Configuration factory(
            DebbieConfigurationFactory configurationFactory, BeanFactoryContext applicationContext, Class<Configuration> configurationClass) {
        Set<Configuration> configurations = configurationFactory.getConfigurations(configurationClass, applicationContext);
        for (DataSourceConfiguration configuration : configurations) {
            LOGGER.debug("DataSourcePoolProperties : " + configurationClass);
            if (configurationClass != DefaultDataSourcePoolConfiguration.class && configurationClass != DataSourceConfiguration.class) {
                return (Configuration) configuration;
            }
        }
        return (Configuration) new DefaultDataSourcePoolProperties().toConfiguration(applicationContext);
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(DataSourceConfigurationFactory.class);
}
