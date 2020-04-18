package com.truthbean.debbie.util;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author truthbean/Rogar·Q
 * @since 0.0.2
 * Created on 2020-04-07 16:37.
 */
public interface JacksonTargetB {

    @JsonProperty("b_name")
    String getName();

    @JsonProperty("b_name")
    void setName(String name);
}
