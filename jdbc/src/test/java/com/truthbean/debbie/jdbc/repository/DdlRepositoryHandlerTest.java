package com.truthbean.debbie.jdbc.repository;

import com.truthbean.debbie.bean.BeanFactoryContext;
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

public class DdlRepositoryHandlerTest {
    private static DdlRepositoryHandler ddlRepositoryHandler;
    private static DataSourceFactory factory;

    @BeforeAll
    public static void before() {
        var applicationFactory = DebbieApplicationFactory.configure(DataSourceConfigurationTest.class);

        BeanFactoryContext applicationContext = applicationFactory.getBeanFactoryHandler();
        var config = DataSourceProperties.toConfiguration();
        factory = new DefaultDataSourceFactory();
        factory.factory(config);

        ddlRepositoryHandler = new DdlRepositoryHandler();
    }

    @AfterAll
    public static void after() {

    }

    @Test
    public void testCreateDatabase() throws ExecutionException, InterruptedException {
        var transaction = factory.getTransaction();
        var r = RepositoryCallback.asyncActionTransactional(transaction, () -> {
            var connection = transaction.getDriverConnection();
            return ddlRepositoryHandler.createDatabase(connection, "hello");
        });
        System.out.println(r.get());
    }

    @Test
    public void testShowDatabases() {
        var transaction = factory.getTransaction();
        var r = RepositoryCallback.action(transaction, () -> {
            var connection = transaction.getDriverConnection();
            return ddlRepositoryHandler.showDatabases(connection);
        });
        System.out.println(r);
    }

    @Test
    public void testDropDatabase() {
        var transaction = factory.getTransaction();
        var r = RepositoryCallback.actionTransactional(transaction, () -> {
            var connection = transaction.getDriverConnection();
            return ddlRepositoryHandler.dropDatabase(connection, "hello");
        });
        System.out.println(r);
    }

    @Test
    public void testShowTables() {
        var transaction = factory.getTransaction();
        var r = RepositoryCallback.actionTransactional(transaction, () -> {
            var connection = transaction.getDriverConnection();
            ddlRepositoryHandler.useDatabase(connection, "mysql");
            return ddlRepositoryHandler.showTables(connection);
        });
        System.out.println(r);
    }

    @Test
    public void testCreateTable() {
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
    public void dropTable() {
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
