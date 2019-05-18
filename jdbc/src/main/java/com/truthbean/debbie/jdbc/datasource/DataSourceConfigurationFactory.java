package com.truthbean.debbie.jdbc.datasource;

import com.truthbean.debbie.core.reflection.ClassLoaderUtils;
import com.truthbean.debbie.core.spi.SpiLoader;
import com.truthbean.debbie.jdbc.datasource.pool.DataSourcePoolProperties;
import com.truthbean.debbie.jdbc.datasource.pool.DefaultDataSourcePoolProperties;

import java.util.Set;

/**
 * @author TruthBean
 * @since 0.0.1
 */
public class DataSourceConfigurationFactory {

    public static <Configuration extends DataSourceConfiguration> Configuration factory() {
        var classLoader = ClassLoaderUtils.getClassLoader(DataSourceConfigurationFactory.class);
        Set<DataSourcePoolProperties> poolPropertiesSet = SpiLoader.loadProviders(DataSourcePoolProperties.class, classLoader);
        for (DataSourcePoolProperties properties : poolPropertiesSet) {
            if (properties.getClass() != DefaultDataSourcePoolProperties.class) {
                return (Configuration) properties.loadConfiguration();
            }
        }
        return (Configuration) new DefaultDataSourcePoolProperties().loadConfiguration();
    }
}
