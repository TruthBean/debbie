package com.truthbean.debbie.jdbc.datasource.pool;

import com.truthbean.debbie.core.properties.ConfigurationTypeNotMatchedException;
import com.truthbean.debbie.jdbc.datasource.DataSourceConfiguration;
import com.truthbean.debbie.jdbc.datasource.DataSourceFactory;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * @author TruthBean
 * @since 0.0.1
 */
public class DefaultDataSourcePoolFactory implements DataSourceFactory {
    private PooledDataSource pooledDataSource;
    @Override
    public DataSourceFactory factory(DataSource dataSource) {
        if (dataSource instanceof PooledDataSource) {
            pooledDataSource = (PooledDataSource) dataSource;
        } else {
            throw new ConfigurationTypeNotMatchedException();
        }
        return this;
    }

    @Override
    public DataSourceFactory factory(DataSourceConfiguration configuration) {
        if (configuration instanceof DefaultDataSourcePoolConfiguration) {
            pooledDataSource = new PooledDataSource((DefaultDataSourcePoolConfiguration) configuration);
        } else {
            throw new ConfigurationTypeNotMatchedException();
        }
        return this;
    }

    @Override
    public DataSource getDataSource() {
        return pooledDataSource;
    }

    @Override
    public Connection getConnection() {
        try {
            return pooledDataSource.getConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}