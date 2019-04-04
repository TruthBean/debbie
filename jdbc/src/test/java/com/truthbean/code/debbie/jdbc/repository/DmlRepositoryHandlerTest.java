package com.truthbean.code.debbie.jdbc.repository;

import com.truthbean.code.debbie.core.bean.BeanInitializationHandler;
import com.truthbean.code.debbie.jdbc.datasource.SingleDataSourceConnectionContext;
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
    private static SingleDataSourceConnectionContext connectionContext = SingleDataSourceConnectionContext.getInstance();

    private static DmlRepositoryHandler dmlRepositoryHandler;
    private static DdlRepositoryHandler ddlRepositoryHandler;

    @BeforeAll
    public static void before() {
        connectionContext.initConnectionAndBind();
        var connection = connectionContext.get();
        dmlRepositoryHandler = new DmlRepositoryHandler(connection);
        ddlRepositoryHandler = new DdlRepositoryHandler(connection);
    }

    @AfterAll
    public static void after() {
        connectionContext.close();
    }

    @Test
    public void testInsert() throws MalformedURLException {
        BeanInitializationHandler.init(Qu.class);
        var r = ddlRepositoryHandler.userDatabase("test");
        System.out.println(r);
        var q = new Qu();
        q.setBegin(new Timestamp(System.currentTimeMillis()));
        q.setOrigin("姬");
        q.setWebsite(new URL("https://www.qu.org"));
        Long insert = dmlRepositoryHandler.insert(q);
        System.out.println(insert);
    }
}
