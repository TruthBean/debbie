/**
 * Copyright (c) 2026 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *         http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.check.jdbc.repository;

import com.truthbean.Console;
import com.truthbean.Logger;
import com.truthbean.LoggerFactory;
import com.truthbean.debbie.bean.BeanInject;
import com.truthbean.debbie.check.jdbc.entity.Surname;
import com.truthbean.debbie.jdbc.datasource.DataSourceConfiguration;
import com.truthbean.debbie.jdbc.datasource.DataSourceFactory;
import com.truthbean.debbie.jdbc.repository.DdlRepositoryHandler;
import com.truthbean.debbie.jdbc.repository.DynamicRepository;
import com.truthbean.debbie.jdbc.repository.RepositoryCallback;
import com.truthbean.debbie.jdbc.repository.RepositoryHandler;
import com.truthbean.debbie.test.annotation.DebbieApplicationTest;
import org.junit.jupiter.api.Test;

import java.util.concurrent.ExecutionException;

@DebbieApplicationTest
class DdlRepoHandlerTest {

    @Test
    void testCreateDatabase(@BeanInject("dataSourceFactory") DataSourceFactory factory,
                                   @BeanInject DataSourceConfiguration configuration)
        throws ExecutionException, InterruptedException {
        var ddlRepositoryHandler = DdlRepositoryHandler.INSTANCE;
        /*var transaction = factory.getTransaction();
        var r = RepositoryCallback.asyncActionTransactional(transaction, () -> {
            var connection = transaction.getDriverConnection();
            return ddlRepositoryHandler.createDatabase(connection, "hello0");
        });
        Console.println(r.get());*/

        // CREATE DATABASE IF NOT EXISTS hello DEFAULT_PROFILE CHARACTER SET 'utf8' DEFAULT_PROFILE COLLATE 'utf8_general_ci';
        String sql = DynamicRepository.modify(factory.getTransaction()).sqlBuilder()
                .create().database().ifNotExists().$("hello").defaultCharacterSet("utf8").defaultCollate("utf8_general_ci")
                .toSql();
        Console.println(sql);
        Console.println("=========================================================");
    }

    @Test
    void testShowDatabases(@BeanInject("dataSourceFactory") DataSourceFactory factory,
                                  @BeanInject DataSourceConfiguration configuration) {
        var ddlRepositoryHandler = DdlRepositoryHandler.INSTANCE;
        var transaction = factory.getTransaction();
        var r = RepositoryCallback.action(transaction, () -> {
            return ddlRepositoryHandler.showDatabases(LOGGER, transaction);
        });
        Console.println(r);
    }

    @Test
    void testDropDatabase(@BeanInject("dataSourceFactory") DataSourceFactory factory,
                                 @BeanInject DataSourceConfiguration configuration) {
        var ddlRepositoryHandler = DdlRepositoryHandler.INSTANCE;
        var transaction = factory.getTransaction();
        var r = RepositoryCallback.actionTransactional(transaction, () -> {
            return ddlRepositoryHandler.dropDatabase(LOGGER, transaction, "hello");
        });
        Console.println(r);
    }

    @Test
    void testShowTables(@BeanInject("dataSourceFactory") DataSourceFactory factory,
                               @BeanInject DataSourceConfiguration configuration) {
        var ddlRepositoryHandler = DdlRepositoryHandler.INSTANCE;
        var transaction = factory.getTransaction();
        var r = RepositoryCallback.actionTransactional(transaction, () -> {
            ddlRepositoryHandler.useDatabase(LOGGER, transaction, "mysql");
            return ddlRepositoryHandler.showTables(LOGGER, transaction);
        });
        Console.println(r);
    }

    @Test
    void testCreateTable(@BeanInject("dataSourceFactory") DataSourceFactory factory,
                                @BeanInject DataSourceConfiguration configuration) {
        var ddlRepositoryHandler = DdlRepositoryHandler.INSTANCE;
        var transaction = factory.getTransaction();
        var r = RepositoryCallback.actionTransactional(transaction, () -> {
            ddlRepositoryHandler.useDatabase(LOGGER, transaction, "test");
            // beanInitialization.init(Surname.class);
            // beanFactoryHandler.refreshBeans();
            ddlRepositoryHandler.createTable(LOGGER, transaction, Surname.class);
            return ddlRepositoryHandler.showTables(LOGGER, transaction);
        });
        Console.println(r);
    }

    @Test
    void alterTable(@BeanInject("dataSourceFactory") DataSourceFactory factory) {
        var transaction = factory.getTransaction();
        // use table
        DynamicRepository.query(transaction)
                .sqlBuilder()
                .use("hello")
                .repository()
                .execute(LOGGER);
        // ALTER TABLE `surname` ADD COLUMN `test` INT NULL AFTER `name`;
        DynamicRepository.modify(transaction)
                .sqlBuilder()
                .alter().table("`surname`").add().column("`test1`").intDeFaultNull().after("`name`")
                .repository()
                .execute(LOGGER);
    }

    @Test
    void dropTable(@BeanInject("dataSourceFactory") DataSourceFactory factory,
                          @BeanInject DataSourceConfiguration configuration) {
        var ddlRepositoryHandler = DdlRepositoryHandler.INSTANCE;
        var transaction = factory.getTransaction();
        var r = RepositoryCallback.actionTransactional(transaction, () -> {
            ddlRepositoryHandler.useDatabase(LOGGER, transaction, "test");
            ddlRepositoryHandler.dropTable(LOGGER, transaction, "surname");
            return ddlRepositoryHandler.showTables(LOGGER, transaction);
        });
        Console.println(r);
    }

    @Test
    void truncateTable(@BeanInject("dataSourceFactory") DataSourceFactory factory) {
        DynamicRepository.modify(factory.getTransaction())
                .sqlBuilder()
                .truncate().table("surname")
                .repository()
                .execute(LOGGER);
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(DdlRepoHandlerTest.class);
}
