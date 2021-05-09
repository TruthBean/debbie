/**
 * Copyright (c) 2021 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *         http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.jdbc.repository;

import com.truthbean.debbie.jdbc.transaction.TransactionInfo;

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

    public CustomRepository() {
    }

    @Override
    void setRepositoryHandler(DmlRepositoryHandler<Entity, Id> repositoryHandler) {
        super.setRepositoryHandler(repositoryHandler);
        this.repositoryHandler = repositoryHandler;
    }

    public int[] batch(String sql, Object[][] args) {
        TransactionInfo transaction = getTransaction();
        return repositoryHandler.batch(transaction, sql, args);
    }

    public int update(String sql, Object... args) {
        TransactionInfo transaction = getTransaction();
        return repositoryHandler.update(transaction, sql, args);
    }

    public int insert(String sql, Object... args) {
        TransactionInfo transaction = getTransaction();
        return repositoryHandler.update(transaction, sql, args);
    }

    public int delete(String sql, Object... args) {
        TransactionInfo transaction = getTransaction();
        return repositoryHandler.update(transaction, sql, args);
    }

    public List<Entity> selectEntityList(String selectSql, Object... args) {
        TransactionInfo transaction = getTransaction();
        var entityClass = repositoryHandler.getEntityClass();
        return repositoryHandler.query(transaction, selectSql, entityClass, args);
    }

    public <T> List<T> select(String selectSql, Class<T> clazz, Object... args) {
        TransactionInfo transaction = getTransaction();
        return repositoryHandler.query(transaction, selectSql, clazz, args);
    }

    public <T> Set<T> selectSet(String selectSql, Class<T> clazz, Object... args) {
        List<T> list = select(selectSql, clazz, args);
        return new HashSet<>(list);
    }

    public List<Map<String, Object>> selectListMap(String selectSql, Object... args) {
        TransactionInfo transaction = getTransaction();
        return repositoryHandler.queryMap(transaction, selectSql, args);
    }

    public Map<String, Object> selectMap(String selectSql, Object... args) {
        TransactionInfo transaction = getTransaction();
        List<Map<String, Object>> maps = repositoryHandler.queryMap(transaction, selectSql, args);
        if (maps.size() == 1) {
            return maps.get(0);
        } else {
            throw new MoreRowException("Expect one row, but it has" + maps.size() + "rows.");
        }
    }

    public <T> T selectOne(String sql, Class<T> clazz, Object... args) {
        TransactionInfo transaction = getTransaction();
        return repositoryHandler.queryOne(transaction, sql, clazz, args);
    }

    public Entity selectEntity(String selectSql, Object... args) {
        TransactionInfo transaction = getTransaction();
        var entityClass = repositoryHandler.getEntityClass();
        return repositoryHandler.queryOne(transaction, selectSql, entityClass, args);
    }
}
