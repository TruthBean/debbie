package com.truthbean.debbie.check.properties;

import com.truthbean.debbie.properties.NestedPropertiesConfiguration;
import com.truthbean.debbie.properties.PropertiesConfiguration;

@PropertiesConfiguration(keyPrefix = "test")
public class PropertiesConfigurationTest {

    @NestedPropertiesConfiguration
    private Hehe hehe = new Hehe();

    public void setHehe(Hehe hehe) {
        this.hehe = hehe;
    }

    public Hehe getHehe() {
        return hehe;
    }

    public static class Hehe {
        private String name;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }
}
