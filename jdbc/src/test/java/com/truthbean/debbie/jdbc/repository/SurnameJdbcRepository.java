package com.truthbean.debbie.jdbc.repository;

import com.truthbean.debbie.core.bean.BeanInject;
import com.truthbean.debbie.jdbc.annotation.SqlRepository;
import com.truthbean.debbie.jdbc.domain.Page;
import com.truthbean.debbie.jdbc.domain.PageRequest;
import com.truthbean.debbie.jdbc.entity.Surname;

import java.util.List;
import java.util.Optional;

@SqlRepository
public class SurnameJdbcRepository extends JdbcRepository<Surname, Long> {

    public SurnameJdbcRepository(@BeanInject TmpRepository tmpRepository) {
        System.out.println(tmpRepository);
    }

    public boolean save(Surname surname) {
        Long id = super.insert(surname);
        boolean result = id > 0L;
        if (result) {
            surname.setId(id);
        }
        return result;
    }

    public List<Surname> saveAndDelete(Surname surname, Long deleteId) {
        Long insert = super.insert(surname);
        surname.setId(insert);
        System.out.println(1/0);
        super.deleteById(deleteId);
        return super.findAll();
    }

    public Optional<Surname> selectById(Long id) {
        Surname surname = super.findById(id);
        if (surname != null)
            return Optional.of(surname);
        else return Optional.empty();
    }

    public boolean update(Surname surname) {
        return super.update(surname);
    }

    public boolean delete(Long id) {
        return super.deleteById(id);
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
