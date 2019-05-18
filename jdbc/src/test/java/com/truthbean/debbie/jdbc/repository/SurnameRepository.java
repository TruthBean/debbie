package com.truthbean.debbie.jdbc.repository;

import com.truthbean.debbie.jdbc.datasource.DataSourceFactory;
import com.truthbean.debbie.jdbc.domain.Page;
import com.truthbean.debbie.jdbc.domain.PageRequest;
import com.truthbean.debbie.jdbc.entity.Surname;

import java.sql.Connection;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Future;

/**
 * @author TruthBean
 * @since 0.0.1
 */
public class SurnameRepository {
    private DataSourceFactory factory = DataSourceFactory.factory();
    private DmlRepositoryHandler<Surname, Long> repositoryHandler =
            DmlRepositoryHandler.of(Surname.class, Long.class);

    public boolean save(Surname surname) {
        Connection connection = factory.getConnection();
        Long id = RepositoryCallback.actionTransactional(connection, () -> {
            Long insert = repositoryHandler.insert(connection, surname);
            surname.setId(insert);
            return insert;
        });
        boolean result = id > 0L;
        if (result) {
            surname.setId(id);
        }
        return result;
    }

    public List<Surname> saveAndDelete(Surname surname, Long deleteId) {
        Connection connection = factory.getConnection();
        return RepositoryCallback.actionTransactional(connection, () -> {
            Long insert = repositoryHandler.insert(connection, surname);
            surname.setId(insert);
            System.out.println(1/0);
            repositoryHandler.deleteById(connection, deleteId);
            return repositoryHandler.selectAll(connection);
        });
    }

    public Optional<Surname> findById(Long id) {
        Connection connection = factory.getConnection();
        return RepositoryCallback.actionOptional(connection, () -> repositoryHandler.selectById(connection, id));
    }

    public boolean update(Surname surname) {
        Connection connection = factory.getConnection();
        return RepositoryCallback.actionTransactional(connection, () -> repositoryHandler.update(connection, surname));
    }

    public boolean delete(Long id) {
        Connection connection = factory.getConnection();
        return RepositoryCallback.actionTransactional(connection, () -> repositoryHandler.deleteById(connection, id));
    }

    public Future<List<Surname>> findAll() {
        Connection connection = factory.getConnection();
        return RepositoryCallback.asyncAction(connection, () -> repositoryHandler.selectAll(connection));
    }

    public Long count() {
        Connection connection = factory.getConnection();
        return RepositoryCallback.action(connection, () -> repositoryHandler.count(connection));
    }

    public Page<Surname> findPaged(PageRequest pageRequest) {
        Connection connection = factory.getConnection();
        return RepositoryCallback.action(connection, () -> repositoryHandler.selectPaged(connection, pageRequest));
    }

}
