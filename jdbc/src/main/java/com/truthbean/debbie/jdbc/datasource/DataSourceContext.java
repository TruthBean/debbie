package com.truthbean.debbie.jdbc.datasource;

import com.truthbean.debbie.core.reflection.ReflectionHelper;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * @author TruthBean
 * @since 0.0.1
 */
public class DataSourceContext {

    private static final ConnectionManager CONNECTION_MANAGER = new ConnectionManager();

    private static DataSourceFactory dataSourceFactory;

    public static <D extends DataSourceFactory> void registerDataSourceFactory(Class<D> dataSourceFactoryClass,
                                                 DataSourceConfiguration configuration) {
        DataSourceFactory dataSourceFactory = ReflectionHelper.newInstance(dataSourceFactoryClass);
        DataSourceContext.dataSourceFactory = dataSourceFactory.factory(configuration);
    }

    public static <D extends DataSourceFactory> void registerDataSourceFactory(DataSourceFactory dataSourceFactory) {
        DataSourceContext.dataSourceFactory = dataSourceFactory;
    }

    public static DataSourceFactory getDataSourceFactory() {
        return dataSourceFactory;
    }

    public static DataSource getDataSource() {
        return dataSourceFactory.getDataSource();
    }

    public static Connection getConnection() {
        var dataSource = dataSourceFactory.getDataSource();
        Connection connection = null;
        try {
            connection = dataSource.getConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        CONNECTION_MANAGER.bind(connection);
        return connection;
    }

    public static void closeConnection() {
        CONNECTION_MANAGER.close();
    }
}
