package com.truthbean.debbie.jdbc.service;

import com.truthbean.debbie.core.bean.BeanComponent;
import com.truthbean.debbie.core.bean.BeanInject;
import com.truthbean.debbie.jdbc.entity.Surname;
import com.truthbean.debbie.jdbc.repository.SurnameJdbcRepository;

import java.util.List;
import java.util.Optional;

@BeanComponent("surnameService")
public class SurnameServiceImpl implements SurnameService {

    @BeanInject
    private SurnameJdbcRepository surnameJdbcRepository;

    public boolean save(Surname surname) {
        Long id = surnameJdbcRepository.insert(surname);
        boolean result = id > 0L;
        if (result) {
            surname.setId(id);
        }
        return result;
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
