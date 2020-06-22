package com.truthbean.debbie.jdbc.datasource.pool;

import com.truthbean.debbie.jdbc.datasource.DataSourceConfiguration;
import com.truthbean.debbie.jdbc.datasource.DataSourceDriverName;
import com.truthbean.debbie.jdbc.datasource.DataSourceFactory;
import com.truthbean.debbie.properties.ConfigurationTypeNotMatchedException;
import com.truthbean.Logger;
import com.truthbean.logger.LoggerFactory;

import javax.sql.DataSource;

/**
 * @author TruthBean
 * @since 0.0.1
 */
public class DefaultDataSourcePoolFactory implements DataSourceFactory {
    private PooledDataSource pooledDataSource;
    private DataSourceDriverName driverName;
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
            driverName = configuration.getDriverName();
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
    public DataSourceDriverName getDriverName() {
        return driverName;
    }

    @Override
    public Logger getLogger() {
        return logger;
    }

    @Override
    public void destroy() {
        pooledDataSource.close();
    }

    private static final Logger logger = LoggerFactory.getLogger(DefaultDataSourcePoolFactory.class);
}
