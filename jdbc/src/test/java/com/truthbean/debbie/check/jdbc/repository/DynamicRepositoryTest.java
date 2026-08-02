package com.truthbean.debbie.check.jdbc.repository;

import com.truthbean.Console;
import com.truthbean.Logger;
import com.truthbean.LoggerFactory;
import com.truthbean.debbie.bean.BeanInject;
import com.truthbean.debbie.jdbc.column.ColumnInfo;
import com.truthbean.debbie.jdbc.datasource.DataSourceFactory;
import com.truthbean.debbie.jdbc.repository.DmlRepositoryHandler;
import com.truthbean.debbie.jdbc.repository.DynamicRepository;
import com.truthbean.debbie.jdbc.repository.JdbcTransactionRepository;
import com.truthbean.debbie.jdbc.repository.RepositoryHandler;
import com.truthbean.debbie.test.annotation.DebbieApplicationTest;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

@DebbieApplicationTest
class DynamicRepositoryTest {

    @Test
    void test(@BeanInject("dataSourceFactory") DataSourceFactory factory) {
        var driver = factory.getDriverName();
        var transaction = factory.getTransaction();
        String sql = DynamicRepository.sqlBuilder(driver)
                .select("id", "name").from("railway.seat").orderBy("id").desc()
                .build();
        RepositoryHandler repositoryHandler = RepositoryHandler.INSTANCE;
        // repositoryHandler.setDriverName(driver);
        List<List<ColumnInfo>> query = repositoryHandler.query(LOGGER, transaction, sql);
        Console.println(query);
    }

    @Test
    void testDynamicRepository(@BeanInject("dataSourceFactory") DataSourceFactory factory) {
        var transaction = factory.getTransaction();
        List<Map<String, Object>> result = DynamicRepository.query(transaction)
                .sqlBuilder()
                .select("s.id", "s.name")
                .from("railway.seat s")
                .left().join("railway.carriage c").on().eq("c.id = s.carriageId")
                .where().eq("s.id", 142)
                .and().eq("s.name", "10F")
                .orderBy("s.id").desc()
                .repository()
                .toMap(LOGGER);
        Console.println("-------------------------------");
        Console.println(result);
    }

    @Test
    void testDynamicSql() {

    }

    private static final Logger LOGGER = LoggerFactory.getLogger(DynamicRepositoryTest.class);

}