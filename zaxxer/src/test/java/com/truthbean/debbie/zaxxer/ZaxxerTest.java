package com.truthbean.debbie.zaxxer;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.SQLException;

public class ZaxxerTest {


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
}
