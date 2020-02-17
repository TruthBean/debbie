package com.truthbean.debbie.tomcat;

import com.truthbean.debbie.bean.BeanFactoryHandler;
import com.truthbean.debbie.server.BaseServerProperties;
import com.truthbean.debbie.reflection.ClassLoaderUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;
import java.util.Objects;

/**
 * @author TruthBean
 * @since 0.0.1
 * Created on 2019/3/10 14:36.
 */
public class TomcatProperties extends BaseServerProperties<TomcatConfiguration> {
    //===========================================================================
    private static final String TOMCAT_WEBAPP = "debbie.server.tomcat.webapp";
    //===========================================================================

    private TomcatConfiguration configuration;

    @Override
    public TomcatConfiguration toConfiguration(BeanFactoryHandler beanFactoryHandler) {
        if (configuration != null) {
            return configuration;
        }

        var classLoader = beanFactoryHandler.getClassLoader();
        if (classLoader == null) {
            classLoader = ClassLoaderUtils.getDefaultClassLoader();
        }

        TomcatProperties properties = new TomcatProperties();
        configuration = new TomcatConfiguration(classLoader);

        properties.loadAndSet(properties, configuration);

        String userDir;
        URL userDirUrl = classLoader.getResource("");
        if (userDirUrl != null) {
            userDir = userDirUrl.getPath();
        } else {
            userDir = System.getProperty("user.dir");
        }
        LOGGER.debug("user.dir: " + userDir);
        var webappPath = classLoader.getResource("webapp");
        if (webappPath == null) {
            userDir = userDir + "/../webapp";
        } else {
            userDir = webappPath.getPath();
        }
        configuration.setWebappDir(properties.getStringValue(TOMCAT_WEBAPP, userDir));

        return configuration;
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(TomcatProperties.class);
}
