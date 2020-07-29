/**
 * Copyright (c) 2020 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *         http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.check.jdbc.repository;

import com.truthbean.debbie.bean.BeanComponent;
import com.truthbean.debbie.bean.BeanInject;
import com.truthbean.debbie.check.jdbc.entity.Surname;
import com.truthbean.debbie.jdbc.annotation.JdbcTransactional;
import com.truthbean.debbie.jdbc.datasource.DataSourceConfiguration;
import com.truthbean.debbie.jdbc.datasource.DataSourceFactory;
import com.truthbean.debbie.jdbc.domain.Page;
import com.truthbean.debbie.jdbc.domain.PageRequest;
import com.truthbean.debbie.jdbc.repository.DmlRepositoryHandler;
import com.truthbean.debbie.jdbc.repository.RepositoryCallback;

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

    private final DmlRepositoryHandler<Surname, Long> repositoryHandler;

    protected SurnameRepository() {
        this.factory = null;
        this.repositoryHandler = null;
    }

    public SurnameRepository(@BeanInject DataSourceFactory factory, @BeanInject DataSourceConfiguration configuration) {
        this.factory = factory;
        repositoryHandler = DmlRepositoryHandler.of(configuration.getDriverName(), Surname.class, Long.class);
    }

    public boolean save(Surname surname) {
        var transaction = factory.getTransaction();
        Long id = RepositoryCallback.actionTransactional(transaction, () -> {
            Long insert = repositoryHandler.insert(transaction, surname, false);
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
            Long insert = repositoryHandler.insert(transaction, surname, false);
            surname.setId(insert);
            System.out.println(1 / 0);
            repositoryHandler.deleteById(transaction, deleteId);
            return repositoryHandler.selectAll(transaction);
        });
    }

    public Optional<Surname> findById(Long id) {
        var transaction = factory.getTransaction();
        return RepositoryCallback.actionOptional(transaction, () -> repositoryHandler.selectById(transaction, id));
    }

    public boolean update(Surname surname) {
        var transaction = factory.getTransaction();
        return RepositoryCallback.actionTransactional(transaction,
                () -> repositoryHandler.update(transaction, surname, false));
    }

    public boolean delete(Long id) {
        var transaction = factory.getTransaction();
        return RepositoryCallback.actionTransactional(transaction, () -> repositoryHandler.deleteById(transaction, id));
    }

    public Future<List<Surname>> findAll() {
        var transaction = factory.getTransaction();
        return RepositoryCallback.asyncAction(transaction, () -> repositoryHandler.selectAll(transaction));
    }

    @JdbcTransactional
    public Long count() {
        var transaction = factory.getTransaction();
        return RepositoryCallback.action(transaction, () -> repositoryHandler.count(transaction));
    }

    public Page<Surname> findPaged(PageRequest pageRequest) {
        var transaction = factory.getTransaction();
        return RepositoryCallback.action(transaction, () -> repositoryHandler.selectPaged(transaction, pageRequest));
    }

    public Boolean exists(Long id) {
        var transaction = factory.getTransaction();
        return RepositoryCallback.action(transaction, () -> repositoryHandler.existsById(transaction, id));
    }

}
