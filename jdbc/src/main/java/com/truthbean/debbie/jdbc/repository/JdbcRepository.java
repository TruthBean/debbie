package com.truthbean.debbie.jdbc.repository;

import com.truthbean.debbie.jdbc.datasource.connection.ConnectionService;
import com.truthbean.debbie.jdbc.domain.Page;
import com.truthbean.debbie.jdbc.domain.PageRequest;

import java.sql.Connection;
import java.util.List;
import java.util.Optional;

/**
 * @author TruthBean
 * @since 0.0.1
 */
public class JdbcRepository<Entity, Id> extends DmlRepositoryHandler<Entity, Id> implements ConnectionService {

    public boolean deleteById(Id id) {
        Connection connection = getConnection();
        return super.deleteById(connection, id);
    }

    public int delete(Entity condition) {
        Connection connection = getConnection();
        return super.delete(connection, condition);
    }

    public Id insert(Entity entity) {
        Connection connection = getConnection();
        return super.insert(connection, entity);
    }

    public boolean update(Entity entity) {
        Connection connection = getConnection();
        return super.update(connection, entity);
    }

    public Entity findOne(Entity condition) {
        Connection connection = getConnection();
        return super.selectOne(connection, condition);
    }

    public List<Entity> findList(Entity condition) {
        Connection connection = getConnection();
        return super.selectList(connection, condition);
    }

    public Page<Entity> findPaged(Entity condition, PageRequest pageable) {
        Connection connection = getConnection();
        return super.selectPaged(connection, condition, pageable);
    }

    public Page<Entity> findPaged(PageRequest pageable) {
        Connection connection = getConnection();
        return super.selectPaged(connection, pageable);
    }

    public List<Entity> findAll() {
        Connection connection = getConnection();
        return super.selectAll(connection);
    }

    public Long count(Entity condition) {
        Connection connection = getConnection();
        return super.count(connection, condition);
    }

    public Long count() {
        Connection connection = getConnection();
        return super.count(connection);
    }

    public Entity findById(Id id) {
        Connection connection = getConnection();
        return super.selectById(connection, id);
    }

    public Optional<Entity> queryOptionalById(Id id) {
        Connection connection = getConnection();
        return super.selectOptionalById(connection, id);
    }

}
