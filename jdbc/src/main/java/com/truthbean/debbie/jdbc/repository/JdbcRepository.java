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

import com.truthbean.debbie.jdbc.domain.Page;
import com.truthbean.debbie.jdbc.domain.PageRequest;
import com.truthbean.debbie.jdbc.transaction.TransactionService;

import java.util.*;

/**
 * @author TruthBean
 * @since 0.0.1
 */
public class JdbcRepository<Entity, Id> implements TransactionService {

    private DmlRepositoryHandler<Entity, Id> repositoryHandler;

    void setRepositoryHandler(DmlRepositoryHandler<Entity, Id> repositoryHandler) {
        this.repositoryHandler = repositoryHandler;
    }

    public boolean deleteById(Id id) {
        var connection = getDriverConnection();
        return repositoryHandler.deleteById(connection, id);
    }

    public boolean deleteByColumn(String columnName, Object value) {
        var connection = getDriverConnection();
        return repositoryHandler.deleteByColumn(connection, columnName, value);
    }

    public int deleteByIdIn(List<Id> ids) {
        var connection = getDriverConnection();
        return repositoryHandler.deleteByIdIn(connection, ids);
    }

    public <C> int deleteByColumnIn(String columnName, List<C> values) {
        var connection = getDriverConnection();
        return repositoryHandler.deleteByColumnIn(connection, columnName, values);
    }

    public int delete(Entity condition, boolean withConditionNull) {
        var connection = getDriverConnection();
        return repositoryHandler.delete(connection, condition, withConditionNull);
    }

    public int deleteAll() {
        var connection = getDriverConnection();
        return repositoryHandler.delete(connection);
    }

    public Id insert(Entity entity, boolean withEntityPropertyNull) {
        var connection = getDriverConnection();
        return repositoryHandler.insert(connection, entity, withEntityPropertyNull);
    }

    public int insert(Collection<Entity> entities, boolean withEntityPropertyNull) {
        var connection = getDriverConnection();
        return repositoryHandler.insert(connection, entities, withEntityPropertyNull);
    }

    public boolean update(Entity entity, boolean withEntityPropertyNull) {
        var connection = getDriverConnection();
        return repositoryHandler.update(connection, entity, withEntityPropertyNull);
    }

    public int update(Entity entity, boolean withEntityPropertyNull, String whereSql, Object...args) {
        var connection = getDriverConnection();
        return repositoryHandler.update(connection, entity, withEntityPropertyNull, whereSql, args);
    }

    public <S extends Entity> S save(S entity) {
        var connection = getDriverConnection();
        return repositoryHandler.save(connection, entity);
    }

    public Entity findByColumn(String columnName, Object value) {
        var connection = getDriverConnection();
        String whereSql = columnName + " = ?";
        return repositoryHandler.selectOne(connection, whereSql, value);
    }

    public List<Entity> findListByColumn(String columnName, Object value) {
        var connection = getDriverConnection();
        String whereSql = columnName + " = ?";
        return repositoryHandler.selectList(connection, whereSql, value);
    }

    public Entity findOne(Entity condition, boolean withConditionNull) {
        var connection = getDriverConnection();
        return repositoryHandler.selectOne(connection, condition, withConditionNull);
    }

    public Entity findOne(String whereSql, Object...args) {
        var connection = getDriverConnection();
        return repositoryHandler.selectOne(connection, whereSql, args);
    }

    public Optional<Entity> findOptional(Entity condition, boolean withConditionNull) {
        var connection = getDriverConnection();
        var entity = repositoryHandler.selectOne(connection, condition, withConditionNull);
        if (entity == null)
            return Optional.empty();
        else
            return Optional.of(entity);
    }

    public Optional<Entity> findOptional(String whereSql, Object...args) {
        var connection = getDriverConnection();
        var entity = repositoryHandler.selectOne(connection, whereSql, args);
        if (entity == null)
            return Optional.empty();
        else
            return Optional.of(entity);
    }

    public List<Entity> findList(Entity condition, boolean withConditionNull) {
        var connection = getDriverConnection();
        return repositoryHandler.selectList(connection, condition, withConditionNull);
    }

    public List<Entity> findList(String whereSql, Object...args) {
        var connection = getDriverConnection();
        return repositoryHandler.selectList(connection, whereSql, args);
    }

    public Page<Entity> findPaged(Entity condition, boolean withConditionNull, PageRequest pageable) {
        var connection = getDriverConnection();
        return repositoryHandler.selectPaged(connection, condition, withConditionNull, pageable);
    }

    public Page<Entity> findPaged(PageRequest pageable, String whereSql, Object...args) {
        var connection = getDriverConnection();
        return repositoryHandler.selectPaged(connection, pageable, whereSql, args);
    }

    public Page<Entity> findPaged(PageRequest pageable) {
        var connection = getDriverConnection();
        return repositoryHandler.selectPaged(connection, pageable, null);
    }

    public List<Entity> findAll() {
        var connection = getDriverConnection();
        return repositoryHandler.selectAll(connection);
    }

    public List<Entity> findAllById(Iterable<Id> ids) {
        if (!ids.iterator().hasNext()) {
            return Collections.emptyList();
        }

        List<Entity> result = new ArrayList<>();
        var connection = getDriverConnection();
        for (Id id : ids) {
            result.add(repositoryHandler.selectById(connection, id));
        }
        return result;
    }

    public Long count(Entity condition, boolean withConditionNull) {
        var connection = getDriverConnection();
        return repositoryHandler.count(connection, condition, withConditionNull);
    }

    public Long count() {
        var connection = getDriverConnection();
        return repositoryHandler.count(connection);
    }

    public Entity findById(Id id) {
        var connection = getDriverConnection();
        return repositoryHandler.selectById(connection, id);
    }

    public boolean existsById(Id id) {
        var connection = getDriverConnection();
        return repositoryHandler.existsById(connection, id);
    }

    public Optional<Entity> queryOptionalById(Id id) {
        var connection = getDriverConnection();
        return repositoryHandler.selectOptionalById(connection, id);
    }

}
