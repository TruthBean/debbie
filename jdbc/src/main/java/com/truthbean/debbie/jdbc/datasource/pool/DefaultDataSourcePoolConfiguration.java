/**
 * Copyright (c) 2022 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *         http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.jdbc.datasource.pool;

import com.truthbean.debbie.jdbc.datasource.DataSourceConfiguration;
import com.truthbean.debbie.jdbc.datasource.DataSourceFactory;

/**
 * @author TruthBean
 * @since 0.0.1
 */
public class DefaultDataSourcePoolConfiguration extends DataSourceConfiguration {
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
     * which are applying for new {@link ConnectionProxy}
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
    public Class<? extends DataSourceFactory> getDataSourceFactoryClass() {
        return DefaultDataSourcePoolFactory.class;
    }

    @Override
    public DefaultDataSourcePoolConfiguration copy() {
        return (DefaultDataSourcePoolConfiguration) super.copy();
    }
}
