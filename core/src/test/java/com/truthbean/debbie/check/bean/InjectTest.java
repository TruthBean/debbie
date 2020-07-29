package com.truthbean.debbie.check.bean;

import com.truthbean.debbie.bean.BeanComponent;
import com.truthbean.debbie.bean.BeanInject;
import com.truthbean.debbie.bean.inter.A;
import com.truthbean.debbie.bean.inter.DemoBeanComponent;

import javax.inject.Inject;

@BeanComponent
public class InjectTest {
    @Inject
    private A a;

    @BeanInject
    private DemoBeanComponent component;

    public void setA(A a) {
        this.a = a;
    }

    public A getA() {
        return a;
    }

    @Override
    public String toString() {
        return "InjectTest:{" +
                "\"a\":" + a +
                ",\"component\":" + component +
                '}';
    }
}