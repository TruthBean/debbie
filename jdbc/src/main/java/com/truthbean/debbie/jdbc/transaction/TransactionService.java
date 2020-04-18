package com.truthbean.debbie.jdbc.transaction;

import com.truthbean.debbie.jdbc.datasource.DriverConnection;

import java.sql.Connection;

/**
 * @author TruthBean
 * @since 0.0.1
 */
public interface TransactionService {

    default TransactionInfo getTransaction() {
        TransactionInfo transactionInfo = TransactionManager.peek();
        if (transactionInfo == null) {
            throw new TransactionException("No debbie transaction");
        }
        return transactionInfo;
    }

    default Connection getConnection() {
        TransactionInfo transactionInfo = TransactionManager.peek();
        if (transactionInfo == null) {
            throw new TransactionException("No debbie transaction");
        }
        return transactionInfo.getConnection();
    }

    default DriverConnection getDriverConnection() {
        TransactionInfo transactionInfo = TransactionManager.peek();
        if (transactionInfo == null) {
            throw new TransactionException("No debbie transaction");
        }
        return transactionInfo.getDriverConnection();
    }
}
