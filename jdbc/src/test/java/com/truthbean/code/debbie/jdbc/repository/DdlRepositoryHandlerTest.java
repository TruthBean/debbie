package com.truthbean.code.debbie.jdbc.repository;

import com.truthbean.code.debbie.core.bean.BeanInitializationHandler;
import com.truthbean.code.debbie.jdbc.datasource.DataSourceProperties;
import com.truthbean.code.debbie.jdbc.datasource.SingleDataSourceConnectionContext;
import com.truthbean.code.debbie.jdbc.transaction.TransactionException;
import com.truthbean.code.debbie.jdbc.transaction.TransactionIsolationLevel;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class DdlRepositoryHandlerTest {

    private static SingleDataSourceConnectionContext connectionContext;

    private static DdlRepositoryHandler ddlRepositoryHandler;

    @BeforeAll
    public static void before() {
        var config = new DataSourceProperties().toConfiguration();
        connectionContext = SingleDataSourceConnectionContext.createInstance(config);
        connectionContext.setAutoCommit(false);
        connectionContext.setTransactionIsolationLevel(TransactionIsolationLevel.NONE);

        var connection = connectionContext.get();
        ddlRepositoryHandler = new DdlRepositoryHandler(connection);
    }

    @AfterAll
    public static void after() {
        connectionContext.close();
    }

    @Test
    public void testCreateDatabase() {
        try {
            ddlRepositoryHandler.createDatabase("hello");
            connectionContext.commit();
        } catch (TransactionException e) {
            e.printStackTrace();
            connectionContext.rollback();
        }
    }

    @Test
    public void testShowDatabases() {
        var r = ddlRepositoryHandler.showDatabases();
        System.out.println(r);
    }

    @Test
    public void testDropDatabase() {
        try {
            ddlRepositoryHandler.dropDatabase("hello");
            connectionContext.commit();
        } catch (TransactionException e) {
            e.printStackTrace();
            connectionContext.rollback();
        }
    }

    @Test
    public void testShowTables() {
        try {
            ddlRepositoryHandler.userDatabase("mysql");
            connectionContext.commit();
        } catch (TransactionException e) {
            e.printStackTrace();
            connectionContext.rollback();
        }
        var s = ddlRepositoryHandler.showTables();
        System.out.println(s);
    }

    @Test
    public void testCreateTable() {
        try {
            ddlRepositoryHandler.userDatabase("test");

            BeanInitializationHandler.init(Surname.class);
            ddlRepositoryHandler.createTable(Surname.class);

            connectionContext.commit();
        } catch (TransactionException e) {
            e.printStackTrace();
            connectionContext.rollback();
        }
    }

    @Test
    public void dropTable() {
        try {
            ddlRepositoryHandler.userDatabase("test");
            ddlRepositoryHandler.dropTable("surname");

            connectionContext.commit();
        } catch (TransactionException e) {
            e.printStackTrace();
            connectionContext.rollback();
        }
    }

}
