package com.truthbean.debbie.jdbc.transaction;

import java.sql.Connection;

/**
 * @author TruthBean
 * @since 0.0.1
 */
public interface TransactionService {

    default TransactionInfo getTransaction() {
        return TransactionManager.peek();
    }

    default Connection getConnection() {
        return TransactionManager.peek().getConnection();
    }
}
