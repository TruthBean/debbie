package com.truthbean.debbie.jdbc.datasource;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * @author TruthBean
 * @since 0.0.1
 */
public class DefaultDataSourceFactory implements DataSourceFactory {
    private DataSource dataSource;

    @Override
    public DataSourceFactory factory(DataSource dataSource) {
        this.dataSource = dataSource;
        return this;
    }

    @Override
    public DataSourceFactory factory(DataSourceConfiguration configuration) {
        this.dataSource = new DefaultDataSource(configuration);
        return this;
    }

    @Override
    public DataSource getDataSource() {
        return dataSource;
    }

    @Override
    public Connection getConnection() {
        Connection connection = null;
        try {
            connection = dataSource.getConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return connection;
    }
}
