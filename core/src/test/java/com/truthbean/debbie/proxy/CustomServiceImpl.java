package com.truthbean.debbie.proxy;

import com.truthbean.debbie.bean.BeanComponent;

@BeanComponent
public class CustomServiceImpl implements CustomService {
    @Override
    @CustomMethodProxy
    public String hello() {
        return "hello";
    }
}
