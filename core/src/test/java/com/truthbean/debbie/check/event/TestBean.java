package com.truthbean.debbie.check.event;

import com.truthbean.debbie.bean.BeanComponent;

import java.util.UUID;

@BeanComponent
public class TestBean {

    private String uuid;

    public TestBean() {
        this.uuid = UUID.randomUUID().toString();
    }

    @Override
    public String toString() {
        return this.uuid;
    }
}
