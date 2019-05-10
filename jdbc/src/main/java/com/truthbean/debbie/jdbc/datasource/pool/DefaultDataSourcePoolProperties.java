package com.truthbean.debbie.jdbc.datasource.pool;

import com.truthbean.debbie.jdbc.datasource.DataSourceConfiguration;
import com.truthbean.debbie.jdbc.datasource.DataSourceProperties;

/**
 * @author TruthBean
 * @since 0.0.1
 * Created on 2018-03-26 16:41
 */
public class DefaultDataSourcePoolProperties extends DataSourceProperties implements DataSourcePoolProperties {
    private final int initialPoolSize;
    private final int increase;
    private final int maxPoolSize;

    //===========================================================================
    private static final String INITIAL_POOL_SIZE_KEY = "debbie.datasource.pool.init-size";
    private static final String INCREASE_KEY = "debbie.datasource.pool.increase";
    private static final String MAX_KEY = "debbie.datasource.pool.max-size";
    //===========================================================================

    private static DefaultDataSourcePoolConfiguration configuration;

    public DefaultDataSourcePoolProperties() {
        super();
        initialPoolSize = getIntegerValue(INITIAL_POOL_SIZE_KEY, 10);
        increase = getIntegerValue(INCREASE_KEY, 1);
        maxPoolSize = getIntegerValue(MAX_KEY, 10);
    }


    public static DataSourceConfiguration toConfiguration() {
        if (configuration == null) {
            DefaultDataSourcePoolProperties self = new DefaultDataSourcePoolProperties();
            configuration = toConfiguration(self.getConfiguration());
        }
        return configuration;
    }

    public static DefaultDataSourcePoolConfiguration toConfiguration(DataSourceConfiguration dataSourceConfiguration) {
        if (configuration == null) {
            configuration = new DefaultDataSourcePoolConfiguration(dataSourceConfiguration);
            DefaultDataSourcePoolProperties poolProperties = new DefaultDataSourcePoolProperties();
            configuration.setInitialPoolSize(poolProperties.initialPoolSize);
            configuration.setIncrease(poolProperties.increase);
            configuration.setMaxPoolSize(poolProperties.maxPoolSize);
        }
        return configuration;
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

    @Override
    public DataSourceConfiguration loadConfiguration() {
        return toConfiguration(super.getConfiguration());
    }
}
