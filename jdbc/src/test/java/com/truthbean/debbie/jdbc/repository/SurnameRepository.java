package com.truthbean.debbie.jdbc.repository;

import com.truthbean.debbie.jdbc.datasource.DataSourceFactory;
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
    private DataSourceFactory factory = DataSourceFactory.defaultFactory();
    private DmlRepositoryHandler<Surname, Long> repositoryHandler =
            new DmlRepositoryHandler<>(Surname.class, Long.class);

    public boolean save(Surname surname) {
        Connection connection = factory.getConnection();
        Long id = RepositoryAction.actionTransactional(connection, () -> {
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
        return RepositoryAction.actionTransactional(connection, () -> {
            Long insert = repositoryHandler.insert(connection, surname);
            surname.setId(insert);
            System.out.println(1/0);
            repositoryHandler.deleteById(connection, deleteId);
            return repositoryHandler.findAll(connection);
        });
    }

    public Optional<Surname> findById(Long id) {
        Connection connection = factory.getConnection();
        return RepositoryAction.actionOptional(connection, () -> repositoryHandler.findById(connection, id));
    }

    public boolean update(Surname surname) {
        Connection connection = factory.getConnection();
        return RepositoryAction.actionTransactional(connection, () -> repositoryHandler.update(connection, surname));
    }

    public boolean delete(Long id) {
        Connection connection = factory.getConnection();
        return RepositoryAction.actionTransactional(connection, () -> repositoryHandler.deleteById(connection, id));
    }

    public Future<List<Surname>> findAll() {
        Connection connection = factory.getConnection();
        return RepositoryAction.asyncAction(connection, () -> repositoryHandler.findAll(connection));
    }

    public Long count() {
        Connection connection = factory.getConnection();
        return RepositoryAction.action(connection, () -> repositoryHandler.count(connection));
    }

    public Page<Surname> findPaged(PageRequest pageRequest) {
        Connection connection = factory.getConnection();
        return RepositoryAction.action(connection, () -> repositoryHandler.findPaged(connection, pageRequest));
    }

}
