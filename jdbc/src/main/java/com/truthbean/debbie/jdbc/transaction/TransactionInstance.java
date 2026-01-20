/**
 * Copyright (c) 2026 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 * http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.jdbc.transaction;

import com.truthbean.Logger;
import com.truthbean.LoggerFactory;

import java.io.Closeable;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Objects;
import java.util.UUID;

/**
 * @author TruthBean/Rogar·Q
 * @since 0.5.7
 */
public class TransactionInstance implements Closeable {
    private String id;
    private volatile boolean using;

    private volatile Connection connection;

    private volatile boolean closed;

    public TransactionInstance() {
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

    public Connection getConnection() {
        return connection;
    }

    public void setConnection(Connection connection) {
        this.connection = connection;
    }

    public Connection setAutoCommit(boolean autoCommit) {
        if (connection == null || closed) {
            return null;
        }
        try {
            this.connection.setAutoCommit(autoCommit);
        } catch (SQLException e) {
            LOGGER.error("set autocommit error.", e);
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
            LOGGER.error("set transaction isolation error.", e);
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
            LOGGER.error("set transaction isolation error.", e);
        }
        return this.connection;
    }

    public boolean isClosed() {
        return closed;
    }

    public void setClosed(boolean closed) {
        this.closed = closed;
    }

    public void commit() {
        // commit
        if (connection == null) {
            LOGGER.error("Connection is null!");
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
    }

    public void rollback() {
        if (connection == null) {
            LOGGER.error("Connection is null! ");
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
    }

    @Override
    public void close() {
        if (connection == null) {
            LOGGER.error("connection is null!");
            return;
        }

        LOGGER.trace(() -> id + ": close connection(" + connection + ") " + connection.hashCode() + " and remove it. ");
        try {
            if (!connection.isClosed()) {
                connection.close();
                connection = null;
            }
        } catch (SQLException e) {
            LOGGER.error("close connection(" + connection + ") " + connection.hashCode() + " error \n", e);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof TransactionInstance that))
            return false;
        return Objects.equals(getId(), that.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(TransactionInstance.class);
}
