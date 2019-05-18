package com.truthbean.debbie.tomcat;

import com.truthbean.debbie.boot.BaseServerProperties;
import com.truthbean.debbie.core.reflection.ClassLoaderUtils;

import java.util.Objects;

/**
 * @author TruthBean
 * @since 0.0.1
 * Created on 2019/3/10 14:36.
 */
public class TomcatProperties extends BaseServerProperties {
    //===========================================================================
    private static final String TOMCAT_WEBAPP = "debbie.server.tomcat.webapp";
    //===========================================================================


    private static TomcatConfiguration configuration;

    public static TomcatConfiguration toConfiguration() {
        if (configuration != null) {
            return configuration;
        }

        TomcatProperties properties = new TomcatProperties();
        configuration = new TomcatConfiguration();

        properties.loadAndSet(properties, configuration);

        ClassLoader classLoader = ClassLoaderUtils.getDefaultClassLoader();
        String userDir = Objects.requireNonNull(classLoader.getResource("")).getPath();
        var webappPath = classLoader.getResource("webapp");
        if (webappPath == null) {
            userDir = userDir + "/../webapp";
        } else {
            userDir = webappPath.getPath();
        }
        configuration.setWebappDir(properties.getStringValue(TOMCAT_WEBAPP, userDir));

        return configuration;
    }
}
