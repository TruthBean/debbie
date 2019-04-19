package com.truthbean.debbie.jdbc.repository;

import com.truthbean.debbie.jdbc.domain.PageRequest;
import com.truthbean.debbie.jdbc.entity.Surname;

import java.util.List;

/**
 * @author TruthBean
 * @since 0.0.1
 */
public class SurnameRepository extends DmlRepositoryHandler<Surname, Long> {
    private RepositoryHandler repositoryHandler;

    public SurnameRepository(RepositoryHandler repositoryHandler) {
        super(repositoryHandler, Surname.class, Long.class);
        this.repositoryHandler = repositoryHandler;
    }

    public boolean save(Surname surname) {
        Long id = repositoryHandler.actionTransactional(() -> {
            Long insert = super.insert(surname);
            surname.setId(insert);
            return insert;
        });
        boolean result = id > 0L;
        if (result) {
            surname.setId(id);
        }
        return result;
    }

    public Surname findById(Long id) {
        return super.findById(id);
    }

    public boolean update(Surname surname) {
        return repositoryHandler.actionTransactional(() -> super.update(surname));
    }

    public boolean delete(Long id) {
        return repositoryHandler.actionTransactional(() -> super.deleteById(id));
    }

    public List<Surname> findAll() {
        return super.findAll();
    }

    public Long count() {
        return super.count();
    }

    public Page<Surname> findPaged(PageRequest pageRequest) {
        return super.findPaged(pageRequest);
    }

}
