package com.truthbean.debbie.check.test;

import com.truthbean.Console;
import com.truthbean.debbie.bean.BeanComponent;

import java.util.UUID;

@BeanComponent
public class TestComponent {

    public String getId() {
        Console.println("TestComponent");
        return UUID.randomUUID().toString();
    }
}
