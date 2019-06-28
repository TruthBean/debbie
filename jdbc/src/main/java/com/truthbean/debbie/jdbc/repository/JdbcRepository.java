package com.truthbean.debbie.jdbc.repository;

import com.truthbean.debbie.jdbc.transaction.TransactionService;
import com.truthbean.debbie.jdbc.domain.Page;
import com.truthbean.debbie.jdbc.domain.PageRequest;

import java.sql.Connection;
import java.util.List;
import java.util.Optional;

/**
 * @author TruthBean
 * @since 0.0.1
 */
public class JdbcRepository<Entity, Id> extends DmlRepositoryHandler<Entity, Id> implements TransactionService {

    public boolean deleteById(Id id) {
        Connection connection = getConnection();
        return super.deleteById(connection, id);
    }

    public int delete(Entity condition, boolean withConditionNull) {
        Connection connection = getConnection();
        return super.delete(connection, condition, withConditionNull);
    }

    public int deleteAll() {
        Connection connection = getConnection();
        return super.delete(connection);
    }

    public Id insert(Entity entity, boolean withEntityPropertyNull) {
        Connection connection = getConnection();
        return super.insert(connection, entity, withEntityPropertyNull);
    }

    public boolean update(Entity entity, boolean withEntityPropertyNull) {
        Connection connection = getConnection();
        return super.update(connection, entity, withEntityPropertyNull);
    }

    public int update(Entity entity, boolean withEntityPropertyNull, String whereSql, Object...args) {
        Connection connection = getConnection();
        return super.update(connection, entity, withEntityPropertyNull, whereSql, args);
    }

    public <S extends Entity> S save(S entity) {
        Connection connection = getConnection();
        return super.save(connection, entity);
    }

    public Entity findOne(Entity condition, boolean withConditionNull) {
        Connection connection = getConnection();
        return super.selectOne(connection, condition, withConditionNull);
    }

    public Entity findOne(String whereSql, Object...args) {
        Connection connection = getConnection();
        return super.selectOne(connection, whereSql, args);
    }

    public Optional<Entity> findOptional(Entity condition, boolean withConditionNull) {
        Connection connection = getConnection();
        var entity = super.selectOne(connection, condition, withConditionNull);
        if (entity == null)
            return Optional.empty();
        else
            return Optional.of(entity);
    }

    public Optional<Entity> findOptional(String whereSql, Object...args) {
        Connection connection = getConnection();
        var entity = super.selectOne(connection, whereSql, args);
        if (entity == null)
            return Optional.empty();
        else
            return Optional.of(entity);
    }

    public List<Entity> findList(Entity condition, boolean withConditionNull) {
        Connection connection = getConnection();
        return super.selectList(connection, condition, withConditionNull);
    }

    public List<Entity> findList(String whereSql, Object...args) {
        Connection connection = getConnection();
        return super.selectList(connection, whereSql, args);
    }

    public Page<Entity> findPaged(Entity condition, boolean withConditionNull, PageRequest pageable) {
        Connection connection = getConnection();
        return super.selectPaged(connection, condition, withConditionNull, pageable);
    }

    public Page<Entity> findPaged(PageRequest pageable, String whereSql, Object...args) {
        Connection connection = getConnection();
        return super.selectPaged(connection, pageable, whereSql, args);
    }

    public Page<Entity> findPaged(PageRequest pageable) {
        Connection connection = getConnection();
        return super.selectPaged(connection, pageable, null);
    }

    public List<Entity> findAll() {
        Connection connection = getConnection();
        return super.selectAll(connection);
    }

    public Long count(Entity condition, boolean withConditionNull) {
        Connection connection = getConnection();
        return super.count(connection, condition, withConditionNull);
    }

    public Long count() {
        Connection connection = getConnection();
        return super.count(connection);
    }

    public Entity findById(Id id) {
        Connection connection = getConnection();
        return super.selectById(connection, id);
    }

    public boolean existsById(Id id) {
        Connection connection = getConnection();
        return super.existsById(connection, id);
    }

    public Optional<Entity> queryOptionalById(Id id) {
        Connection connection = getConnection();
        return super.selectOptionalById(connection, id);
    }

}
