/**
 * Copyright (c) 2020 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *         http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.jdbc.repository;

import com.truthbean.debbie.bean.BeanInject;
import com.truthbean.debbie.jdbc.datasource.DataSourceConfiguration;
import com.truthbean.debbie.jdbc.datasource.DataSourceFactory;
import com.truthbean.debbie.jdbc.entity.Surname;
import com.truthbean.debbie.test.DebbieApplicationExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.concurrent.ExecutionException;

@ExtendWith({DebbieApplicationExtension.class})
public class DdlRepoHandlerTest {

    @Test
    public void testCreateDatabase(@BeanInject("dataSourceFactory") DataSourceFactory factory,
                                   @BeanInject DataSourceConfiguration configuration)
        throws ExecutionException, InterruptedException {
        var ddlRepositoryHandler = new DdlRepositoryHandler();
        var transaction = factory.getTransaction();
        var r = RepositoryCallback.asyncActionTransactional(transaction, () -> {
            var connection = transaction.getConnection();
            return ddlRepositoryHandler.createDatabase(transaction.getDriverConnection(), "hello");
        });
        System.out.println(r.get());
    }

    @Test
    public void testShowDatabases(@BeanInject("dataSourceFactory") DataSourceFactory factory,
                                  @BeanInject DataSourceConfiguration configuration) {
        var ddlRepositoryHandler = new DdlRepositoryHandler();
        var transaction = factory.getTransaction();
        var r = RepositoryCallback.action(transaction, () -> {
            var connection = transaction.getConnection();
            return ddlRepositoryHandler.showDatabases(transaction.getDriverConnection());
        });
        System.out.println(r);
    }

    @Test
    public void testDropDatabase(@BeanInject("dataSourceFactory") DataSourceFactory factory,
                                 @BeanInject DataSourceConfiguration configuration) {
        var ddlRepositoryHandler = new DdlRepositoryHandler();
        var transaction = factory.getTransaction();
        var r = RepositoryCallback.actionTransactional(transaction, () -> {
            var connection = transaction.getConnection();
            return ddlRepositoryHandler.dropDatabase(transaction.getDriverConnection(), "hello");
        });
        System.out.println(r);
    }

    @Test
    public void testShowTables(@BeanInject("dataSourceFactory") DataSourceFactory factory,
                               @BeanInject DataSourceConfiguration configuration) {
        var ddlRepositoryHandler = new DdlRepositoryHandler();
        var transaction = factory.getTransaction();
        var r = RepositoryCallback.actionTransactional(transaction, () -> {
            var connection = transaction.getDriverConnection();
            ddlRepositoryHandler.useDatabase(connection, "mysql");
            return ddlRepositoryHandler.showTables(connection);
        });
        System.out.println(r);
    }

    @Test
    public void testCreateTable(@BeanInject("dataSourceFactory") DataSourceFactory factory,
                                @BeanInject DataSourceConfiguration configuration) {
        var ddlRepositoryHandler = new DdlRepositoryHandler();
        var transaction = factory.getTransaction();
        var r = RepositoryCallback.actionTransactional(transaction, () -> {
            var connection = transaction.getDriverConnection();
            ddlRepositoryHandler.useDatabase(connection, "test");
            // beanInitialization.init(Surname.class);
            // beanFactoryHandler.refreshBeans();
            ddlRepositoryHandler.createTable(connection, Surname.class);
            return ddlRepositoryHandler.showTables(connection);
        });
        System.out.println(r);
    }

    @Test
    public void dropTable(@BeanInject("dataSourceFactory") DataSourceFactory factory,
                          @BeanInject DataSourceConfiguration configuration) {
        var ddlRepositoryHandler = new DdlRepositoryHandler();
        var transaction = factory.getTransaction();
        var r = RepositoryCallback.actionTransactional(transaction, () -> {
            var connection = transaction.getDriverConnection();
            ddlRepositoryHandler.useDatabase(connection, "test");
            ddlRepositoryHandler.dropTable(connection, "surname");
            return ddlRepositoryHandler.showTables(connection);
        });
        System.out.println(r);
    }
}
