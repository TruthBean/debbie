package com.truthbean.debbie.jdbc.repository;

import com.truthbean.debbie.bean.BeanInject;
import com.truthbean.debbie.jdbc.datasource.DataSourceFactory;
import com.truthbean.debbie.jdbc.entity.Surname;
import com.truthbean.debbie.test.DebbieApplicationExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.concurrent.ExecutionException;

@ExtendWith({DebbieApplicationExtension.class})
public class DdlRepoHandlerTest {

    @Test
    public void testCreateDatabase(@BeanInject("dataSourceFactory") DataSourceFactory factory)
        throws ExecutionException, InterruptedException {
        var ddlRepositoryHandler = new DdlRepositoryHandler();
        var transaction = factory.getTransaction();
        var r = RepositoryCallback.asyncActionTransactional(transaction, () -> {
            var connection = transaction.getConnection();
            return ddlRepositoryHandler.createDatabase(connection, "hello");
        });
        System.out.println(r.get());
    }

    @Test
    public void testShowDatabases(@BeanInject("dataSourceFactory") DataSourceFactory factory) {
        var ddlRepositoryHandler = new DdlRepositoryHandler();
        var transaction = factory.getTransaction();
        var r = RepositoryCallback.action(transaction, () -> {
            var connection = transaction.getConnection();
            return ddlRepositoryHandler.showDatabases(connection);
        });
        System.out.println(r);
    }

    @Test
    public void testDropDatabase(@BeanInject("dataSourceFactory") DataSourceFactory factory) {
        var ddlRepositoryHandler = new DdlRepositoryHandler();
        var transaction = factory.getTransaction();
        var r = RepositoryCallback.actionTransactional(transaction, () -> {
            var connection = transaction.getConnection();
            return ddlRepositoryHandler.dropDatabase(connection, "hello");
        });
        System.out.println(r);
    }

    @Test
    public void testShowTables(@BeanInject("dataSourceFactory") DataSourceFactory factory) {
        var ddlRepositoryHandler = new DdlRepositoryHandler();
        var transaction = factory.getTransaction();
        var r = RepositoryCallback.actionTransactional(transaction, () -> {
            var connection = transaction.getConnection();
            ddlRepositoryHandler.userDatabase(connection, "mysql");
            return ddlRepositoryHandler.showTables(connection);
        });
        System.out.println(r);
    }

    @Test
    public void testCreateTable(@BeanInject("dataSourceFactory") DataSourceFactory factory) {
        var ddlRepositoryHandler = new DdlRepositoryHandler();
        var transaction = factory.getTransaction();
        var r = RepositoryCallback.actionTransactional(transaction, () -> {
            var connection = transaction.getConnection();
            ddlRepositoryHandler.userDatabase(connection, "test");
            // beanInitialization.init(Surname.class);
            // beanFactoryHandler.refreshBeans();
            ddlRepositoryHandler.createTable(connection, Surname.class);
            return ddlRepositoryHandler.showTables(connection);
        });
        System.out.println(r);
    }

    @Test
    public void dropTable(@BeanInject("dataSourceFactory") DataSourceFactory factory) {
        var ddlRepositoryHandler = new DdlRepositoryHandler();
        var transaction = factory.getTransaction();
        var r = RepositoryCallback.actionTransactional(transaction, () -> {
            var connection = transaction.getConnection();
            ddlRepositoryHandler.userDatabase(connection, "test");
            ddlRepositoryHandler.dropTable(connection, "surname");
            return ddlRepositoryHandler.showTables(connection);
        });
        System.out.println(r);
    }
}
