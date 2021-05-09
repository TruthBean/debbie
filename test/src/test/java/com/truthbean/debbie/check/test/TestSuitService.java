package com.truthbean.debbie.check.test;

import com.truthbean.debbie.bean.BeanComponent;
import org.springframework.beans.factory.annotation.Autowired;

@BeanComponent
public class TestSuitService {

    private TestComponent testComponent;

    @Autowired
    public void setTestComponent(TestComponent testComponent) {
        this.testComponent = testComponent;
    }

    public String getId() {
        System.out.println("TestSuitService");
        return testComponent.getId();
    }
}
