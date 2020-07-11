/**
 * Copyright (c) 2020 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *         http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.jdbc.transaction;

import com.truthbean.debbie.jdbc.datasource.DriverConnection;
import com.truthbean.Logger;
import com.truthbean.logger.LoggerFactory;

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

    private Method method;

    private DriverConnection driverConnection;
    private Connection connection;

    private boolean forceCommit;
    private Class<? extends Throwable> rollbackFor;

    private final Map<Object, Object> resources = new LinkedHashMap<>();
    private final List<ResourceHolder> resourceHolders = new LinkedList<>();

    public TransactionInfo() {
        this.id = UUID.randomUUID().toString();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Method getMethod() {
        return method;
    }

    public void setMethod(Method method) {
        this.method = method;
    }

    public Connection getConnection() {
        return connection;
    }

    public DriverConnection getDriverConnection() {
        return driverConnection;
    }

    public void setConnection(DriverConnection driverConnection) {
        this.driverConnection = driverConnection;
        this.connection = driverConnection.getConnection();
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
        try {
            this.connection.setAutoCommit(autoCommit);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return this.connection;
    }

    public Connection setTransactionIsolation(TransactionIsolationLevel transactionIsolationLevel) {
        try {
            this.connection.setTransactionIsolation(transactionIsolationLevel.getLevel());
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return this.connection;
    }

    public Connection setTransactionIsolation(int transactionIsolationLevel) {
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
            LOGGER.error("method (" + method + ") not bind connection is null. ");
            return;
        }

        try {
            if (!connection.isReadOnly() && !connection.getAutoCommit()) {
                LOGGER.debug(() -> "Connection(" + connection + ") " + connection.hashCode() + " commit ...");
                connection.commit();
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
            }
        } catch (SQLException e) {
            LOGGER.error("rollback error for " + e.getMessage());
        }

        // after
        afterRollback();
    }

    private void afterRollback() {
        for (ResourceHolder resourceHolder : resourceHolders) {
            resourceHolder.afterRollback();
        }
    }

    private void beforeClose() {
        for (ResourceHolder resourceHolder : resourceHolders) {
            resourceHolder.beforeClose();
        }
    }

    @Override
    public void close() {
        beforeClose();

        if (connection == null) {
            LOGGER.error("method (" + method + ") not bind connection is null. ");
            return;
        }

        LOGGER.debug(() -> "close connection(" + connection + ") " + connection.hashCode() + " and remove it. ");
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
