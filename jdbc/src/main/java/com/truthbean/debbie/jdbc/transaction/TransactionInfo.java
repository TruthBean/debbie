package com.truthbean.debbie.jdbc.transaction;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Closeable;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

/**
 * @author TruthBean
 * @since 0.0.1
 */
public class TransactionInfo implements Closeable {
    private String id;

    private Method method;

    private Connection connection;

    private final Map<String, Object> resources = new LinkedHashMap<>();

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

    public void setConnection(Connection connection) {
        this.connection = connection;
    }

    public void bindResource(String key, Object value) {
        resources.put(key, value);
    }

    public void clearResource() {
        resources.clear();
    }

    public Object getResource(String key) {
        return resources.get(key);
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

    public void commit() {
        if (connection == null) {
            LOGGER.error("method (" + method + ") not bind connection is null. ");
            return;
        }

        try {
            if (!connection.isReadOnly() && !connection.getAutoCommit()) {
                LOGGER.debug("Connection " + connection.hashCode() + " commit ...");
                connection.commit();
            }
        } catch (SQLException e) {
            LOGGER.error("commit error for " + e.getMessage());
        }
    }

    public void rollback() {
        if (connection == null) {
            LOGGER.error("method (" + method + ") not bind connection is null. ");
            return;
        }

        try {
            if (!connection.isReadOnly()) {
                LOGGER.debug("Connection " + connection.hashCode() + " rollback ...");
                connection.rollback();
            }
        } catch (SQLException e) {
            LOGGER.error("rollback error for " + e.getMessage());
        }
    }

    @Override
    public void close() {
        resources.clear();
        if (connection == null) {
            LOGGER.error("method (" + method + ") not bind connection is null. ");
            return;
        }

        LOGGER.debug("close connection " + connection.hashCode() + " and remove it. ");
        try {
            if (!connection.isClosed()) {
                connection.close();
                connection = null;
            }
        } catch (SQLException e) {
            LOGGER.error("close connection " + connection.hashCode() + " error \n", e);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TransactionInfo)) return false;
        TransactionInfo that = (TransactionInfo) o;
        return Objects.equals(getId(), that.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(TransactionInfo.class);
}
