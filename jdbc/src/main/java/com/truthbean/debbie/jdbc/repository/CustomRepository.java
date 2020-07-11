/**
 * Copyright (c) 2020 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *         http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
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

    private DmlRepositoryHandler<Entity, Id> repositoryHandler;

    void setRepositoryHandler(DmlRepositoryHandler<Entity, Id> repositoryHandler) {
        super.setRepositoryHandler(repositoryHandler);
        this.repositoryHandler = repositoryHandler;
    }

    public int[] batch(String sql, Object[][] args) {
        Connection connection = getConnection();
        return repositoryHandler.batch(connection, sql, args);
    }

    public int update(String sql, Object... args) {
        Connection connection = getConnection();
        return repositoryHandler.update(connection, sql, args);
    }

    public int insert(String sql, Object... args) {
        Connection connection = getConnection();
        return repositoryHandler.update(connection, sql, args);
    }

    public int delete(String sql, Object... args) {
        Connection connection = getConnection();
        return repositoryHandler.update(connection, sql, args);
    }

    public List<Entity> selectEntityList(String selectSql, Object... args) {
        Connection connection = getConnection();
        var entityClass = repositoryHandler.getEntityClass();
        return repositoryHandler.query(connection, selectSql, entityClass, args);
    }

    public <T> List<T> select(String selectSql, Class<T> clazz, Object... args) {
        Connection connection = getConnection();
        return repositoryHandler.query(connection, selectSql, clazz, args);
    }

    public <T> Set<T> selectSet(String selectSql, Class<T> clazz, Object... args) {
        List<T> list = select(selectSql, clazz, args);
        return new HashSet<>(list);
    }

    public List<Map<String, Object>> selectListMap(String selectSql, Object... args) {
        Connection connection = getConnection();
        return repositoryHandler.queryMap(connection, selectSql, args);
    }

    public Map<String, Object> selectMap(String selectSql, Object... args) {
        Connection connection = getConnection();
        List<Map<String, Object>> maps = repositoryHandler.queryMap(connection, selectSql, args);
        if (maps.size() == 1) {
            return maps.get(0);
        } else {
            throw new MoreRowException("Expect one row, but it has" + maps.size() + "rows.");
        }
    }

    public <T> T selectOne(String sql, Class<T> clazz, Object... args) {
        Connection connection = getConnection();
        return repositoryHandler.queryOne(connection, sql, clazz, args);
    }

    public Entity selectEntity(String selectSql, Object... args) {
        Connection connection = getConnection();
        var entityClass = repositoryHandler.getEntityClass();
        return repositoryHandler.queryOne(connection, selectSql, entityClass, args);
    }
}
