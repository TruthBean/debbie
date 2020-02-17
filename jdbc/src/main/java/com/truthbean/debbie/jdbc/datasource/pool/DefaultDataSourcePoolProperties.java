package com.truthbean.debbie.jdbc.datasource.pool;

import com.truthbean.debbie.bean.BeanFactoryHandler;
import com.truthbean.debbie.jdbc.datasource.DataSourceConfiguration;
import com.truthbean.debbie.jdbc.datasource.DataSourceProperties;
import com.truthbean.debbie.properties.DebbieProperties;

/**
 * @author TruthBean
 * @since 0.0.1
 * Created on 2018-03-26 16:41
 */
public class DefaultDataSourcePoolProperties extends DataSourceProperties {
    private final boolean unpool;

    //===========================================================================
    private static final String POOL_KEY_PREFIX = "debbie.datasource.pool";

    private static final String MAX_ACTIVE_CONNECTION_KEY = "debbie.datasource.pool.max-active-connection";
    private static final String MAX_IDLE_CONNECTION_KEY = "debbie.datasource.pool.max-idle-connection";
    private static final String MAX_CHECKOUT_TIME_KEY = "debbie.datasource.pool.max-checkout-time";
    private static final String TIME_TO_WAIT_KEY = "debbie.datasource.pool.time-to-wait";
    private static final String MAX_LOCAL_BAD_CONNECTION_TOLERANCE_KEY = "debbie.datasource.pool.max-local-bad-connection-tolerance";
    private static final String PING_QUERY_KEY = "debbie.datasource.pool.ping-query";
    private static final String PING_CONNECTION_NOT_USED_FOR_KEY = "debbie.datasource.pool.ping-connection-not-used-for";
    //===========================================================================

    private static DefaultDataSourcePoolConfiguration configuration;

    public DefaultDataSourcePoolProperties() {
        super();
        if (getMatchedKey(POOL_KEY_PREFIX).isEmpty()) {
            unpool = true;
        } else {
            unpool = false;
            configuration = new DefaultDataSourcePoolConfiguration(super.getConfiguration());
            configuration.setMaxActiveConnection(getIntegerValue(MAX_ACTIVE_CONNECTION_KEY, 10));
            configuration.setMaxIdleConnection(getIntegerValue(MAX_IDLE_CONNECTION_KEY, 5));
            configuration.setMaxCheckoutTime(getIntegerValue(MAX_CHECKOUT_TIME_KEY, 20000));
            configuration.setTimeToWait(getIntegerValue(TIME_TO_WAIT_KEY, 20000));
            configuration.setMaxLocalBadConnectionTolerance(getIntegerValue(MAX_LOCAL_BAD_CONNECTION_TOLERANCE_KEY, 3));
            String pingQuery = getValue(PING_QUERY_KEY);
            if (pingQuery == null || pingQuery.isBlank()) {
                configuration.setPingEnabled(false);
            } else {
                configuration.setPingQuery(pingQuery);
                configuration.setPingEnabled(true);
                configuration.setPingConnectionNotUsedFor(getIntegerValue(PING_CONNECTION_NOT_USED_FOR_KEY, 0));
                configuration.setDataSourceFactoryClass(DefaultDataSourcePoolFactory.class);
            }
        }
    }

    @Override
    public DataSourceConfiguration toConfiguration(BeanFactoryHandler beanFactoryHandler) {
        if (unpool) {
            return super.getConfiguration();
        }
        return toConfiguration(super.getConfiguration());
    }

    public static DefaultDataSourcePoolConfiguration toConfiguration(DataSourceConfiguration dataSourceConfiguration) {
        if (configuration == null) {
            configuration = new DefaultDataSourcePoolConfiguration(dataSourceConfiguration);
        }
        return configuration;
    }
}
