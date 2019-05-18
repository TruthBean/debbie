package com.truthbean.debbie.jdbc.repository;

import com.truthbean.debbie.core.bean.BeanComponent;

import java.util.UUID;

@BeanComponent
public class TmpRepository {

    private String tmp;

    public TmpRepository() {
        tmp = UUID.randomUUID().toString();
        System.out.println(tmp);
    }

    public String getTmp() {
        return tmp;
    }

    @Override
    public String toString() {
        return "tmp: " + tmp;
    }
}
