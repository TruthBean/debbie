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
import com.truthbean.debbie.test.DebbieApplicationTest;
import org.junit.jupiter.api.Test;

import java.util.concurrent.ExecutionException;

@DebbieApplicationTest
class DdlRepoHandlerTest {

    @Test
    void testCreateDatabase(@BeanInject("dataSourceFactory") DataSourceFactory factory,
                                   @BeanInject DataSourceConfiguration configuration)
        throws ExecutionException, InterruptedException {
        var ddlRepositoryHandler = new DdlRepositoryHandler();
        /*var transaction = factory.getTransaction();
        var r = RepositoryCallback.asyncActionTransactional(transaction, () -> {
            var connection = transaction.getDriverConnection();
            return ddlRepositoryHandler.createDatabase(connection, "hello0");
        });
        System.out.println(r.get());*/

        // CREATE DATABASE IF NOT EXISTS hello DEFAULT CHARACTER SET 'utf8' DEFAULT COLLATE 'utf8_general_ci';
        String sql = DynamicRepository.modify(factory.getTransaction().getDriverConnection())
                .create().database().ifNotExists().$("hello").defaultCharacterSet("utf8").defaultCollate("utf8_general_ci")
                .toSql();
        System.out.println(sql);
        System.out.println("=========================================================");
    }

    @Test
    void testShowDatabases(@BeanInject("dataSourceFactory") DataSourceFactory factory,
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
    void testDropDatabase(@BeanInject("dataSourceFactory") DataSourceFactory factory,
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
    void testShowTables(@BeanInject("dataSourceFactory") DataSourceFactory factory,
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
    void testCreateTable(@BeanInject("dataSourceFactory") DataSourceFactory factory,
                                @BeanInject DataSourceConfiguration configuration) {
        var ddlRepositoryHandler = new DdlRepositoryHandler();
        var transaction = factory.getTransaction();
        var r = RepositoryCallback.actionTransactional(transaction, () -> {
            var connection = transaction.getDriverConnection();
            ddlRepositoryHandler.useDatabase(connection, "hello");
            // beanInitialization.init(Surname.class);
            // beanFactoryHandler.refreshBeans();
            ddlRepositoryHandler.createTable(connection, Surname.class);
            return ddlRepositoryHandler.showTables(connection);
        });
        System.out.println(r);
    }

    @Test
    void alterTable(@BeanInject("dataSourceFactory") DataSourceFactory factory) {
        var transaction = factory.getTransaction();
        // use table
        DynamicRepository.query(transaction.getDriverConnection())
                .use("hello")
                .execute();
        // ALTER TABLE `surname` ADD COLUMN `test` INT NULL AFTER `name`;
        DynamicRepository.modify(transaction.getDriverConnection())
                .alter().table("`surname`").add().column("`test1`").intDeFaultNull().after("`name`")
                .execute();
    }

    @Test
    void dropTable(@BeanInject("dataSourceFactory") DataSourceFactory factory,
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
