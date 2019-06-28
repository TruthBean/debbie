package com.truthbean.debbie.bean.inter;

import com.truthbean.debbie.bean.BeanComponent;
import com.truthbean.debbie.bean.BeanInject;
import com.truthbean.debbie.bean.BeanType;

import java.util.UUID;

@BeanComponent(name = "a", type = BeanType.SINGLETON)
public class AImpl implements A {

    private final String a;

    private B bBean;
    private C cBean;

    public String getA() {
        return a;
    }

    public AImpl(@BeanInject("b") B bBean, @BeanInject(name = "c") C cBean) {
        this.bBean = bBean;
        this.cBean = cBean;

        a = "A --- " + UUID.randomUUID().toString();
    }

    @Override
    public String toString() {
        return "ABean:{" + "a:\'" + a + '\'' + "," + "bBean:" + bBean + "," + "cBean:" + cBean + '}';
    }
}