package com.truthbean.debbie.jdbc.datasource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;

/**
 * @author TruthBean
 * @since 0.0.1
 */
public class DefaultDataSourceFactory implements DataSourceFactory {
    private DataSource dataSource;
    private DataSourceDriverName driverName;

    @Override
    public DataSourceFactory factory(DataSource dataSource) {
        this.dataSource = dataSource;
        return this;
    }

    @Override
    public DataSourceFactory factory(DataSourceConfiguration configuration) {
        this.dataSource = new DefaultDataSource(configuration);
        this.driverName = configuration.getDriverName();
        return this;
    }

    @Override
    public DataSource getDataSource() {
        return dataSource;
    }

    @Override
    public DataSourceDriverName getDriverName() {
        return driverName;
    }

    @Override
    public Logger getLogger() {
        return logger;
    }

    private static final Logger logger = LoggerFactory.getLogger(DefaultDataSourceFactory.class);
}
