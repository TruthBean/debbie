package com.truthbean.code.debbie.jdbc.repository;

import com.truthbean.code.debbie.core.bean.BeanInitializationHandler;
import com.truthbean.code.debbie.jdbc.datasource.DataSourceProperties;
import com.truthbean.code.debbie.jdbc.datasource.SingleDataSourceConnectionContext;
import com.truthbean.code.debbie.jdbc.transaction.TransactionException;
import com.truthbean.code.debbie.jdbc.transaction.TransactionIsolationLevel;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Timestamp;

/**
 * @author TruthBean
 * @since 0.0.1
 * Created on 2019/4/4 21:57.
 */
public class DmlRepositoryHandlerTest {
    private static SingleDataSourceConnectionContext connectionContext;

    private static DmlRepositoryHandler<Surname, Long> dmlRepositoryHandler;
    private static DdlRepositoryHandler ddlRepositoryHandler;

    @BeforeAll
    public static void before() {
        BeanInitializationHandler.init(Surname.class);

        var config = new DataSourceProperties().toConfiguration();
        connectionContext = SingleDataSourceConnectionContext.createInstance(config);
        connectionContext.setTransactionIsolationLevel(TransactionIsolationLevel.READ_COMMITTED);
        var connection = connectionContext.get();

        ddlRepositoryHandler = new DdlRepositoryHandler(connection);
        try {
            ddlRepositoryHandler.userDatabase("test");
            connectionContext.commit();
        } catch (TransactionException e) {
            e.printStackTrace();
            connectionContext.rollback();
        }

        dmlRepositoryHandler = new SurnameRepository(connection);
    }

    @AfterAll
    public static void after() {
        connectionContext.close();
    }

    @Test
    public void testInsert() throws MalformedURLException {
        var q = new Surname();
        q.setBegin(new Timestamp(System.currentTimeMillis()));
        q.setOrigin("姬");
        q.setWebsite(new URL("https://www.qu.org"));
        q.setName("璩");
        Long insert = null;
        try {
            insert = dmlRepositoryHandler.insert(q);
            connectionContext.commit();
        } catch (TransactionException e) {
            e.printStackTrace();
            connectionContext.rollback();
        }
        System.out.println(insert);
    }

    @Test
    public void testFindById() {
        Surname surname = dmlRepositoryHandler.findById(1L);
        System.out.println(surname);
    }

    @Test
    public void testUpdate() throws MalformedURLException {
        Surname surname = dmlRepositoryHandler.findById(1L);
        System.out.println(surname);
        surname.setWebsite(new URL("https://qu.org"));
        try {
            dmlRepositoryHandler.update(surname);
            connectionContext.commit();
        } catch (TransactionException e) {
            e.printStackTrace();
            connectionContext.rollback();
        }
        System.out.println(surname);
    }

    @Test
    public void testDeleteById() {
        int r = 0;
        try {
            r = dmlRepositoryHandler.deleteById(1L);
            connectionContext.commit();
        } catch (TransactionException e) {
            e.printStackTrace();
            connectionContext.rollback();
        }
        System.out.println(r == 1);
    }

    @Test
    public void findList() {
        var l = dmlRepositoryHandler.findList(null);
        System.out.println(l);
    }
}
