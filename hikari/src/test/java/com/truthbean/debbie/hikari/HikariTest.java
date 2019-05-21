package com.truthbean.debbie.hikari;

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
        DataSourceFactory factory = DataSourceFactory.factory();
        System.out.println(factory);

        try {
            Connection connection = factory.getConnection();
            Thread.sleep(1000);
            connection.close();
        } catch (SQLException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
