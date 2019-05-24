package com.truthbean.debbie.jdbc.service;

import com.truthbean.debbie.core.bean.BeanComponent;
import com.truthbean.debbie.core.bean.BeanInject;
import com.truthbean.debbie.jdbc.annotation.JdbcTransactional;
import com.truthbean.debbie.jdbc.entity.Surname;
import com.truthbean.debbie.jdbc.repository.SurnameJdbcRepository;

import java.util.List;
import java.util.Optional;

@BeanComponent("surnameService")
@JdbcTransactional
public class SurnameServiceImpl implements SurnameService {

    @BeanInject
    private SurnameJdbcRepository surnameJdbcRepository;

    @JdbcTransactional(rollbackFor = ArithmeticException.class, forceCommit = false, readonly = false)
    public boolean save(Surname surname) {
        var all = surnameJdbcRepository.findAll();
        System.out.println(all);
        boolean id = surnameJdbcRepository.update(surname);
        System.out.println(surname.getId() / 0L);
        return id;
    }

    public Optional<Surname> selectById(Long id) {
        Surname surname = surnameJdbcRepository.findById(id);
        if (surname == null)
            return Optional.empty();
        else
            return Optional.of(surname);
    }

    @Override
    public List<Surname> selectAll() {
        return surnameJdbcRepository.findAll();
    }

    @Override
    public void doNothing() {
        System.out.println("none ...");
    }
}
