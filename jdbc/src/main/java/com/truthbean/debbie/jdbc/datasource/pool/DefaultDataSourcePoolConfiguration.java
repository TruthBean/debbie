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

    /**
     * The maximum number of active connections
     */
    private int maxActiveConnection = 10;

    /**
     * The maximum number of idle connections
     */
    private int maxIdleConnection = 5;

    /**
     * The maximum time a connection can be used before it *may* be given away again
     */
    private int maxCheckoutTime = 20000;

    /**
     * The time to wait before retrying to get a connection
     */
    private int timeToWait = 20000;

    /**
     * The maximum number of tolerance for bad connection happens in one thread
     * which are applying for new {@link PooledConnection}
     */
    private int maxLocalBadConnectionTolerance = 3;

    /**
     * The query to be used to check a connection
     */
    private String pingQuery = "NO PING QUERY SET";

    /**
     * Determines if the ping query should be used.
     */
    private boolean pingEnabled;

    /**
     * If a connection has not been used in this many milliseconds,
     * ping the database to make sure the connection is still good.
     */
    private int pingConnectionNotUsedFor;

    public DefaultDataSourcePoolConfiguration(DataSourceConfiguration configuration) {
        super(configuration);
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

    public int getMaxActiveConnection() {
        return maxActiveConnection;
    }

    public void setMaxActiveConnection(int maxActiveConnection) {
        this.maxActiveConnection = maxActiveConnection;
    }

    public int getMaxIdleConnection() {
        return maxIdleConnection;
    }

    public void setMaxIdleConnection(int maxIdleConnection) {
        this.maxIdleConnection = maxIdleConnection;
    }

    public int getMaxCheckoutTime() {
        return maxCheckoutTime;
    }

    public void setMaxCheckoutTime(int maxCheckoutTime) {
        this.maxCheckoutTime = maxCheckoutTime;
    }

    public int getTimeToWait() {
        return timeToWait;
    }

    public void setTimeToWait(int timeToWait) {
        this.timeToWait = timeToWait;
    }

    public int getMaxLocalBadConnectionTolerance() {
        return maxLocalBadConnectionTolerance;
    }

    public void setMaxLocalBadConnectionTolerance(int maxLocalBadConnectionTolerance) {
        this.maxLocalBadConnectionTolerance = maxLocalBadConnectionTolerance;
    }

    public String getPingQuery() {
        return pingQuery;
    }

    public void setPingQuery(String pingQuery) {
        this.pingQuery = pingQuery;
    }

    public boolean isPingEnabled() {
        return pingEnabled;
    }

    public void setPingEnabled(boolean pingEnabled) {
        this.pingEnabled = pingEnabled;
    }

    public int getPingConnectionNotUsedFor() {
        return pingConnectionNotUsedFor;
    }

    public void setPingConnectionNotUsedFor(int pingConnectionNotUsedFor) {
        this.pingConnectionNotUsedFor = pingConnectionNotUsedFor;
    }

    @Override
    public DefaultDataSourcePoolConfiguration clone() {
        return (DefaultDataSourcePoolConfiguration) super.clone();
    }
}
