package com.truthbean.debbie.jdbc.repository;

import com.truthbean.debbie.jdbc.datasource.connection.ConnectionService;
import com.truthbean.debbie.jdbc.domain.PageRequest;

import java.sql.Connection;
import java.util.List;

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
        return super.findOne(connection, condition);
    }

    public List<Entity> findList(Entity condition) {
        Connection connection = getConnection();
        return super.findList(connection, condition);
    }

    public Page<Entity> findPaged(Entity condition, PageRequest pageable) {
        Connection connection = getConnection();
        return super.findPaged(connection, condition, pageable);
    }

    public Page<Entity> findPaged(PageRequest pageable) {
        Connection connection = getConnection();
        return super.findPaged(connection, pageable);
    }

    public List<Entity> findAll() {
        Connection connection = getConnection();
        return super.findAll(connection);
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
        return super.findById(connection, id);
    }

}
