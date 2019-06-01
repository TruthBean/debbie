package com.truthbean.debbie.jdbc.datasource;

import javax.sql.DataSource;

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
}
