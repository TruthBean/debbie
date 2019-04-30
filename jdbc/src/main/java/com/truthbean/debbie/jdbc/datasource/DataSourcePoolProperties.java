package com.truthbean.debbie.jdbc.datasource;

/**
 * @author TruthBean
 * @since 0.0.1
 * Created on 2018-03-26 16:41
 */
public class DataSourcePoolProperties extends DataSourceProperties {
    private final int initialPoolSize;
    private final int increase;
    private final int maxPoolSize;

    //===========================================================================
    private static final String INITIAL_POOL_SIZE_KEY = "debbie.datasource.pool.init-size";
    private static final String INCREASE_KEY = "debbie.datasource.pool.increase";
    private static final String MAX_KEY = "debbie.datasource.pool.max-size";
    //===========================================================================

    public DataSourcePoolProperties() {
        initialPoolSize = getIntegerValue(INITIAL_POOL_SIZE_KEY, 10);
        increase = getIntegerValue(INCREASE_KEY, 1);
        maxPoolSize = getIntegerValue(MAX_KEY, 10);
    }

    public static DataSourceConfiguration toConfiguration() {
        return new DataSourcePoolConfiguration(DataSourceProperties.toConfiguration());
    }

    public int getIncrease() {
        return increase;
    }

    public int getInitialPoolSize() {
        return initialPoolSize;
    }

    public int getMaxPoolSize() {
        return maxPoolSize;
    }
}
