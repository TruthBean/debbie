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

import com.truthbean.debbie.bean.BeanComponent;
import com.truthbean.debbie.jdbc.datasource.DataSourceConfiguration;
import com.truthbean.debbie.jdbc.datasource.DataSourceFactory;
import com.truthbean.debbie.jdbc.domain.Page;
import com.truthbean.debbie.jdbc.domain.PageRequest;
import com.truthbean.debbie.jdbc.entity.Surname;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.Future;

/**
 * @author TruthBean
 * @since 0.0.1
 */
@BeanComponent
public class SurnameRepository {

    private final DataSourceFactory factory;
    private final DataSourceConfiguration configuration;

    private final DmlRepositoryHandler<Surname, Long> repositoryHandler;

    public SurnameRepository(DataSourceFactory factory, DataSourceConfiguration configuration) {
        this.factory = factory;
        this.configuration = configuration;
        repositoryHandler = DmlRepositoryHandler.of(configuration.getDriverName(), Surname.class, Long.class);
    }

    public boolean save(Surname surname) {
        var transaction = factory.getTransaction();
        Long id = RepositoryCallback.actionTransactional(transaction, () -> {
            var connection = transaction.getDriverConnection();
            Long insert = repositoryHandler.insert(connection, surname, false);
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
        var transaction = factory.getTransaction();
        return RepositoryCallback.actionTransactional(transaction, () -> {
            var connection = transaction.getDriverConnection();
            Long insert = repositoryHandler.insert(connection, surname, false);
            surname.setId(insert);
            System.out.println(1 / 0);
            repositoryHandler.deleteById(connection, deleteId);
            return repositoryHandler.selectAll(connection);
        });
    }

    public Optional<Surname> findById(Long id) {
        var transaction = factory.getTransaction();
        return RepositoryCallback.actionOptional(transaction, () -> {
            var connection = transaction.getDriverConnection();
            return repositoryHandler.selectById(connection, id);
        });
    }

    public boolean update(Surname surname) {
        var transaction = factory.getTransaction();
        return RepositoryCallback.actionTransactional(transaction, () -> {
            var connection = transaction.getDriverConnection();
            return repositoryHandler.update(connection, surname, false);
        });
    }

    public boolean delete(Long id) {
        var transaction = factory.getTransaction();
        return RepositoryCallback.actionTransactional(transaction, () -> {
            var connection = transaction.getDriverConnection();
            return repositoryHandler.deleteById(connection, id);
        });
    }

    public Future<List<Surname>> findAll() {
        var transaction = factory.getTransaction();
        return RepositoryCallback.asyncAction(transaction, () -> {
            var connection = transaction.getDriverConnection();
            return repositoryHandler.selectAll(connection);
        });
    }

    public Long count() {
        var transaction = factory.getTransaction();
        return RepositoryCallback.action(transaction, () -> {
            var connection = transaction.getDriverConnection();
            return repositoryHandler.count(connection);
        });
    }

    public Page<Surname> findPaged(PageRequest pageRequest) {
        var transaction = factory.getTransaction();
        return RepositoryCallback.action(transaction, () -> {
            var connection = transaction.getDriverConnection();
            return repositoryHandler.selectPaged(connection, pageRequest);
        });
    }

    public Boolean exists(Long id) {
        var transaction = factory.getTransaction();
        return RepositoryCallback.action(transaction, () -> {
            var connection = transaction.getDriverConnection();
            return repositoryHandler.existsById(connection, id);
        });
    }

}
