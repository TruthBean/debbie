package com.truthbean.debbie.test;

import com.truthbean.debbie.bean.BeanComponent;

import java.util.UUID;

@BeanComponent
public class TestSuitService {

    public String getId() {
        return UUID.randomUUID().toString();
    }
}
