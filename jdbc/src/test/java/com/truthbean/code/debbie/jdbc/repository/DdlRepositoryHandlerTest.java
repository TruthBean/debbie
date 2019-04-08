package com.truthbean.code.debbie.jdbc.repository;

import com.truthbean.code.debbie.core.bean.BeanInitializationHandler;
import com.truthbean.code.debbie.jdbc.datasource.SingleDataSourceConnectionContext;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class DdlRepositoryHandlerTest {

    private static SingleDataSourceConnectionContext connectionContext = SingleDataSourceConnectionContext.getInstance();

    private static DdlRepositoryHandler ddlRepositoryHandler;

    @BeforeAll
    public static void before() {
        connectionContext.initConnectionAndBind();
        var connection = connectionContext.get();
        ddlRepositoryHandler = new DdlRepositoryHandler(connection);
    }

    @AfterAll
    public static void after() {
        connectionContext.close();
    }

    @Test
    public void testCreateDatabase() {
        ddlRepositoryHandler.createDatabase("hello");
    }

    @Test
    public void testShowDatabases() {
        var r = ddlRepositoryHandler.showDatabases();
        System.out.println(r);
    }

    @Test
    public void testDropDatabase() {
        ddlRepositoryHandler.dropDatabase("hello");
    }

    @Test
    public void testShowTables() {
        ddlRepositoryHandler.userDatabase("mysql");
        var s = ddlRepositoryHandler.showTables();
        System.out.println(s);
    }

    @Test
    public void testCreateTable() {
        ddlRepositoryHandler.userDatabase("test");
        BeanInitializationHandler.init(Surname.class);
        ddlRepositoryHandler.createTable(Surname.class);
    }

    @Test
    public void dropTable() {
        ddlRepositoryHandler.userDatabase("test");
        ddlRepositoryHandler.dropTable("surname");
    }

}
