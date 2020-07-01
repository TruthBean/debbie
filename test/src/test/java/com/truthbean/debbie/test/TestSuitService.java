package com.truthbean.debbie.test;

import com.truthbean.debbie.bean.BeanComponent;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.UUID;

@BeanComponent
public class TestSuitService {

    @Autowired
    private TestComponent testComponent;

    public String getId() {
        System.out.println("TestSuitService");
        return testComponent.getId();
    }
}
