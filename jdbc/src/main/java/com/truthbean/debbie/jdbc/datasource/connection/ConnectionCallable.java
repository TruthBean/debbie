package com.truthbean.debbie.jdbc.datasource.connection;

import java.sql.Connection;

public interface ConnectionCallable<V> {

    default Connection getConnection() {
        return ConnectionManager.get();
    }

    V call(Connection connection);
}
