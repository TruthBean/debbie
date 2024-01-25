/**
 * Copyright (c) 2024 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 * http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.jdbc.datasource.pool;

import com.truthbean.debbie.jdbc.datasource.DefaultDataSource;
import com.truthbean.Logger;
import com.truthbean.LoggerFactory;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Clinton Begin
 */
public class DefaultConnectionPool {

    private final DefaultDataSource dataSource;

    private final List<ConnectionProxy> idleConnections = new ArrayList<>();
    private final List<ConnectionProxy> activeConnections = new ArrayList<>();
    private long requestCount = 0;
    private long accumulatedRequestTime = 0;
    private long accumulatedCheckoutTime = 0;
    private long claimedOverdueConnectionCount = 0;
    private long accumulatedCheckoutTimeOfOverdueConnections = 0;
    private long accumulatedWaitTime = 0;
    private long hadToWaitCount = 0;
    private long badConnectionCount = 0;

    private int expectedConnectionTypeCode;

    private final DefaultDataSourcePoolConfiguration configuration;

    public DefaultConnectionPool(DefaultDataSourcePoolConfiguration configuration) {
        this.configuration = configuration;
        this.dataSource = new DefaultDataSource(configuration);
    }

    public synchronized long getRequestCount() {
        return requestCount;
    }

    public synchronized long getAverageRequestTime() {
        return requestCount == 0 ? 0 : accumulatedRequestTime / requestCount;
    }

    public synchronized long getAverageWaitTime() {
        return hadToWaitCount == 0 ? 0 : accumulatedWaitTime / hadToWaitCount;
    }

    public synchronized long getHadToWaitCount() {
        return hadToWaitCount;
    }

    public synchronized long getBadConnectionCount() {
        return badConnectionCount;
    }

    public synchronized long getClaimedOverdueConnectionCount() {
        return claimedOverdueConnectionCount;
    }

    public synchronized long getAverageOverdueCheckoutTime() {
        return claimedOverdueConnectionCount == 0 ? 0 : accumulatedCheckoutTimeOfOverdueConnections / claimedOverdueConnectionCount;
    }

    public synchronized long getAverageCheckoutTime() {
        return requestCount == 0 ? 0 : accumulatedCheckoutTime / requestCount;
    }


    public synchronized int getIdleConnectionCount() {
        return idleConnections.size();
    }

    public synchronized int getActiveConnectionCount() {
        return activeConnections.size();
    }

    /*
     * Closes all active and idle connections in the pool
     */
    public void forceCloseAll() {
        synchronized (DefaultConnectionPool.class) {
            expectedConnectionTypeCode = assembleConnectionTypeCode(configuration.getUrl(), configuration.getUser(), configuration.getPassword());
            for (int i = this.activeConnections.size(); i > 0; i--) {
                try {
                    ConnectionProxy conn = this.activeConnections.remove(i - 1);
                    conn.invalidate();

                    Connection realConn = conn.getRealConnection();
                    if (!realConn.getAutoCommit()) {
                        realConn.rollback();
                    }
                    realConn.close();
                } catch (Exception e) {
                    // ignore
                }
            }
            for (int i = this.idleConnections.size(); i > 0; i--) {
                try {
                    ConnectionProxy conn = this.idleConnections.remove(i - 1);
                    conn.invalidate();

                    Connection realConn = conn.getRealConnection();
                    if (!realConn.getAutoCommit()) {
                        realConn.rollback();
                    }
                    realConn.close();
                } catch (Exception e) {
                    // ignore
                }
            }
        }
        if (log.isDebugEnabled()) {
            log.debug("PooledDataSource forcefully closed/removed all connections.");
        }
    }

    public DefaultConnectionPool getPoolState() {
        return this;
    }

    private int assembleConnectionTypeCode(String url, String username, String password) {
        return ("" + url + username + password).hashCode();
    }

    protected void pushConnection(ConnectionProxy conn) throws SQLException {
        synchronized (DefaultConnectionPool.class) {
            this.activeConnections.remove(conn);
            if (conn.isValid()) {
                if (this.idleConnections.size() < configuration.getMaxIdleConnection() && conn.getConnectionTypeCode() == expectedConnectionTypeCode) {
                    this.accumulatedCheckoutTime += conn.getCheckoutTime();
                    if (!conn.getRealConnection().getAutoCommit()) {
                        conn.getRealConnection().rollback();
                    }
                    ConnectionProxy newConn = new ConnectionProxy(conn.getRealConnection(), this);
                    this.idleConnections.add(newConn);
                    newConn.setCreatedTimestamp(conn.getCreatedTimestamp());
                    newConn.setLastUsedTimestamp(conn.getLastUsedTimestamp());
                    conn.invalidate();
                    if (log.isDebugEnabled()) {
                        log.debug("Returned connection " + newConn.getRealHashCode() + " to pool.");
                    }
                    this.notifyAll();
                } else {
                    this.accumulatedCheckoutTime += conn.getCheckoutTime();
                    if (!conn.getRealConnection().getAutoCommit()) {
                        conn.getRealConnection().rollback();
                    }
                    conn.getRealConnection().close();
                    if (log.isDebugEnabled()) {
                        log.debug("Closed connection " + conn.getRealHashCode() + ".");
                    }
                    conn.invalidate();
                }
            } else {
                if (log.isDebugEnabled()) {
                    log.debug("A bad connection (" + conn.getRealHashCode() + ") attempted to return to the pool, discarding connection.");
                }
                this.badConnectionCount++;
            }
        }
    }

    protected ConnectionProxy popConnection(String username, String password) throws SQLException {
        boolean countedWait = false;
        ConnectionProxy conn = null;
        long t = System.currentTimeMillis();
        int localBadConnectionCount = 0;

        while (conn == null) {
            synchronized (DefaultConnectionPool.class) {
                if (!this.idleConnections.isEmpty()) {
                    // Pool has available connection
                    conn = this.idleConnections.remove(0);
                    if (log.isDebugEnabled()) {
                        log.debug("Checked out connection " + conn.getRealHashCode() + " from pool.");
                    }
                } else {
                    // Pool does not have available connection
                    if (this.activeConnections.size() < configuration.getMaxActiveConnection()) {
                        // Can create new connection
                        conn = new ConnectionProxy(dataSource.getConnection(), this);
                        if (log.isDebugEnabled()) {
                            log.debug("Created connection " + conn.getRealHashCode() + ".");
                        }
                    } else {
                        // Cannot create new connection
                        ConnectionProxy oldestActiveConnection = this.activeConnections.get(0);
                        long longestCheckoutTime = oldestActiveConnection.getCheckoutTime();
                        if (longestCheckoutTime > configuration.getMaxCheckoutTime()) {
                            // Can claim overdue connection
                            this.claimedOverdueConnectionCount++;
                            this.accumulatedCheckoutTimeOfOverdueConnections += longestCheckoutTime;
                            this.accumulatedCheckoutTime += longestCheckoutTime;
                            this.activeConnections.remove(oldestActiveConnection);
                            if (!oldestActiveConnection.getRealConnection().getAutoCommit()) {
                                try {
                                    oldestActiveConnection.getRealConnection().rollback();
                                } catch (SQLException e) {
                                  /*
                                     Just log a message for debug and continue to execute the following
                                     thisment like nothing happened.
                                     Wrap the bad connection with a new ConnectionProxy, this will help
                                     to not interrupt current executing thread and give current thread a
                                     chance to join the next competition for another valid/good database
                                     connection. At the end of this loop, bad {@link @conn} will be set as null.
                                   */
                                    log.debug("Bad connection. Could not roll back");
                                }
                            }
                            conn = new ConnectionProxy(oldestActiveConnection.getRealConnection(), this);
                            conn.setCreatedTimestamp(oldestActiveConnection.getCreatedTimestamp());
                            conn.setLastUsedTimestamp(oldestActiveConnection.getLastUsedTimestamp());
                            oldestActiveConnection.invalidate();
                            if (log.isDebugEnabled()) {
                                log.debug("Claimed overdue connection " + conn.getRealHashCode() + ".");
                            }
                        } else {
                            // Must wait
                            try {
                                if (!countedWait) {
                                    this.hadToWaitCount++;
                                    countedWait = true;
                                }
                                int timeToWait = configuration.getTimeToWait();
                                if (log.isDebugEnabled()) {
                                    log.debug("Waiting as long as " + timeToWait + " milliseconds for connection.");
                                }
                                long wt = System.currentTimeMillis();
                                this.wait(timeToWait);
                                this.accumulatedWaitTime += System.currentTimeMillis() - wt;
                            } catch (InterruptedException e) {
                                break;
                            }
                        }
                    }
                }
                if (conn != null) {
                    // ping to server and check the connection is valid or not
                    if (conn.isValid()) {
                        if (!conn.getRealConnection().getAutoCommit()) {
                            conn.getRealConnection().rollback();
                        }
                        conn.setConnectionTypeCode(assembleConnectionTypeCode(configuration.getUrl(), username, password));
                        conn.setCheckoutTimestamp(System.currentTimeMillis());
                        conn.setLastUsedTimestamp(System.currentTimeMillis());
                        this.activeConnections.add(conn);
                        this.requestCount++;
                        this.accumulatedRequestTime += System.currentTimeMillis() - t;
                    } else {
                        if (log.isDebugEnabled()) {
                            log.debug("A bad connection (" + conn.getRealHashCode() + ") was returned from the pool, getting another connection.");
                        }
                        this.badConnectionCount++;
                        localBadConnectionCount++;
                        conn = null;
                        if (localBadConnectionCount > (configuration.getMaxIdleConnection() + configuration.getMaxLocalBadConnectionTolerance())) {
                            if (log.isDebugEnabled()) {
                                log.debug("PooledDataSource: Could not get a good connection to the database.");
                            }
                            throw new SQLException("PooledDataSource: Could not get a good connection to the database.");
                        }
                    }
                }
            }

        }

        if (conn == null) {
            if (log.isDebugEnabled()) {
                log.debug("PooledDataSource: Unknown severe error condition.  The connection pool returned a null connection.");
            }
            throw new SQLException("PooledDataSource: Unknown severe error condition.  The connection pool returned a null connection.");
        }

        return conn;
    }

    /**
     * Method to check to see if a connection is still usable
     *
     * @param conn - the connection to check
     * @return True if the connection is still usable
     */
    protected boolean pingConnection(ConnectionProxy conn) {
        boolean result = true;

        try {
            result = !conn.getRealConnection().isClosed();
        } catch (SQLException e) {
            if (log.isDebugEnabled()) {
                log.debug("Connection " + conn.getRealHashCode() + " is BAD: " + e.getMessage());
            }
            result = false;
        }

        if (result) {
            if (!configuration.isPingEnabled())
                return true;
            int poolPingConnectionsNotUsedFor = configuration.getPingConnectionNotUsedFor();
            String poolPingQuery = configuration.getPingQuery();
            if (poolPingConnectionsNotUsedFor >= 0 && conn.getTimeElapsedSinceLastUse() > poolPingConnectionsNotUsedFor) {
                try {
                    if (log.isDebugEnabled()) {
                        log.debug("Testing connection " + conn.getRealHashCode() + " ...");
                    }
                    Connection realConn = conn.getRealConnection();
                    try (Statement statement = realConn.createStatement()) {
                        statement.executeQuery(poolPingQuery).close();
                    }
                    if (!realConn.getAutoCommit()) {
                        realConn.rollback();
                    }
                    result = true;
                    if (log.isDebugEnabled()) {
                        log.debug("Connection " + conn.getRealHashCode() + " is GOOD!");
                    }
                } catch (Exception e) {
                    log.warn("Execution of ping query '" + poolPingQuery + "' failed: " + e.getMessage());
                    try {
                        conn.getRealConnection().close();
                    } catch (Exception e2) {
                        //ignore
                    }
                    result = false;
                    if (log.isDebugEnabled()) {
                        log.debug("Connection " + conn.getRealHashCode() + " is BAD: " + e.getMessage());
                    }
                }
            }
        }
        return result;
    }

    /**
     * Unwraps a pooled connection to get to the 'real' connection
     *
     * @param conn - the pooled connection to unwrap
     * @return The 'real' connection
     */
    public static Connection unwrapConnection(Connection conn) {
        if (Proxy.isProxyClass(conn.getClass())) {
            InvocationHandler handler = Proxy.getInvocationHandler(conn);
            if (handler instanceof ConnectionProxy) {
                return ((ConnectionProxy) handler).getRealConnection();
            }
        }
        return conn;
    }

    @Override
    public synchronized String toString() {
        return "\n===CONFINGURATION==============================================" +
                "\n jdbcDriver                     " + configuration.getDriverName() +
                "\n jdbcUrl                        " + configuration.getUrl() +
                "\n jdbcUsername                   " + configuration.getUrl() +
                "\n jdbcPassword                   " + (configuration.getPassword() == null ? "NULL" : "************") +
                "\n poolMaxActiveConnections       " + configuration.getMaxActiveConnection() +
                "\n poolMaxIdleConnections         " + configuration.getMaxIdleConnection() +
                "\n poolMaxCheckoutTime            " + configuration.getMaxCheckoutTime() +
                "\n poolTimeToWait                 " + configuration.getTimeToWait() +
                "\n poolPingEnabled                " + configuration.isPingEnabled() +
                "\n poolPingQuery                  " + configuration.getPingQuery() +
                "\n poolPingConnectionsNotUsedFor  " + configuration.getPingConnectionNotUsedFor() +
                "\n ---STATUS-----------------------------------------------------" +
                "\n activeConnections              " + getActiveConnectionCount() +
                "\n idleConnections                " + getIdleConnectionCount() +
                "\n requestCount                   " + getRequestCount() +
                "\n averageRequestTime             " + getAverageRequestTime() +
                "\n averageCheckoutTime            " + getAverageCheckoutTime() +
                "\n claimedOverdue                 " + getClaimedOverdueConnectionCount() +
                "\n averageOverdueCheckoutTime     " + getAverageOverdueCheckoutTime() +
                "\n hadToWait                      " + getHadToWaitCount() +
                "\n averageWaitTime                " + getAverageWaitTime() +
                "\n badConnectionCount             " + getBadConnectionCount() +
                "\n===============================================================";
    }

    private static final Logger log = LoggerFactory.getLogger(DefaultConnectionPool.class);

}