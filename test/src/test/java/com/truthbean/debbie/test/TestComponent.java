package com.truthbean.debbie.test;

import com.truthbean.debbie.bean.BeanComponent;

import java.util.UUID;

@BeanComponent
public class TestComponent {

    public String getId() {
        System.out.println("TestComponent");
        return UUID.randomUUID().toString();
    }
}
