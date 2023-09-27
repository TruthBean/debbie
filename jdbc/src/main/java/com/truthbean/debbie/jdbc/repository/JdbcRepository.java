/**
 * Copyright (c) 2023 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 * http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.jdbc.repository;

import com.truthbean.Logger;
import com.truthbean.LoggerFactory;
import com.truthbean.debbie.jdbc.domain.Page;
import com.truthbean.debbie.jdbc.domain.PageRequest;
import com.truthbean.debbie.jdbc.entity.EntityResolver;
import com.truthbean.debbie.jdbc.transaction.TransactionService;

import java.util.*;

/**
 * @author TruthBean
 * @since 0.0.1
 */
public class JdbcRepository<Entity, Id> implements TransactionService {

    private JdbcTransactionRepository<Entity, Id> jdbcTransactionRepository;

    public JdbcRepository() {
    }

    public JdbcRepository(JdbcTransactionRepository<Entity, Id> jdbcTransactionRepository) {
        this.jdbcTransactionRepository = jdbcTransactionRepository;
        this.jdbcTransactionRepository.setLogger(getLog());
    }

    void setJdbcTransactionRepository(JdbcTransactionRepository<Entity, Id> jdbcTransactionRepository) {
        this.jdbcTransactionRepository = jdbcTransactionRepository;
        this.jdbcTransactionRepository.setLogger(getLog());
    }

    void setIdClass(Class<Id> idClass) {
        jdbcTransactionRepository.setIdClass(idClass);
    }

    void setEntityClass(Class<Entity> entityClass) {
        jdbcTransactionRepository.setEntityClass(entityClass);
    }

    void setEntityResolver(EntityResolver entityResolver) {
        jdbcTransactionRepository.setEntityResolver(entityResolver);
    }

    protected Class<Entity> getEntityClass() {
        return jdbcTransactionRepository.getEntityClass();
    }

    protected Class<Id> getIdClass() {
        return jdbcTransactionRepository.getIdClass();
    }

    public boolean deleteById(Id id) {
        var transaction = getTransaction();
        return jdbcTransactionRepository.deleteById(transaction, id);
    }

    public boolean deleteByColumn(String columnName, Object value) {
        var transaction = getTransaction();
        return jdbcTransactionRepository.deleteByColumn(transaction, columnName, value);
    }

    public int deleteByIdIn(List<Id> ids) {
        var transaction = getTransaction();
        return jdbcTransactionRepository.deleteByIdIn(transaction, ids);
    }

    public <C> int deleteByColumnIn(String columnName, List<C> values) {
        var transaction = getTransaction();
        return jdbcTransactionRepository.deleteByColumnIn(transaction, columnName, values);
    }

    public int delete(Entity condition, boolean withConditionNull) {
        var transaction = getTransaction();
        return jdbcTransactionRepository.delete(transaction, condition, withConditionNull);
    }

    public int deleteAll() {
        var transaction = getTransaction();
        return jdbcTransactionRepository.deleteAll(transaction);
    }

    public Id insert(Entity entity, boolean withEntityPropertyNull) {
        var transaction = getTransaction();
        return jdbcTransactionRepository.insert(transaction, entity, withEntityPropertyNull);
    }

    public int insert(Collection<Entity> entities, boolean withEntityPropertyNull) {
        var transaction = getTransaction();
        return jdbcTransactionRepository.insert(transaction, entities, withEntityPropertyNull);
    }

    public boolean update(Entity entity, boolean withEntityPropertyNull) {
        var transaction = getTransaction();
        return jdbcTransactionRepository.update(transaction, entity, withEntityPropertyNull);
    }

    public int update(Entity entity, boolean withEntityPropertyNull, String whereSql, Object... args) {
        var transaction = getTransaction();
        return jdbcTransactionRepository.update(transaction, entity, withEntityPropertyNull, whereSql, args);
    }

    public <S extends Entity> S save(S entity) {
        var transaction = getTransaction();
        return jdbcTransactionRepository.save(transaction, entity);
    }

    public Entity findByColumn(String columnName, Object value) {
        var transaction = getTransaction();
        return jdbcTransactionRepository.findByColumn(transaction, columnName, value);
    }

    public List<Entity> findListByColumn(String columnName, Object value) {
        var transaction = getTransaction();
        return jdbcTransactionRepository.findListByColumn(transaction, columnName, value);
    }

    public List<Entity> findListByColumnIn(String columnName, Collection<?> values) {
        if (values.isEmpty()) {
            return new ArrayList<>();
        }
        var transaction = getTransaction();
        return jdbcTransactionRepository.findListByColumnIn(transaction, columnName, values);
    }

    public Entity findOne(Entity condition, boolean withConditionNull) {
        var transaction = getTransaction();
        return jdbcTransactionRepository.findOne(transaction, condition, withConditionNull);
    }

    public Entity findOne(String whereSql, Object... args) {
        var transaction = getTransaction();
        return jdbcTransactionRepository.findOne(transaction, whereSql, args);
    }

    public Optional<Entity> findOptional(Entity condition, boolean withConditionNull) {
        var transaction = getTransaction();
        return jdbcTransactionRepository.findOptional(transaction, condition, withConditionNull);
    }

    public Optional<Entity> findOptional(String whereSql, Object... args) {
        var transaction = getTransaction();
        return jdbcTransactionRepository.findOptional(transaction, whereSql, args);
    }

    public List<Entity> findList(Entity condition, boolean withConditionNull) {
        var transaction = getTransaction();
        return jdbcTransactionRepository.findList(transaction, condition, withConditionNull);
    }

    public List<Entity> findList(String whereSql, Object... args) {
        var transaction = getTransaction();
        return jdbcTransactionRepository.findList(transaction, whereSql, args);
    }

    public Page<Entity> findPaged(Entity condition, boolean withConditionNull, PageRequest pageable) {
        var transaction = getTransaction();
        return jdbcTransactionRepository.findPaged(transaction, condition, withConditionNull, pageable);
    }

    public Page<Entity> findPaged(PageRequest pageable, String whereSql, Object... args) {
        var transaction = getTransaction();
        return jdbcTransactionRepository.findPaged(transaction, pageable, whereSql, args);
    }

    public Page<Entity> findPaged(PageRequest pageable) {
        var transaction = getTransaction();
        return jdbcTransactionRepository.findPaged(transaction, pageable);
    }

    public List<Entity> findAll() {
        var transaction = getTransaction();
        return jdbcTransactionRepository.findAll(transaction);
    }

    public List<Entity> findAllById(Collection<Id> ids) {
        if (!ids.iterator().hasNext()) {
            return Collections.emptyList();
        }
        var transaction = getTransaction();
        return jdbcTransactionRepository.findAllByIdIn(transaction, ids);
    }

    public Long count(Entity condition, boolean withConditionNull) {
        var transaction = getTransaction();
        return jdbcTransactionRepository.count(transaction, condition, withConditionNull);
    }

    public Long count() {
        var transaction = getTransaction();
        return jdbcTransactionRepository.count(transaction);
    }

    public Entity findById(Id id) {
        var transaction = getTransaction();
        return jdbcTransactionRepository.findById(transaction, id);
    }

    public boolean existsById(Id id) {
        var transaction = getTransaction();
        return jdbcTransactionRepository.existsById(transaction, id);
    }

    public Optional<Entity> queryOptionalById(Id id) {
        var transaction = getTransaction();
        return jdbcTransactionRepository.queryOptionalById(transaction, id);
    }

    protected Logger getLog() {
        return LoggerFactory.getLogger(this.getClass());
    }

}
