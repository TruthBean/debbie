/**
 * Copyright (c) 2022 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *         http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.check.jdbc.transaction;

import com.truthbean.debbie.bean.BeanInject;
import com.truthbean.debbie.jdbc.datasource.DataSourceFactory;
import com.truthbean.debbie.jdbc.repository.RepositoryCallback;
import com.truthbean.debbie.jdbc.transaction.TransactionManager;
import com.truthbean.debbie.test.annotation.DebbieApplicationTest;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

@DebbieApplicationTest
class ResourceHolderTest {

    @Test
    void testTransaction(@BeanInject("dataSourceFactory") DataSourceFactory factory) {
        var transaction = factory.getTransaction();
        transaction.registerResourceHolder(new PrintResourceHolder());
        var r = RepositoryCallback.actionTransactional(transaction, () -> {
            var connection = transaction.getConnection();
            return connection.getClientInfo();
        });
        System.out.println(r);
    }

    @Test
    void testTransactionManager(@BeanInject("dataSourceFactory") DataSourceFactory factory) {
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