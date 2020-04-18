package com.truthbean.debbie.util;

/**
 * @author truthbean/Rogar·Q
 * @since 0.0.2
 * Created on 2020-04-07 16:36.
 */
public class JacksonTarget implements JacksonTargetA, JacksonTargetB {
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
