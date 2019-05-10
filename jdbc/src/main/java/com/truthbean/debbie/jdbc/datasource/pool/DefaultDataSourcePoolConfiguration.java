package com.truthbean.debbie.jdbc.datasource.pool;

import com.truthbean.debbie.jdbc.datasource.DataSourceConfiguration;

/**
 * @author TruthBean
 * @since 0.0.1
 */
public class DefaultDataSourcePoolConfiguration extends DataSourceConfiguration {
    private int initialPoolSize;
    private int increase;
    private int maxPoolSize;

    public DefaultDataSourcePoolConfiguration(DataSourceConfiguration configuration) {
        super(configuration);
    }

    public DefaultDataSourcePoolConfiguration(int initialPoolSize, int increase, int maxPoolSize) {
        this.initialPoolSize = initialPoolSize;
        this.increase = increase;
        this.maxPoolSize = maxPoolSize;
    }

    public int getInitialPoolSize() {
        return initialPoolSize;
    }

    public void setInitialPoolSize(int initialPoolSize) {
        this.initialPoolSize = initialPoolSize;
    }

    public int getIncrease() {
        return increase;
    }

    public void setIncrease(int increase) {
        this.increase = increase;
    }

    public int getMaxPoolSize() {
        return maxPoolSize;
    }

    public void setMaxPoolSize(int maxPoolSize) {
        this.maxPoolSize = maxPoolSize;
    }

    @Override
    public DefaultDataSourcePoolConfiguration clone() {
        return (DefaultDataSourcePoolConfiguration) super.clone();
    }
}
