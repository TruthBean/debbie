package com.truthbean.debbie.jdbc.datasource.pool;

import com.truthbean.debbie.jdbc.datasource.DataSourceConfiguration;
import com.truthbean.debbie.jdbc.datasource.DataSourceProperties;

/**
 * @author TruthBean
 * @since 0.0.1
 * Created on 2018-03-26 16:41
 */
public class DefaultDataSourcePoolProperties extends DataSourceProperties implements DataSourcePoolProperties {
    private final boolean unpool;

    //===========================================================================
    private static final String POOL_KEY_PREFIX = "debbie.datasource.pool";
    private static final String INITIAL_POOL_SIZE_KEY = "debbie.datasource.pool.init-size";
    private static final String INCREASE_KEY = "debbie.datasource.pool.increase";
    private static final String MAX_KEY = "debbie.datasource.pool.max-size";

    private static final String MAX_ACTIVE_CONNECTION_KEY = "debbie.datasource.pool.max-active-connection";
    //===========================================================================

    private static DefaultDataSourcePoolConfiguration configuration;

    public DefaultDataSourcePoolProperties() {
        super();
        if (getMatchedKey(POOL_KEY_PREFIX).isEmpty()) {
            unpool = true;
        } else {
            unpool = false;
            configuration = new DefaultDataSourcePoolConfiguration(super.getConfiguration());
            configuration.setInitialPoolSize(getIntegerValue(INITIAL_POOL_SIZE_KEY, 10));
            configuration.setIncrease(getIntegerValue(INCREASE_KEY, 1));
            configuration.setMaxPoolSize(getIntegerValue(MAX_KEY, 10));


        }
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
        }
        return configuration;
    }

    @Override
    public DataSourceConfiguration loadConfiguration() {
        if (unpool) {
            return super.getConfiguration();
        }
        return toConfiguration(super.getConfiguration());
    }
}
