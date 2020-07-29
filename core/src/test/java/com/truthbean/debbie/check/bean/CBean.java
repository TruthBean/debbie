package com.truthbean.debbie.check.bean;

import com.truthbean.debbie.bean.BeanComponent;
import com.truthbean.debbie.bean.BeanInject;
import com.truthbean.debbie.bean.BeanType;

import java.util.UUID;

@BeanComponent(type = BeanType.SINGLETON)
public class CBean {

    private final String c;

    @BeanInject
    private ABean aBean;

    @BeanInject
    private BBean bBean;


    public String getC() {
        return c;
    }

    public CBean() {
        c = "C --- " + UUID.randomUUID().toString();
    }

    @Override
    public String toString() {
        String a = null, b = null;
        if (aBean != null) {
            a = aBean.getA();
        }
        if (bBean != null) {
            b = bBean.getB();
        }
        return "{" +
                "\"c\":\"" + c + '\"' +
                ",\"aBean\":" + a +
                ",\"bBean\":" + b +
                '}';
    }
}