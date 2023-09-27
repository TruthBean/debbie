package com.truthbean.debbie.check.bean;

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
        System.out.println("a construct");
    }

    @Override
    public void postConstruct() {
        System.out.println("a postConstruct");
        System.out.println(this);
    }

    @Override
    public void postCreate() {
        System.out.println("a postCreate");
        System.out.println(this);
    }

    @Override
    public String toString() {
        return "ABean:{" + "a:\'" + a + '\'' + "," + "cBean:" + cBean + '}';
    }
}
