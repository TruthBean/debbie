package com.truthbean.debbie.check.bean;

import com.truthbean.debbie.bean.BeanComponent;
import com.truthbean.debbie.bean.BeanInject;
import com.truthbean.debbie.bean.BeanType;

import java.util.UUID;

@BeanComponent(type = BeanType.SINGLETON)
public class BBean {

    private final String b;

    private final CBean cBean;

    public String getB() {
        return b;
    }

    public BBean(@BeanInject CBean cBean) {
        this.cBean = cBean;

        b = "B --- " + UUID.randomUUID().toString();
    }

    @Override
    public String toString() {
        return "BBean:{" + "b:\'" + b + '\'' + "," + "cBean:" + cBean + '}';
    }
}
