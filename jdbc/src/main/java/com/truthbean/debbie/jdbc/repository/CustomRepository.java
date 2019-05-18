package com.truthbean.debbie.jdbc.repository;

import java.sql.Connection;
import java.util.List;

/**
 * @author TruthBean
 * @since 0.0.1
 */
public class CustomRepository<Entity, Id> extends JdbcRepository<Entity, Id> {

    public int[] batch(String sql, Object[][] args) {
        Connection connection = getConnection();
        return super.batch(connection, sql, args);
    }

    public int update(String sql, Object... args) {
        Connection connection = getConnection();
        return super.update(connection, sql, args);
    }

    public <T> List<T> select(String selectSql, Class<T> clazz, Object... args) {
        Connection connection = getConnection();
        return super.query(connection, selectSql, clazz, args);
    }

    public <T> T selectOne(String selectSql, Class<T> clazz, Object... args) {
        Connection connection = getConnection();
        return super.queryOne(connection, selectSql, clazz, args);
    }
}
