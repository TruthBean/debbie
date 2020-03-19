package com.truthbean.debbie.jdbc.repository;

import java.sql.Connection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

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

    public int insert(String sql, Object... args) {
        Connection connection = getConnection();
        return super.update(connection, sql, args);
    }

    public int delete(String sql, Object... args) {
        Connection connection = getConnection();
        return super.update(connection, sql, args);
    }

    public List<Entity> selectEntityList(String selectSql, Object... args) {
        Connection connection = getConnection();
        var entityClass = super.getEntityClass();
        return super.query(connection, selectSql, entityClass, args);
    }

    public <T> List<T> select(String selectSql, Class<T> clazz, Object... args) {
        Connection connection = getConnection();
        return super.query(connection, selectSql, clazz, args);
    }

    public <T> Set<T> selectSet(String selectSql, Class<T> clazz, Object... args) {
        List<T> list = select(selectSql, clazz, args);
        return new HashSet<>(list);
    }

    public List<Map<String, Object>> selectListMap(String selectSql, Object... args) {
        Connection connection = getConnection();
        return super.queryMap(connection, selectSql, args);
    }

    public Map<String, Object> selectMap(String selectSql, Object... args) {
        Connection connection = getConnection();
        List<Map<String, Object>> maps = super.queryMap(connection, selectSql, args);
        if (maps.size() == 1) {
            return maps.get(0);
        } else {
            throw new MoreRowException("Expect one row, but it has" + maps.size() + "rows.");
        }
    }

    public <T> T selectOne(String selectSql, Class<T> clazz, Object... args) {
        Connection connection = getConnection();
        return super.queryOne(connection, selectSql, clazz, args);
    }

    public Entity selectEntity(String selectSql, Object... args) {
        Connection connection = getConnection();
        var entityClass = super.getEntityClass();
        return super.queryOne(connection, selectSql, entityClass, args);
    }
}
