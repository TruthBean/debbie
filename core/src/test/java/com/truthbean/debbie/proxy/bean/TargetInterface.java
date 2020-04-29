package com.truthbean.debbie.proxy.bean;

import com.truthbean.debbie.proxy.MethodProxy;

public interface TargetInterface {

    @MethodProxy(order = 1)
    void code();

    default void coding() {
        System.out.println("coding");
    }

    void learn();
}
