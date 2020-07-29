package com.truthbean.debbie.check.properties;

import com.truthbean.debbie.properties.PropertiesConfiguration;
import com.truthbean.debbie.properties.PropertyInject;

@PropertiesConfiguration(keyPrefix = "test")
public class PropertiesConfigurationTest {

    @PropertyInject("hehe")
    private String hehe = "hehe";

    public void setHehe(String hehe) {
        this.hehe = hehe;
    }

    public String getHehe() {
        return hehe;
    }
}
