package com.truthbean.code.debbie.tomcat;

import com.truthbean.code.debbie.boot.AbstractServerProperties;
import com.truthbean.code.debbie.core.reflection.ClassLoaderUtils;

import java.util.Objects;

/**
 * @author TruthBean
 * @since 0.0.1
 * Created on 2019/3/10 14:36.
 */
public class TomcatProperties extends AbstractServerProperties {
    //===========================================================================
    private static final String TOMCAT_WEBAPP = "debbie.server.tomcat.webapp";
    //===========================================================================

    public static TomcatConfiguration loadProperties() {
        TomcatProperties properties = new TomcatProperties();
        TomcatConfiguration configuration = new TomcatConfiguration();

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
