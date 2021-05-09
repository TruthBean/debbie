package com.truthbean.debbie.check.jdbc.repository;

import com.truthbean.debbie.bean.BeanInject;
import com.truthbean.debbie.jdbc.column.ColumnInfo;
import com.truthbean.debbie.jdbc.datasource.DataSourceFactory;
import com.truthbean.debbie.jdbc.repository.DynamicRepository;
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
        String sql = DynamicRepository.sql(driver)
                .select("id", "name").from("railway.seat").orderBy("id").desc()
                .builder();
        RepositoryHandler repositoryHandler = new RepositoryHandler();
        repositoryHandler.setDriverName(driver);
        List<List<ColumnInfo>> query = repositoryHandler.query(transaction, sql);
        System.out.println(query);
    }

    @Test
    void testDynamicRepository(@BeanInject("dataSourceFactory") DataSourceFactory factory) {
        var transaction = factory.getTransaction();
        List<Map<String, Object>> result = DynamicRepository.query(transaction)
                .select("s.id", "s.name")
                .from("railway.seat s")
                .left().join("railway.carriage c").on().eq("c.id = s.carriageId")
                .where().eq("s.id", 142)
                .and().eq("s.name", "10F")
                .orderBy("s.id").desc()
                .toMap();
        System.out.println("-------------------------------");
        System.out.println(result);
    }

    @Test
    void testDynamicSql() {

    }

}