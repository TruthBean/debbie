/**
 * Copyright (c) 2021 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *         http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.jdbc.transaction;

import com.truthbean.debbie.jdbc.datasource.DataSourceDriverName;
import com.truthbean.Logger;
import com.truthbean.LoggerFactory;

import java.io.Closeable;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;

/**
 * @author TruthBean
 * @since 0.0.1
 */
public class TransactionInfo implements Closeable {
    private String id;
    private volatile boolean using;

    private final String method = "(no method)";

    private DataSourceDriverName driverName;
    private volatile Connection connection;

    private boolean forceCommit;
    private Class<? extends Throwable> rollbackFor;

    private final Map<Object, Object> resources = new LinkedHashMap<>();
    private final List<ResourceHolder> resourceHolders = new LinkedList<>();

    private volatile boolean closed;

    public TransactionInfo() {
        this.id = UUID.randomUUID().toString();
        this.using = false;
        this.closed = false;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setUsing(boolean using) {
        this.using = using;
    }

    public boolean isUsing() {
        return using;
    }

    public Method getMethod() {
        if (hasMethod())
            return (Method) this.resources.get(this.method);
        return null;
    }

    private boolean hasMethod() {
        return this.resources.containsKey(this.method);
    }

    public Connection getConnection() {
        return connection;
    }

    public void setConnection(Connection connection) {
        this.connection = connection;
    }

    public DataSourceDriverName getDriverName() {
        return driverName;
    }

    public void setDriverName(DataSourceDriverName driverName) {
        this.driverName = driverName;
    }

    public boolean isForceCommit() {
        return forceCommit;
    }

    public void setForceCommit(boolean forceCommit) {
        this.forceCommit = forceCommit;
    }

    public Class<? extends Throwable> getRollbackFor() {
        return rollbackFor;
    }

    public void setRollbackFor(Class<? extends Throwable> rollbackFor) {
        this.rollbackFor = rollbackFor;
    }

    public void bindResource(Object key, Object value) {
        resources.put(key, value);
    }

    public void bindResources(Map<Object, Object> resources) {
        if (resources != null && !resources.isEmpty())
            this.resources.putAll(resources);
    }

    public void bindMethod(Method method) {
        this.resources.put(this.method, method);
    }

    public void clearResource() {
        resources.clear();
    }

    public Object getResource(Object key) {
        return resources.get(key);
    }

    public void registerResourceHolder(ResourceHolder resourceHolder) {
        if (resourceHolder != null)
            resourceHolders.add(resourceHolder);
    }

    public void registerResourceHolders(List<ResourceHolder> resourceHolders) {
        if (resourceHolders != null && !resourceHolders.isEmpty()) {
            this.resourceHolders.addAll(resourceHolders);
        }
    }

    public void releaseResourceHolder(ResourceHolder resourceHolder) {
        resourceHolders.remove(resourceHolder);
    }

    public void clearResourceHolders() {
        resourceHolders.clear();
    }

    public Connection setAutoCommit(boolean autoCommit) {
        if (connection == null || closed) {
            return null;
        }
        try {
            this.connection.setAutoCommit(autoCommit);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return this.connection;
    }

    public Connection setTransactionIsolation(TransactionIsolationLevel transactionIsolationLevel) {
        if (connection == null || closed) {
            return null;
        }
        try {
            this.connection.setTransactionIsolation(transactionIsolationLevel.getLevel());
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return this.connection;
    }

    public Connection setTransactionIsolation(int transactionIsolationLevel) {
        if (connection == null || closed) {
            return null;
        }
        try {
            this.connection.setTransactionIsolation(transactionIsolationLevel);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return this.connection;
    }

    public void prepare() {
        for (ResourceHolder resourceHolder : resourceHolders) {
            resourceHolder.prepare();
        }
    }

    private void beforeCommit() {
        for (ResourceHolder resourceHolder : resourceHolders) {
            resourceHolder.beforeCommit();
        }
    }

    public void commit() {
        // before
        beforeCommit();

        // commit
        if (connection == null) {
            LOGGER.error("method (" + method + ") not bind connection. ");
            return;
        }

        try {
            if (!connection.isReadOnly() && !connection.getAutoCommit()) {
                LOGGER.debug(() -> "Connection(" + connection + ") " + connection.hashCode() + " commit ...");
                connection.commit();
            } else {
                LOGGER.warn(() -> "Connection(" + connection + ") " + connection.hashCode() + " is readonly or autocommited, cannot commit manually!");
            }
        } catch (SQLException e) {
            LOGGER.error("commit error for " + e.getMessage());
        }

        // after
        afterCommit();
    }

    private void afterCommit() {
        for (ResourceHolder resourceHolder : resourceHolders) {
            resourceHolder.afterCommit();
        }
    }

    private void beforeRollback() {
        for (ResourceHolder resourceHolder : resourceHolders) {
            resourceHolder.beforeRollback();
        }
    }

    public void rollback() {
        // before
        beforeRollback();

        if (connection == null) {
            LOGGER.error("method (" + method + ") not bind connection is null. ");
            return;
        }

        try {
            if (!connection.isReadOnly()) {
                LOGGER.debug(() -> "Connection(" + connection + ") " + connection.hashCode() + " rollback ...");
                connection.rollback();
            } else {
                LOGGER.warn(() -> "Connection(" + connection + ") " + connection.hashCode() + " is readonly, cannot rollback!");
            }
        } catch (SQLException e) {
            LOGGER.error("rollback error for " + e.getMessage());
        }

        // after
        afterRollback();
    }

    public boolean isClosed() {
        return closed;
    }

    public void setClosed(boolean closed) {
        this.closed = closed;
    }

    private void afterRollback() {
        for (ResourceHolder resourceHolder : resourceHolders) {
            resourceHolder.afterRollback();
        }
    }

    private void beforeClose() {
        this.closed = true;
        for (ResourceHolder resourceHolder : resourceHolders) {
            resourceHolder.beforeClose();
        }
    }

    @Override
    public void close() {
        synchronized (this) {
            if (!isUsing()) {
                beforeClose();

                if (connection == null) {
                    LOGGER.error("method (" + method + ") not bind connection is null. ");
                    return;
                }

                if (hasMethod())
                    LOGGER.debug(() -> id + ": close connection(" + connection + ") " + connection.hashCode() + " by transactional method(" + getMethod() + ") and remove it. ");
                else
                    LOGGER.debug(() -> id + ": close connection(" + connection + ") " + connection.hashCode() + " and remove it. ");
                try {
                    if (!connection.isClosed()) {
                        connection.close();
                        connection = null;
                    }
                } catch (SQLException e) {
                    LOGGER.error("close connection(" + connection + ") " + connection.hashCode() + " error \n", e);
                }

                afterClose();

                // clear
                resources.clear();
                resourceHolders.clear();
            } else {
                LOGGER.warn(() -> id + ": connection(" + connection + ") " + connection.hashCode() + " is using, cannot remove it! ");
            }
        }
    }

    private void afterClose() {
        for (ResourceHolder resourceHolder : resourceHolders) {
            resourceHolder.afterClose();
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof TransactionInfo))
            return false;
        TransactionInfo that = (TransactionInfo) o;
        return Objects.equals(getId(), that.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(TransactionInfo.class);
}
