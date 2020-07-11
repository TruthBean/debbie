/**
 * Copyright (c) 2020 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *         http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.jdbc.datasource.pool;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * @author Clinton Begin
 */
class ConnectionProxy implements InvocationHandler {

    private static final String CLOSE = "close";
    private static final Class<?>[] IFACES = new Class<?>[]{Connection.class};

    private final int hashCode;
    private final Connection realConnection;
    private final Connection proxyConnection;
    private long checkoutTimestamp;
    private long createdTimestamp;
    private long lastUsedTimestamp;
    private int connectionTypeCode;
    private boolean valid;

    private final DefaultConnectionPool defaultConnectionPool;

    /**
     * Constructor for SimplePooledConnection that uses the Connection and PooledDataSource passed in
     *
     * @param connection - the connection that is to be presented as a pooled connection
     * @param defaultConnectionPool - the dataSource that the connection is from
     */
    public ConnectionProxy(Connection connection, DefaultConnectionPool defaultConnectionPool) {
        this.hashCode = connection.hashCode();
        this.realConnection = connection;
        this.createdTimestamp = System.currentTimeMillis();
        this.lastUsedTimestamp = System.currentTimeMillis();
        this.valid = true;
        this.proxyConnection = (Connection) Proxy.newProxyInstance(Connection.class.getClassLoader(), IFACES, this);
        this.defaultConnectionPool = defaultConnectionPool;
    }

    /**
     * Invalidates the connection
     */
    public void invalidate() {
        valid = false;
    }

    /**
     * Method to see if the connection is usable
     *
     * @return True if the connection is usable
     */
    public boolean isValid() {
        return valid && realConnection != null && defaultConnectionPool.pingConnection(this);
    }

    /**
     * Getter for the *real* connection that this wraps
     *
     * @return The connection
     */
    public Connection getRealConnection() {
        return realConnection;
    }

    /**
     * Getter for the proxy for the connection
     *
     * @return The proxy
     */
    public Connection getProxyConnection() {
        return proxyConnection;
    }

    /**
     * Gets the hashcode of the real connection (or 0 if it is null)
     *
     * @return The hashcode of the real connection (or 0 if it is null)
     */
    public int getRealHashCode() {
        return realConnection == null ? 0 : realConnection.hashCode();
    }

    /**
     * Getter for the connection type (based on url + user + password)
     *
     * @return The connection type
     */
    public int getConnectionTypeCode() {
        return connectionTypeCode;
    }

    /**
     * Setter for the connection type
     *
     * @param connectionTypeCode - the connection type
     */
    public void setConnectionTypeCode(int connectionTypeCode) {
        this.connectionTypeCode = connectionTypeCode;
    }

    /**
     * Getter for the time that the connection was created
     *
     * @return The creation timestamp
     */
    public long getCreatedTimestamp() {
        return createdTimestamp;
    }

    /**
     * Setter for the time that the connection was created
     *
     * @param createdTimestamp - the timestamp
     */
    public void setCreatedTimestamp(long createdTimestamp) {
        this.createdTimestamp = createdTimestamp;
    }

    /**
     * Getter for the time that the connection was last used
     *
     * @return - the timestamp
     */
    public long getLastUsedTimestamp() {
        return lastUsedTimestamp;
    }

    /**
     * Setter for the time that the connection was last used
     *
     * @param lastUsedTimestamp - the timestamp
     */
    public void setLastUsedTimestamp(long lastUsedTimestamp) {
        this.lastUsedTimestamp = lastUsedTimestamp;
    }

    /**
     * Getter for the time since this connection was last used
     *
     * @return - the time since the last use
     */
    public long getTimeElapsedSinceLastUse() {
        return System.currentTimeMillis() - lastUsedTimestamp;
    }

    /**
     * Getter for the age of the connection
     *
     * @return the age
     */
    public long getAge() {
        return System.currentTimeMillis() - createdTimestamp;
    }

    /**
     * Getter for the timestamp that this connection was checked out
     *
     * @return the timestamp
     */
    public long getCheckoutTimestamp() {
        return checkoutTimestamp;
    }

    /**
     * Setter for the timestamp that this connection was checked out
     *
     * @param timestamp the timestamp
     */
    public void setCheckoutTimestamp(long timestamp) {
        this.checkoutTimestamp = timestamp;
    }

    /**
     * Getter for the time that this connection has been checked out
     *
     * @return the time
     */
    public long getCheckoutTime() {
        return System.currentTimeMillis() - checkoutTimestamp;
    }

    @Override
    public int hashCode() {
        return hashCode;
    }

    /**
     * Allows comparing this connection to another
     *
     * @param obj - the other connection to test for equality
     * @see Object#equals(Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof ConnectionProxy) {
            return realConnection.hashCode() == ((ConnectionProxy) obj).realConnection.hashCode();
        } else if (obj instanceof Connection) {
            return hashCode == obj.hashCode();
        } else {
            return false;
        }
    }

    /**
     * Required for InvocationHandler implementation.
     *
     * @param proxy  - not used
     * @param method - the method to be executed
     * @param args   - the parameters to be passed to the method
     * @see java.lang.reflect.InvocationHandler#invoke(Object, java.lang.reflect.Method, Object[])
     */
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        String methodName = method.getName();
        if (CLOSE.hashCode() == methodName.hashCode() && CLOSE.equals(methodName)) {
            defaultConnectionPool.pushConnection(this);
            return null;
        }
        try {
            if (!Object.class.equals(method.getDeclaringClass())) {
                // issue #579 toString() should never fail
                // throw an SQLException instead of a Runtime
                checkConnection();
            }
            return method.invoke(realConnection, args);
        } catch (Throwable t) {
            throw t;
        }

    }

    private void checkConnection() throws SQLException {
        if (!valid) {
            throw new SQLException("Error accessing ConnectionProxy. Connection is invalid.");
        }
    }

}