package com.truthbean.debbie.jdbc.repository;

import com.truthbean.debbie.jdbc.domain.Page;
import com.truthbean.debbie.jdbc.domain.PageRequest;
import com.truthbean.debbie.jdbc.transaction.TransactionService;

import java.util.*;

/**
 * @author TruthBean
 * @since 0.0.1
 */
public class JdbcRepository<Entity, Id> extends DmlRepositoryHandler<Entity, Id> implements TransactionService {

    public boolean deleteById(Id id) {
        var connection = getDriverConnection();
        return super.deleteById(connection, id);
    }

    public boolean deleteByColumn(String columnName, Object value) {
        var connection = getDriverConnection();
        return super.deleteByColumn(connection, columnName, value);
    }

    public int deleteByIdIn(List<Id> ids) {
        var connection = getDriverConnection();
        return super.deleteByIdIn(connection, ids);
    }

    public <C> int deleteByColumnIn(String columnName, List<C> values) {
        var connection = getDriverConnection();
        return super.deleteByColumnIn(connection, columnName, values);
    }

    public int delete(Entity condition, boolean withConditionNull) {
        var connection = getDriverConnection();
        return super.delete(connection, condition, withConditionNull);
    }

    public int deleteAll() {
        var connection = getDriverConnection();
        return super.delete(connection);
    }

    public Id insert(Entity entity, boolean withEntityPropertyNull) {
        var connection = getDriverConnection();
        return super.insert(connection, entity, withEntityPropertyNull);
    }

    public int insert(Collection<Entity> entities, boolean withEntityPropertyNull) {
        var connection = getDriverConnection();
        return super.insert(connection, entities, withEntityPropertyNull);
    }

    public boolean update(Entity entity, boolean withEntityPropertyNull) {
        var connection = getDriverConnection();
        return super.update(connection, entity, withEntityPropertyNull);
    }

    public int update(Entity entity, boolean withEntityPropertyNull, String whereSql, Object...args) {
        var connection = getDriverConnection();
        return super.update(connection, entity, withEntityPropertyNull, whereSql, args);
    }

    public <S extends Entity> S save(S entity) {
        var connection = getDriverConnection();
        return super.save(connection, entity);
    }

    public Entity findByColumn(String columnName, Object value) {
        var connection = getDriverConnection();
        String whereSql = columnName + " = ?";
        return super.selectOne(connection, whereSql, value);
    }

    public List<Entity> findListByColumn(String columnName, Object value) {
        var connection = getDriverConnection();
        String whereSql = columnName + " = ?";
        return super.selectList(connection, whereSql, value);
    }

    public Entity findOne(Entity condition, boolean withConditionNull) {
        var connection = getDriverConnection();
        return super.selectOne(connection, condition, withConditionNull);
    }

    public Entity findOne(String whereSql, Object...args) {
        var connection = getDriverConnection();
        return super.selectOne(connection, whereSql, args);
    }

    public Optional<Entity> findOptional(Entity condition, boolean withConditionNull) {
        var connection = getDriverConnection();
        var entity = super.selectOne(connection, condition, withConditionNull);
        if (entity == null)
            return Optional.empty();
        else
            return Optional.of(entity);
    }

    public Optional<Entity> findOptional(String whereSql, Object...args) {
        var connection = getDriverConnection();
        var entity = super.selectOne(connection, whereSql, args);
        if (entity == null)
            return Optional.empty();
        else
            return Optional.of(entity);
    }

    public List<Entity> findList(Entity condition, boolean withConditionNull) {
        var connection = getDriverConnection();
        return super.selectList(connection, condition, withConditionNull);
    }

    public List<Entity> findList(String whereSql, Object...args) {
        var connection = getDriverConnection();
        return super.selectList(connection, whereSql, args);
    }

    public Page<Entity> findPaged(Entity condition, boolean withConditionNull, PageRequest pageable) {
        var connection = getDriverConnection();
        return super.selectPaged(connection, condition, withConditionNull, pageable);
    }

    public Page<Entity> findPaged(PageRequest pageable, String whereSql, Object...args) {
        var connection = getDriverConnection();
        return super.selectPaged(connection, pageable, whereSql, args);
    }

    public Page<Entity> findPaged(PageRequest pageable) {
        var connection = getDriverConnection();
        return super.selectPaged(connection, pageable, null);
    }

    public List<Entity> findAll() {
        var connection = getDriverConnection();
        return super.selectAll(connection);
    }

    public List<Entity> findAllById(Iterable<Id> ids) {
        if (!ids.iterator().hasNext()) {
            return Collections.emptyList();
        }

        List<Entity> result = new ArrayList<>();
        var connection = getDriverConnection();
        for (Id id : ids) {
            result.add(super.selectById(connection, id));
        }
        return result;
    }

    public Long count(Entity condition, boolean withConditionNull) {
        var connection = getDriverConnection();
        return super.count(connection, condition, withConditionNull);
    }

    public Long count() {
        var connection = getDriverConnection();
        return super.count(connection);
    }

    public Entity findById(Id id) {
        var connection = getDriverConnection();
        return super.selectById(connection, id);
    }

    public boolean existsById(Id id) {
        var connection = getDriverConnection();
        return super.existsById(connection, id);
    }

    public Optional<Entity> queryOptionalById(Id id) {
        var connection = getDriverConnection();
        return super.selectOptionalById(connection, id);
    }

}
