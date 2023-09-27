package com.truthbean.debbie.check.bean;

import com.truthbean.debbie.bean.*;

import java.util.UUID;

@BeanComponent(type = BeanType.SINGLETON)
public class BBean implements ConstructPost, CreatePost {

    private final String b;

    private final ABean aBean;
    private final CBean cBean;

    public String getB() {
        return b;
    }

    public BBean(@BeanInject ABean aBean, @BeanInject CBean cBean) {
        this.aBean = aBean;
        this.cBean = cBean;

        b = "b --- " + UUID.randomUUID();
        System.out.println("b construct");
    }

    @Override
    public void postConstruct() {
        System.out.println("b postConstruct");
        System.out.println(this);
    }

    @Override
    public void postCreate() {
        System.out.println("b postCreate");
        System.out.println(this);
    }

    @Override
    public String toString() {
        return "BBean:{" + "b:\'" + b + '\'' + "," + "aBean:" + aBean + "," + "cBean:" + cBean + '}';
    }
}
