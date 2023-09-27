package com.truthbean.debbie.check.bean;

import com.truthbean.debbie.bean.*;

import java.util.UUID;

@BeanComponent(type = BeanType.SINGLETON)
public class CBean implements ConstructPost, CreatePost {

    private final String c;

    private BBean bBean;

    @BeanInject
    private ABean aBean;

    public CBean() {
        c = "C --- " + UUID.randomUUID();
        System.out.println("c construct");
    }

    @Override
    public void postConstruct() {
        System.out.println("c postConstruct");
        System.out.println(this);
    }

    @Override
    public void postCreate() {
        System.out.println("c postCreate");
        System.out.println(this);
    }

    @BeanInject
    public void setABean(BBean bBean) {
        this.bBean = bBean;
        System.out.println("c setABean: " + bBean);
    }

    public void setBBean(ABean aBean) {
        this.aBean = aBean;
        System.out.println("c setBBean: " + aBean);
    }

    public String getC() {
        return c;
    }

    @Override
    public String toString() {
        String a = null, b = null;
        if (bBean != null) {
            a = bBean.getB();
        }
        if (aBean != null) {
            b = aBean.getA();
        }
        return "{" +
                "\"c\":\"" + c + '\"' +
                ",\"bBean\":" + a +
                ",\"aBean\":" + b +
                '}';
    }
}