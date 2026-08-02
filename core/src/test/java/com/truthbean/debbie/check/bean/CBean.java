package com.truthbean.debbie.check.bean;

import com.truthbean.Console;
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
        Console.println("c construct");
    }

    @Override
    public void postConstruct() {
        Console.println("c postConstruct");
        Console.println(this);
    }

    @Override
    public void postCreate() {
        Console.println("c postCreate");
        Console.println(this);
    }

    @BeanInject
    public void setABean(BBean bBean) {
        this.bBean = bBean;
        Console.println("c setABean: " + bBean);
    }

    public void setBBean(ABean aBean) {
        this.aBean = aBean;
        Console.println("c setBBean: " + aBean);
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