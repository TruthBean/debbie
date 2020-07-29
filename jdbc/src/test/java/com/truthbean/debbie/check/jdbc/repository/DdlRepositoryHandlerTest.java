/**
 * Copyright (c) 2020 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *         http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.check.jdbc.repository;

import com.truthbean.debbie.check.jdbc.datasource.DataSourceConfigurationTest;
import com.truthbean.debbie.check.jdbc.entity.Surname;
import com.truthbean.debbie.core.ApplicationContext;
import com.truthbean.debbie.core.ApplicationFactory;
import com.truthbean.debbie.jdbc.datasource.DataSourceFactory;
import com.truthbean.debbie.jdbc.datasource.DataSourceProperties;
import com.truthbean.debbie.jdbc.datasource.DefaultDataSourceFactory;
import com.truthbean.debbie.jdbc.repository.DdlRepositoryHandler;
import com.truthbean.debbie.jdbc.repository.RepositoryCallback;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.concurrent.ExecutionException;

class DdlRepositoryHandlerTest {
    private static DdlRepositoryHandler ddlRepositoryHandler;
    private static DataSourceFactory factory;

    @BeforeAll
    public static void before() {
        var applicationFactory = ApplicationFactory.configure(DataSourceConfigurationTest.class);

        ApplicationContext applicationContext = applicationFactory.getApplicationContext();
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
        var r = RepositoryCallback.asyncActionTransactional(transaction,
                () -> ddlRepositoryHandler.createDatabase(transaction, "hello"));
        System.out.println(r.get());
    }

    @Test
    void testShowDatabases() {
        var transaction = factory.getTransaction();
        var r = RepositoryCallback.action(transaction,
                () -> ddlRepositoryHandler.showDatabases(transaction));
        System.out.println(r);
    }

    @Test
    void testDropDatabase() {
        var transaction = factory.getTransaction();
        var r = RepositoryCallback.actionTransactional(transaction,
                () -> ddlRepositoryHandler.dropDatabase(transaction, "hello"));
        System.out.println(r);
    }

    @Test
    void testShowTables() {
        var transaction = factory.getTransaction();
        var r = RepositoryCallback.actionTransactional(transaction, () -> {
            ddlRepositoryHandler.useDatabase(transaction, "mysql");
            return ddlRepositoryHandler.showTables(transaction);
        });
        System.out.println(r);
    }

    @Test
    void testCreateTable() {
        var transaction = factory.getTransaction();
        var r = RepositoryCallback.actionTransactional(transaction, () -> {
            // ddlRepositoryHandler.useDatabase(transaction, "test");
            // beanInitialization.init(Surname.class);
            // beanFactoryHandler.refreshBeans();
            ddlRepositoryHandler.createTable(transaction, Surname.class);
            return ""; // ddlRepositoryHandler.showTables(transaction);
        });
        System.out.println(r);
    }

    @Test
    void dropTable() {
        var transaction = factory.getTransaction();
        var r = RepositoryCallback.actionTransactional(transaction, () -> {
            ddlRepositoryHandler.useDatabase(transaction, "test");
            ddlRepositoryHandler.dropTable(transaction, "surname");
            return ddlRepositoryHandler.showTables(transaction);
        });
        System.out.println(r);
    }

}
