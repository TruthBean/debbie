package com.truthbean.debbie.jdbc.transaction;

import com.truthbean.debbie.jdbc.datasource.DataSourceFactory;

import java.util.Objects;

/**
 * @author TruthBean/Rogar·Q
 * @since 0.6.0
 */
public interface DefaultTransactionProvider extends TransactionService {
    @Override
    default TransactionInfo getTransaction() {
        TransactionInfo transactionInfo = TransactionManager.peek();
        if (transactionInfo == null) {
            transactionInfo = new TransactionInfo();
            if (!Objects.equals(TransactionManager.peek(), transactionInfo)) {
                TransactionManager.offer(transactionInfo);
            }
        }
        if (transactionInfo.isNoInstance()) {
            DataSourceFactory factory = getDataSourceFactory();
            if (factory != null)
                transactionInfo.setConnection(factory.getConnection());
        }
        transactionInfo.setAutoCommit(true);
        transactionInfo.setForceCommit(false);
        transactionInfo.setRollbackFor(Exception.class);
        return transactionInfo;
    }

    DataSourceFactory getDataSourceFactory();
}
