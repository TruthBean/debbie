package com.truthbean.debbie.jdbc.service;

import com.truthbean.debbie.bean.BeanComponent;
import com.truthbean.debbie.bean.BeanInject;
import com.truthbean.debbie.jdbc.entity.Surname;

import java.util.List;

/**
 * @author TruthBean
 * @since 0.0.2
 * Created on 2019-11-28 21:34.
 */
@BeanComponent
public class ApiService {

    private final SurnameService surnameService;

    public ApiService(@BeanInject SurnameService surnameService) {
        this.surnameService = surnameService;
    }

    public List<Surname> selectAll() {
        return this.surnameService.list();
    }
}
