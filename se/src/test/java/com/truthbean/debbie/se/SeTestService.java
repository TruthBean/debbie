package com.truthbean.debbie.se;

import com.truthbean.debbie.bean.BeanComponent;

import java.util.UUID;

@BeanComponent
public class SeTestService {

    public String getUuid() {
        return UUID.randomUUID().toString();
    }
}
