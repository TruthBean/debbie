package com.truthbean.debbie.jdbc.datasource.threadlocal;

import com.truthbean.Logger;
import com.truthbean.LoggerFactory;
import com.truthbean.debbie.jdbc.datasource.DataSourceConfiguration;
import com.truthbean.debbie.jdbc.datasource.DataSourceDriverName;
import com.truthbean.debbie.jdbc.datasource.DataSourceFactory;
import com.truthbean.debbie.jdbc.datasource.DefaultDataSource;

import javax.sql.DataSource;

/**
 * @author TruthBean/Rogar·Q
 * @since 0.6.0
 */
public class ThreadLocalDataSourceFactory implements DataSourceFactory {

    private final ThreadLocal<DataSource> dataSourceThreadLocal = new ThreadLocal<>();
    private final ThreadLocal<DataSourceDriverName> driverNameThreadLocal = new ThreadLocal<>();

    @Override
    public <T extends DataSourceConfiguration> boolean support(T configuration) {
        return configuration instanceof ThreadLocalDataSourceConfiguration
                && ((ThreadLocalDataSourceConfiguration) configuration).isThreadLocal();
    }

    @Override
    public DataSourceFactory factory(DataSource dataSource) {
        if (dataSourceThreadLocal.get() == null) {
            dataSourceThreadLocal.set(dataSource);
        }
        return this;
    }

    @Override
    public DataSourceFactory factory(DataSourceConfiguration configuration) {
        if (driverNameThreadLocal.get() == null) {
            DefaultDataSource dataSource = new DefaultDataSource(configuration);
            driverNameThreadLocal.set(configuration.getDriverName());
            dataSourceThreadLocal.set(dataSource);
        }
        return this;
    }

    @Override
    public String getName() {
        return "threadLocalDataSourceFactory";
    }

    @Override
    public DataSource getDataSource() {
        return dataSourceThreadLocal.get();
    }

    @Override
    public DataSourceDriverName getDriverName() {
        return driverNameThreadLocal.get();
    }

    @Override
    public Logger getLogger() {
        return LOGGER;
    }

    @Override
    public void close() {
        dataSourceThreadLocal.remove();
        driverNameThreadLocal.remove();
        DataSourceFactory.super.close();
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(ThreadLocalDataSourceFactory.class);
}
