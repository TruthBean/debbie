package com.truthbean.debbie.jdbc.repository;

import com.truthbean.debbie.core.bean.BeanInitializationHandler;
import com.truthbean.debbie.jdbc.datasource.*;
import com.truthbean.debbie.jdbc.entity.Surname;
import com.truthbean.debbie.jdbc.transaction.TransactionException;
import com.truthbean.debbie.jdbc.transaction.TransactionIsolationLevel;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

public class DdlRepositoryHandlerTest {

    private static DdlRepositoryHandler ddlRepositoryHandler;

    @BeforeAll
    public static void before() throws SQLException {
        var config = new DataSourceProperties().toConfiguration();
        DataSourceFactory factory = new DefaultDataSourceFactory();
        factory.factory(config);

        var connection = factory.getDataSource().getConnection();

        ddlRepositoryHandler = new DdlRepositoryHandler(connection);
    }

    @AfterAll
    public static void after() {
        DataSourceContext.closeConnection();
    }

    @Test
    public void testCreateDatabase() {
        try {
            ddlRepositoryHandler.createDatabase("hello");
            ddlRepositoryHandler.commit();
        } catch (TransactionException e) {
            e.printStackTrace();
            ddlRepositoryHandler.rollback();
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
            ddlRepositoryHandler.commit();
        } catch (TransactionException e) {
            e.printStackTrace();
            ddlRepositoryHandler.rollback();
        }
    }

    @Test
    public void testShowTables() {
        try {
            ddlRepositoryHandler.userDatabase("mysql");
            ddlRepositoryHandler.commit();
        } catch (TransactionException e) {
            e.printStackTrace();
            ddlRepositoryHandler.rollback();
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

            ddlRepositoryHandler.commit();
        } catch (TransactionException e) {
            e.printStackTrace();
            ddlRepositoryHandler.rollback();
        }
    }

    @Test
    public void dropTable() {
        try {
            ddlRepositoryHandler.userDatabase("test");
            ddlRepositoryHandler.dropTable("surname");

            ddlRepositoryHandler.commit();
        } catch (TransactionException e) {
            e.printStackTrace();
            ddlRepositoryHandler.rollback();
        }
    }

}
