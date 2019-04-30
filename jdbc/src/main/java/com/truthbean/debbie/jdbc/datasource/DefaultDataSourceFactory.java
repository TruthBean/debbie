package com.truthbean.debbie.jdbc.datasource;

import javax.sql.DataSource;

/**
 * @author TruthBean
 * @since 0.0.1
 */
public class DefaultDataSourceFactory implements DataSourceFactory {
    private DataSourceConfiguration configuration;
    private DataSource dataSource;

    @Override
    public DataSourceFactory factory() {
        this.configuration = DataSourceProperties.toConfiguration();
        this.dataSource = new DefaultDataSource(configuration);
        return this;
    }

    @Override
    public DataSourceFactory factory(DataSource dataSource) {
        this.dataSource = dataSource;
        return this;
    }

    @Override
    public DataSourceFactory factory(DataSourceConfiguration configuration) {
        this.configuration = configuration;
        this.dataSource = new DefaultDataSource(configuration);
        return this;
    }

    @Override
    public DataSource getDataSource() {
        return dataSource;
    }
}
