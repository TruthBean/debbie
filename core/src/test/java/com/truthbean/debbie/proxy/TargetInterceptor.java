package com.truthbean.debbie.proxy;

public class TargetInterceptor {
    public void before() {
        System.out.println("思考中......");
    }

    public void after() {
        System.out.println("检查中......");
    }
}
