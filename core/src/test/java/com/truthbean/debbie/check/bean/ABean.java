package com.truthbean.debbie.check.bean;

import com.truthbean.debbie.bean.BeanComponent;
import com.truthbean.debbie.bean.BeanInject;
import com.truthbean.debbie.bean.BeanType;

import java.util.UUID;

@BeanComponent(type = BeanType.SINGLETON)
public class ABean {

    private final String a;

    private BBean bBean;
    private CBean cBean;

    public String getA() {
        return a;
    }

    public ABean(@BeanInject BBean bBean, @BeanInject CBean cBean) {
        this.bBean = bBean;
        this.cBean = cBean;

        a = "A --- " + UUID.randomUUID().toString();
    }

    @Override
    public String toString() {
        return "ABean:{" + "a:\'" + a + '\'' + "," + "bBean:" + bBean + "," + "cBean:" + cBean + '}';
    }
}
