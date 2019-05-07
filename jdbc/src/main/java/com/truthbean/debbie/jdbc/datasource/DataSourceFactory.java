package com.truthbean.debbie.jdbc.datasource;

import com.truthbean.debbie.core.reflection.ReflectionHelper;

import javax.sql.DataSource;
import java.sql.Connection;

/**
 * @author TruthBean
 * @since 0.0.1
 */
public interface DataSourceFactory {

    static DataSourceFactory defaultFactory() {
        var config = DataSourceProperties.toConfiguration();
        return loadFactory(config);
    }

    static <Configuration extends DataSourceConfiguration> DataSourceFactory loadFactory(Configuration configuration) {
        DataSourceFactory factory = ReflectionHelper.newInstance(configuration.getDataSourceFactoryClass());
        return factory.factory(configuration);
    }

    /**
     * create DataSourceFactory by application.properties
     * @return DataSourceFactory
     */
    DataSourceFactory factory();

    /**
     * create DataSourceFactory by DataSource
     * @param dataSource dataSource
     * @return DataSourceFactory
     */
    DataSourceFactory factory(DataSource dataSource);

    /**
     * create DataSourceFactory by DataSourceConfiguration
     * @param configuration configuration
     * @return DataSourceFactory
     */
    DataSourceFactory factory(DataSourceConfiguration configuration);

    /**
     * get DataSource
     * @return DataSource
     */
    DataSource getDataSource();

    Connection getConnection();

}
