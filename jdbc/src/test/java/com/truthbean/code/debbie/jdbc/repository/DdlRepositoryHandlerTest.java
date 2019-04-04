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
        var r = ddlRepositoryHandler.createDatabase("hello");
        System.out.println(r);
    }

    @Test
    public void testShowDatabases() {
        var r = ddlRepositoryHandler.showDatabases();
        System.out.println(r);
    }

    @Test
    public void testDropDatabase() {
        var r = ddlRepositoryHandler.dropDatabase("hello");
        System.out.println(r);
    }

    @Test
    public void testShowTables() {
        var r = ddlRepositoryHandler.userDatabase("mysql");
        System.out.println(r);
        var s = ddlRepositoryHandler.showTables();
        System.out.println(s);
    }

    @Test
    public void testCreateTable() {
        var r = ddlRepositoryHandler.userDatabase("test");
        System.out.println(r);
        BeanInitializationHandler.init(Qu.class);
        ddlRepositoryHandler.createTable(Qu.class);
    }

    @Test
    public void dropTable() {
        var r = ddlRepositoryHandler.userDatabase("test");
        System.out.println(r);
        var s = ddlRepositoryHandler.dropTable("qu");
        System.out.println(s);
    }

}
