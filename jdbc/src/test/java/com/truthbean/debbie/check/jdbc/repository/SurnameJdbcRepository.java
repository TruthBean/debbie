/**
 * Copyright (c) 2023 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *         http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.check.jdbc.repository;

import com.truthbean.debbie.bean.BeanInject;
import com.truthbean.debbie.check.jdbc.entity.Surname;
import com.truthbean.debbie.jdbc.annotation.SqlRepository;
import com.truthbean.debbie.jdbc.domain.Page;
import com.truthbean.debbie.jdbc.domain.PageRequest;
import com.truthbean.debbie.jdbc.repository.DebbieRepository;

import java.util.List;
import java.util.Optional;

@SqlRepository
public class SurnameJdbcRepository extends DebbieRepository<Surname, Long> {

    public SurnameJdbcRepository(@BeanInject TmpRepository tmpRepository) {
        System.out.println(tmpRepository);
    }

    public boolean insert(Surname surname) {
        Long id = super.insert(surname, false);
        boolean result = id > 0L;
        if (result) {
            surname.setId(id);
        }
        return result;
    }

    public List<Surname> saveAndDelete(Surname surname, Long deleteId) {
        Long insert = super.insert(surname, false);
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
        return super.update(surname, false);
    }

    public boolean delete(Long id) {
        return super.deleteById(id);
    }
}
