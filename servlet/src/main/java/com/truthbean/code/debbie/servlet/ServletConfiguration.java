package com.truthbean.code.debbie.servlet;

import com.truthbean.code.debbie.mvc.MvcConfiguration;

/**
 * @author TruthBean
 * @since 0.0.1
 * Created on 2019/3/10 18:56.
 */
public class ServletConfiguration extends MvcConfiguration {
    private String dispatcherMapping;

    public ServletConfiguration() {
    }

    public String getDispatcherMapping() {
        return dispatcherMapping;
    }

    public void setDispatcherMapping(String dispatcherMapping) {
        this.dispatcherMapping = dispatcherMapping;
    }
}
