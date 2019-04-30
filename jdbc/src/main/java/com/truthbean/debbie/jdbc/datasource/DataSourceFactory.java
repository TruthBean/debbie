package com.truthbean.debbie.jdbc.datasource;

import javax.sql.DataSource;

/**
 * @author TruthBean
 * @since 0.0.1
 */
public interface DataSourceFactory {

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

}
