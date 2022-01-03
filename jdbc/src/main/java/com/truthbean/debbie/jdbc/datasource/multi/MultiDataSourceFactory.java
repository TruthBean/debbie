package com.truthbean.debbie.jdbc.datasource.multi;

import com.truthbean.debbie.jdbc.datasource.DataSourceFactory;

import javax.sql.DataSource;
import java.util.Map;

/**
 * @author TruthBean
 * @since 3.1.0
 */
public interface MultiDataSourceFactory extends DataSourceFactory {

    String dataSourceKeyPrefix = "dataSource";
    String dataSourceFactoryKeyPrefix = "dataSourceFactory";
    String defaultDataSourceKey = "defaultDataSource";
    String defaultDataSourceFactoryKey = "defaultDataSourceFactory";

    Map<String, DataSourceFactory> getAllDataSourceFactory();

    Map<String, DataSource> getAllDataSource();

    DataSourceFactory factoryDefault(DataSource dataSource);

    DataSourceFactory factory(String name);

    DataSource getDefaultDataSource();

    DataSource getDataSource(String name);
}
