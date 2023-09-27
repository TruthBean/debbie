package com.truthbean.debbie.check.bean;

import com.truthbean.debbie.bean.*;

import java.util.UUID;

@BeanComponent(type = BeanType.SINGLETON)
public class ABCBean implements ConstructPost, CreatePost {

    private final String a;

    private final BBean bBean;
    private final ABean aBean;
    private final CBean cBean;

    public ABCBean(@BeanInject BBean bBean, @BeanInject ABean aBean, @BeanInject CBean cBean) {
        this.bBean = bBean;
        this.aBean = aBean;
        this.cBean = cBean;

        a = "ABC --- " + UUID.randomUUID().toString();
        System.out.println("abc construct");
    }

    @Override
    public void postConstruct() {
        System.out.println("abc postConstruct");
        System.out.println(this);
    }

    @Override
    public void postCreate() {
        System.out.println("abc postCreate");
        System.out.println(this);
    }

    public BBean getaBean() {
        return bBean;
    }

    public ABean getbBean() {
        return aBean;
    }

    public CBean getcBean() {
        return cBean;
    }

    @Override
    public String toString() {
        return "ABCBean:{" + "a:\'" + a + '\'' + "," + "bBean:" + bBean + "," + "aBean:" + aBean + "," + "cBean:" + cBean + '}';
    }
}