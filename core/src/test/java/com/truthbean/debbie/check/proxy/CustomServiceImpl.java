package com.truthbean.debbie.check.proxy;

import com.truthbean.debbie.bean.BeanComponent;

@BeanComponent
public class CustomServiceImpl implements CustomService {

    private static final String hello;
    static {
        hello = "static hello ---> v6";
    }

    @Override
    @CustomMethodProxy
    public String hello() {
        return hello;
    }
}
