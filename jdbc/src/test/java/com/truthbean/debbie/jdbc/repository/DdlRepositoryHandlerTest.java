package com.truthbean.debbie.jdbc.repository;

import com.truthbean.debbie.core.bean.BeanInitialization;
import com.truthbean.debbie.jdbc.datasource.DataSourceContext;
import com.truthbean.debbie.jdbc.datasource.DataSourceFactory;
import com.truthbean.debbie.jdbc.datasource.DataSourceProperties;
import com.truthbean.debbie.jdbc.datasource.DefaultDataSourceFactory;
import com.truthbean.debbie.jdbc.entity.Surname;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.concurrent.ExecutionException;

public class DdlRepositoryHandlerTest {

    private BeanInitialization beanInitialization = new BeanInitialization();

    private static DdlRepositoryHandler ddlRepositoryHandler;
    private static DataSourceFactory factory;

    @BeforeAll
    public static void before() {
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
        var connection = factory.getConnection();
        var r = RepositoryAction.asyncActionTransactional(connection, () ->
                ddlRepositoryHandler.createDatabase(connection, "hello"));
        System.out.println(r.get());
    }

    @Test
    public void testShowDatabases() {
        var connection = factory.getConnection();
        var r = RepositoryAction.action(connection, () -> ddlRepositoryHandler.showDatabases(connection));
        System.out.println(r);
    }

    @Test
    public void testDropDatabase() {
        var connection = factory.getConnection();
        var r = RepositoryAction.actionTransactional(connection, () ->
                ddlRepositoryHandler.dropDatabase(connection, "hello")
        );
        System.out.println(r);
    }

    @Test
    public void testShowTables() {
        var connection = factory.getConnection();
        var r = RepositoryAction.actionTransactional(connection, () -> {
            ddlRepositoryHandler.userDatabase(connection, "mysql");
            return ddlRepositoryHandler.showTables(connection);
        });
        System.out.println(r);
    }

    @Test
    public void testCreateTable() {
        var connection = factory.getConnection();
        var r = RepositoryAction.actionTransactional(connection, () -> {
            ddlRepositoryHandler.userDatabase(connection, "test");
            beanInitialization.init(Surname.class);
            ddlRepositoryHandler.createTable(connection, Surname.class);
            return ddlRepositoryHandler.showTables(connection);
        });
        System.out.println(r);
    }

    @Test
    public void dropTable() {
        var connection = factory.getConnection();
        var r = RepositoryAction.actionTransactional(connection, () -> {
            ddlRepositoryHandler.userDatabase(connection, "test");
            ddlRepositoryHandler.dropTable(connection, "surname");
            return ddlRepositoryHandler.showTables(connection);
        });
        System.out.println(r);
    }

}
