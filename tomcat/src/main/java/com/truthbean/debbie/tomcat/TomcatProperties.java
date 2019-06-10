package com.truthbean.debbie.tomcat;

import com.truthbean.debbie.bean.BeanFactoryHandler;
import com.truthbean.debbie.server.BaseServerProperties;
import com.truthbean.debbie.reflection.ClassLoaderUtils;

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

    private TomcatConfiguration configuration;

    @Override
    public TomcatConfiguration toConfiguration(BeanFactoryHandler beanFactoryHandler) {
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
