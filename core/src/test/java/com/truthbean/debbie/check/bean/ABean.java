package com.truthbean.debbie.check.bean;

import com.truthbean.Console;
import com.truthbean.debbie.bean.*;

import java.util.UUID;

@BeanComponent(type = BeanType.SINGLETON)
public class ABean implements ConstructPost, CreatePost {

    private final String a;

    private final CBean cBean;

    public String getA() {
        return a;
    }

    public ABean(@BeanInject CBean cBean) {
        this.cBean = cBean;

        a = "a --- " + UUID.randomUUID().toString();
        Console.println("a construct");
    }

    @Override
    public void postConstruct() {
        Console.println("a postConstruct");
        Console.println(this);
    }

    @Override
    public void postCreate() {
        Console.println("a postCreate");
        Console.println(this);
    }

    @Override
    public String toString() {
        return "ABean:{" + "a:\'" + a + '\'' + "," + "cBean:" + cBean + '}';
    }
}
