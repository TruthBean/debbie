package com.truthbean.debbie.jdbc.datasource;

import com.truthbean.debbie.core.reflection.ReflectionHelper;

import javax.sql.DataSource;

/**
 * @author TruthBean
 * @since 0.0.1
 */
public class DataSourceContext {

    private static DataSourceFactory dataSourceFactory;



    public static void registerDataSourceFactory(DataSourceFactory dataSourceFactory) {
        DataSourceContext.dataSourceFactory = dataSourceFactory;
    }

    public static DataSourceFactory getDataSourceFactory() {
        return dataSourceFactory;
    }

    public static DataSource getDataSource() {
        return dataSourceFactory.getDataSource();
    }
}
