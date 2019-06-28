package com.truthbean.debbie.bean.inter;

import com.truthbean.debbie.bean.BeanComponent;
import com.truthbean.debbie.bean.BeanInject;
import com.truthbean.debbie.bean.BeanType;

import java.util.UUID;

@BeanComponent(name = "c", type = BeanType.SINGLETON)
public class CImpl implements C {

    private final String c;

    @BeanInject(name = "a")
    private A aBean;

    @BeanInject(name = "b")
    private B bBean;


    public String getC() {
        return c;
    }

    public CImpl() {
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