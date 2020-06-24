package com.truthbean.debbie.bean;

import com.truthbean.debbie.bean.inter.A;

import javax.inject.Inject;

@BeanComponent
public class InjectTest {
    @Inject
    private A a;

    public void setA(A a) {
        this.a = a;
    }

    public A getA() {
        return a;
    }

    @Override
    public String toString() {
        return "InjectTest:{" + "\"a\":" + a + "}";
    }
}