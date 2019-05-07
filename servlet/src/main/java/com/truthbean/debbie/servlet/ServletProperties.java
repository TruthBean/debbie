package com.truthbean.debbie.servlet;

import com.truthbean.debbie.core.bean.BeanScanConfiguration;
import com.truthbean.debbie.core.properties.ClassesScanProperties;
import com.truthbean.debbie.mvc.MvcConfiguration;
import com.truthbean.debbie.mvc.MvcProperties;

/**
 * @author TruthBean
 * @since 0.0.1
 * Created on 2019/3/10 17:39.
 */
public class ServletProperties extends MvcProperties {

    //========================================================================================

    //========================================================================================

    private static ServletConfiguration configuration;
    public static ServletConfiguration toConfiguration() {
        if (configuration != null) {
            return configuration;
        }

        configuration = new ServletConfiguration();

        final ServletProperties properties = new ServletProperties();

        MvcConfiguration webConfiguration = MvcProperties.toConfiguration();
        configuration.copyFrom(webConfiguration);

        BeanScanConfiguration beanScanConfiguration = ClassesScanProperties.toConfiguration();
        configuration.copyFrom(beanScanConfiguration);

        return configuration;
    }
}
