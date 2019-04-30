package com.truthbean.debbie.servlet;

import com.truthbean.debbie.mvc.MvcConfiguration;
import com.truthbean.debbie.mvc.MvcProperties;

/**
 * @author TruthBean
 * @since 0.0.1
 * Created on 2019/3/10 17:39.
 */
public class ServletProperties extends MvcProperties {

    //========================================================================================
    private static final String DISPATCHER_MAPPING = "debbie.web.servlet.dispatcher-mapping";
    //========================================================================================

    public static ServletConfiguration toConfiguration() {
        final ServletProperties properties = new ServletProperties();

        ServletConfiguration configuration = new ServletConfiguration();
        configuration.setDispatcherMapping(properties.getStringValue(DISPATCHER_MAPPING, "/"));

        MvcConfiguration webConfiguration = MvcProperties.toConfiguration();
        configuration.copyFrom(webConfiguration);
        return configuration;
    }
}
