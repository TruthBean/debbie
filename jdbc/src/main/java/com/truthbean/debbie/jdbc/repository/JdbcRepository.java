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
        var transaction = getTransaction();
        return repositoryHandler.deleteById(transaction, id);
    }

    public boolean deleteByColumn(String columnName, Object value) {
        var transaction = getTransaction();
        return repositoryHandler.deleteByColumn(transaction, columnName, value);
    }

    public int deleteByIdIn(List<Id> ids) {
        var transaction = getTransaction();
        return repositoryHandler.deleteByIdIn(transaction, ids);
    }

    public <C> int deleteByColumnIn(String columnName, List<C> values) {
        var transaction = getTransaction();
        return repositoryHandler.deleteByColumnIn(transaction, columnName, values);
    }

    public int delete(Entity condition, boolean withConditionNull) {
        var transaction = getTransaction();
        return repositoryHandler.delete(transaction, condition, withConditionNull);
    }

    public int deleteAll() {
        var transaction = getTransaction();
        return repositoryHandler.delete(transaction);
    }

    public Id insert(Entity entity, boolean withEntityPropertyNull) {
        var transaction = getTransaction();
        return repositoryHandler.insert(transaction, entity, withEntityPropertyNull);
    }

    public int insert(Collection<Entity> entities, boolean withEntityPropertyNull) {
        var transaction = getTransaction();
        return repositoryHandler.insert(transaction, entities, withEntityPropertyNull);
    }

    public boolean update(Entity entity, boolean withEntityPropertyNull) {
        var transaction = getTransaction();
        return repositoryHandler.update(transaction, entity, withEntityPropertyNull);
    }

    public int update(Entity entity, boolean withEntityPropertyNull, String whereSql, Object...args) {
        var transaction = getTransaction();
        return repositoryHandler.update(transaction, entity, withEntityPropertyNull, whereSql, args);
    }

    public <S extends Entity> S save(S entity) {
        var transaction = getTransaction();
        return repositoryHandler.save(transaction, entity);
    }

    public Entity findByColumn(String columnName, Object value) {
        var transaction = getTransaction();
        String whereSql = columnName + " = ?";
        return repositoryHandler.selectOne(transaction, whereSql, value);
    }

    public List<Entity> findListByColumn(String columnName, Object value) {
        var transaction = getTransaction();
        String whereSql = columnName + " = ?";
        return repositoryHandler.selectList(transaction, whereSql, value);
    }

    public Entity findOne(Entity condition, boolean withConditionNull) {
        var transaction = getTransaction();
        return repositoryHandler.selectOne(transaction, condition, withConditionNull);
    }

    public Entity findOne(String whereSql, Object...args) {
        var transaction = getTransaction();
        return repositoryHandler.selectOne(transaction, whereSql, args);
    }

    public Optional<Entity> findOptional(Entity condition, boolean withConditionNull) {
        var transaction = getTransaction();
        var entity = repositoryHandler.selectOne(transaction, condition, withConditionNull);
        if (entity == null)
            return Optional.empty();
        else
            return Optional.of(entity);
    }

    public Optional<Entity> findOptional(String whereSql, Object...args) {
        var transaction = getTransaction();
        var entity = repositoryHandler.selectOne(transaction, whereSql, args);
        if (entity == null)
            return Optional.empty();
        else
            return Optional.of(entity);
    }

    public List<Entity> findList(Entity condition, boolean withConditionNull) {
        var transaction = getTransaction();
        return repositoryHandler.selectList(transaction, condition, withConditionNull);
    }

    public List<Entity> findList(String whereSql, Object...args) {
        var transaction = getTransaction();
        return repositoryHandler.selectList(transaction, whereSql, args);
    }

    public Page<Entity> findPaged(Entity condition, boolean withConditionNull, PageRequest pageable) {
        var transaction = getTransaction();
        return repositoryHandler.selectPaged(transaction, condition, withConditionNull, pageable);
    }

    public Page<Entity> findPaged(PageRequest pageable, String whereSql, Object...args) {
        var transaction = getTransaction();
        return repositoryHandler.selectPaged(transaction, pageable, whereSql, args);
    }

    public Page<Entity> findPaged(PageRequest pageable) {
        var transaction = getTransaction();
        return repositoryHandler.selectPaged(transaction, pageable, null);
    }

    public List<Entity> findAll() {
        var transaction = getTransaction();
        return repositoryHandler.selectAll(transaction);
    }

    public List<Entity> findAllById(Iterable<Id> ids) {
        if (!ids.iterator().hasNext()) {
            return Collections.emptyList();
        }

        List<Entity> result = new ArrayList<>();
        var transaction = getTransaction();
        for (Id id : ids) {
            result.add(repositoryHandler.selectById(transaction, id));
        }
        return result;
    }

    public Long count(Entity condition, boolean withConditionNull) {
        var transaction = getTransaction();
        return repositoryHandler.count(transaction, condition, withConditionNull);
    }

    public Long count() {
        var transaction = getTransaction();
        return repositoryHandler.count(transaction);
    }

    public Entity findById(Id id) {
        var transaction = getTransaction();
        return repositoryHandler.selectById(transaction, id);
    }

    public boolean existsById(Id id) {
        var transaction = getTransaction();
        return repositoryHandler.existsById(transaction, id);
    }

    public Optional<Entity> queryOptionalById(Id id) {
        var transaction = getTransaction();
        return repositoryHandler.selectOptionalById(transaction, id);
    }

}
