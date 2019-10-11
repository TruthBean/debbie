package com.truthbean.debbie.rmi;

import com.truthbean.debbie.bean.BeanComponent;

import java.io.Serializable;
import java.util.UUID;

@BeanComponent
public class TestBeanD implements Serializable {

    private String id;

    public TestBeanD() {
        this.id = UUID.randomUUID().toString();
    }

    public String getId() {
        return id;
    }
}
