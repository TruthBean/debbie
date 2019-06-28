package com.truthbean.debbie.bean.inter;

import com.truthbean.debbie.bean.BeanComponent;
import com.truthbean.debbie.bean.BeanInject;
import com.truthbean.debbie.bean.BeanType;

import java.util.UUID;

@BeanComponent(name = "b", type = BeanType.SINGLETON)
public class BImpl implements B {

    private final String b;

    private final C cBean;

    public String getB() {
        return b;
    }

    public BImpl(@BeanInject(name = "c") C cBean) {
        this.cBean = cBean;

        b = "B --- " + UUID.randomUUID().toString();
    }

    @Override
    public String toString() {
        return "BBean:{" + "b:\'" + b + '\'' + "," + "cBean:" + cBean + '}';
    }
}