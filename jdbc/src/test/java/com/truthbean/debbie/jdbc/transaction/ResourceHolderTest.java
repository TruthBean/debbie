package com.truthbean.debbie.jdbc.transaction;

import com.truthbean.debbie.bean.BeanInject;
import com.truthbean.debbie.jdbc.datasource.DataSourceFactory;
import com.truthbean.debbie.jdbc.repository.RepositoryCallback;
import com.truthbean.debbie.test.DebbieApplicationExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.sql.SQLException;

@ExtendWith({DebbieApplicationExtension.class})
class ResourceHolderTest {

    @Test
    public void testTransaction(@BeanInject("dataSourceFactory") DataSourceFactory factory) {
        var transaction = factory.getTransaction();
        transaction.registerResourceHolder(new PrintResourceHolder());
        var r = RepositoryCallback.actionTransactional(transaction, () -> {
            var connection = transaction.getConnection();
            return connection.getClientInfo();
        });
        System.out.println(r);
    }

    @Test
    public void testTransactionManager(@BeanInject("dataSourceFactory") DataSourceFactory factory) {
        var transaction = factory.getTransaction();
        TransactionManager.registerResourceHolder(new Print2ResourceHolder());
        TransactionManager.registerResourceHolder(new PrintResourceHolder());
        TransactionManager.offer(transaction);
        var r = RepositoryCallback.actionTransactional(transactionInfo -> {
            var connection = transactionInfo.getConnection();
            try {
                return connection.getClientInfo();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return null;
        });
        System.out.println(r);
    }
}