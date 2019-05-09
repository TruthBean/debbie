package com.truthbean.debbie.jdbc.datasource.connection;

import java.sql.Connection;

/**
 * @author TruthBean
 * @since 0.0.1
 */
public interface ConnectionService {

    default Connection getConnection() {
        return ConnectionBinder.get();
    }
}
