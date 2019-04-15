package com.truthbean.debbie.jdbc.datasource;

/**
 * @author TruthBean
 * @since 0.0.1
 */
public class DataSourcePoolConfiguration extends DataSourceConfiguration {
    private int initialPoolSize;
    private int increase;
    private int maxPoolSize;

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
    protected DataSourcePoolConfiguration clone() throws CloneNotSupportedException {
        return (DataSourcePoolConfiguration) super.clone();
    }
}
