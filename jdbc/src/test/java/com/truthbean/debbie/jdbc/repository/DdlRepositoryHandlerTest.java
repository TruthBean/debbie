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

import com.truthbean.debbie.bean.DebbieApplicationContext;
import com.truthbean.debbie.boot.DebbieApplicationFactory;
import com.truthbean.debbie.jdbc.datasource.DataSourceConfigurationTest;
import com.truthbean.debbie.jdbc.datasource.DataSourceFactory;
import com.truthbean.debbie.jdbc.datasource.DataSourceProperties;
import com.truthbean.debbie.jdbc.datasource.DefaultDataSourceFactory;
import com.truthbean.debbie.jdbc.entity.Surname;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.concurrent.ExecutionException;

class DdlRepositoryHandlerTest {
    private static DdlRepositoryHandler ddlRepositoryHandler;
    private static DataSourceFactory factory;

    @BeforeAll
    public static void before() {
        var applicationFactory = DebbieApplicationFactory.configure(DataSourceConfigurationTest.class);

        DebbieApplicationContext applicationContext = applicationFactory.getApplicationContext();
        var config = DataSourceProperties.toConfiguration();
        factory = new DefaultDataSourceFactory();
        factory.factory(config);

        ddlRepositoryHandler = new DdlRepositoryHandler();
    }

    @AfterAll
    public static void after() {

    }

    @Test
    void testCreateDatabase() throws ExecutionException, InterruptedException {
        var transaction = factory.getTransaction();
        var r = RepositoryCallback.asyncActionTransactional(transaction, () -> {
            var connection = transaction.getDriverConnection();
            return ddlRepositoryHandler.createDatabase(connection, "hello");
        });
        System.out.println(r.get());
    }

    @Test
    void testShowDatabases() {
        var transaction = factory.getTransaction();
        var r = RepositoryCallback.action(transaction, () -> {
            var connection = transaction.getDriverConnection();
            return ddlRepositoryHandler.showDatabases(connection);
        });
        System.out.println(r);
    }

    @Test
    void testDropDatabase() {
        var transaction = factory.getTransaction();
        var r = RepositoryCallback.actionTransactional(transaction, () -> {
            var connection = transaction.getDriverConnection();
            return ddlRepositoryHandler.dropDatabase(connection, "hello");
        });
        System.out.println(r);
    }

    @Test
    void testShowTables() {
        var transaction = factory.getTransaction();
        var r = RepositoryCallback.actionTransactional(transaction, () -> {
            var connection = transaction.getDriverConnection();
            ddlRepositoryHandler.useDatabase(connection, "mysql");
            return ddlRepositoryHandler.showTables(connection);
        });
        System.out.println(r);
    }

    @Test
    void testCreateTable() {
        var transaction = factory.getTransaction();
        var r = RepositoryCallback.actionTransactional(transaction, () -> {
            var connection = transaction.getDriverConnection();
            // ddlRepositoryHandler.useDatabase(connection, "test");
            // beanInitialization.init(Surname.class);
            // beanFactoryHandler.refreshBeans();
            ddlRepositoryHandler.createTable(connection, Surname.class);
            return ""; // ddlRepositoryHandler.showTables(connection);
        });
        System.out.println(r);
    }

    @Test
    void dropTable() {
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
