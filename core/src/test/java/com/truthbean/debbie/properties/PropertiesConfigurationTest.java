package com.truthbean.debbie.properties;

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
