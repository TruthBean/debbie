package com.truthbean.debbie.hikari;

import com.truthbean.debbie.bean.GlobalBeanFactory;
import com.truthbean.debbie.boot.DebbieApplicationFactory;
import com.truthbean.debbie.jdbc.datasource.DataSourceFactory;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.SQLException;

public class HikariTest {


    @Test
    public void test() {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl("jdbc:mysql://localhost:3306/test");
        // config.setDriverClassName("org.mariadb.jdbc.Driver");
        // config.setDataSourceClassName("org.mariadb.jdbc.MariaDbPoolDataSource");
        config.setUsername("root");
        config.setPassword("root");
        config.addDataSourceProperty("cachePrepStmts", "true");
        config.addDataSourceProperty("prepStmtCacheSize", "250");
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
        config.addDataSourceProperty("serverTimezone", "GMT+8");

        HikariDataSource ds = new HikariDataSource(config);

        try {
            Connection connection = ds.getConnection();
            Thread.sleep(1000);
            connection.close();
        } catch (SQLException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testDataSource() {
        DebbieApplicationFactory applicationFactory = DebbieApplicationFactory.configure(HikariTest.class);
        GlobalBeanFactory globalBeanFactory = applicationFactory.getGlobalBeanFactory();

        try {
            DataSourceFactory factory = globalBeanFactory.factory("dataSourceFactory");
            System.out.println(factory);
            Connection connection = factory.getConnection();
            Thread.sleep(2000);
            connection.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            applicationFactory.release();
        }
    }
}
