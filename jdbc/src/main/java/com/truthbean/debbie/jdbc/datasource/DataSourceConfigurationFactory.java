package com.truthbean.debbie.jdbc.datasource;

import com.truthbean.debbie.core.reflection.ClassLoaderUtils;
import com.truthbean.debbie.core.spi.SpiLoader;
import com.truthbean.debbie.jdbc.datasource.pool.DataSourcePoolProperties;

/**
 * @author TruthBean
 * @since 0.0.1
 */
public class DataSourceConfigurationFactory {

    public static <Configuration extends DataSourceConfiguration> Configuration factory() {
        var classLoader = ClassLoaderUtils.getClassLoader(DataSourceConfigurationFactory.class);
        DataSourcePoolProperties poolProperties = SpiLoader.loadProvider(DataSourcePoolProperties.class, classLoader);
        return (Configuration) poolProperties.loadConfiguration();
    }
}
