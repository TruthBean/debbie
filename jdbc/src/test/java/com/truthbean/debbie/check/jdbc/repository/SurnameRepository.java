/**
 * Copyright (c) 2021 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *         http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.check.jdbc.repository;

import com.truthbean.Logger;
import com.truthbean.LoggerFactory;
import com.truthbean.debbie.bean.BeanComponent;
import com.truthbean.debbie.bean.BeanInject;
import com.truthbean.debbie.check.jdbc.entity.Surname;
import com.truthbean.debbie.jdbc.annotation.JdbcTransactional;
import com.truthbean.debbie.jdbc.datasource.DataSourceConfiguration;
import com.truthbean.debbie.jdbc.datasource.DataSourceFactory;
import com.truthbean.debbie.jdbc.domain.Page;
import com.truthbean.debbie.jdbc.domain.PageRequest;
import com.truthbean.debbie.jdbc.entity.EntityResolver;
import com.truthbean.debbie.jdbc.repository.DebbieRepositoryTemplate;
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

    private final DebbieRepositoryTemplate<Surname, Long> repositoryHandler;

    public SurnameRepository(@BeanInject DataSourceFactory factory) {
        this.factory = factory;
        repositoryHandler = new DebbieRepositoryTemplate<>(Long.class, Surname.class, EntityResolver.getInstance(), LOGGER);
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
            return repositoryHandler.findAll(transaction);
        });
    }

    public Optional<Surname> findById(Long id) {
        var transaction = factory.getTransaction();
        return RepositoryCallback.actionOptional(transaction, () -> repositoryHandler.findById(transaction, id));
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
        return RepositoryCallback.asyncAction(transaction, () -> repositoryHandler.findAll(transaction));
    }

    @JdbcTransactional
    public Long count() {
        var transaction = factory.getTransaction();
        return RepositoryCallback.action(transaction, () -> repositoryHandler.count(transaction));
    }

    public Page<Surname> findPaged(PageRequest pageRequest) {
        var transaction = factory.getTransaction();
        return RepositoryCallback.action(transaction, () -> repositoryHandler.findPaged(transaction, pageRequest));
    }

    public Boolean exists(Long id) {
        var transaction = factory.getTransaction();
        return RepositoryCallback.action(transaction, () -> repositoryHandler.existsById(transaction, id));
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(SurnameRepository.class);

}
