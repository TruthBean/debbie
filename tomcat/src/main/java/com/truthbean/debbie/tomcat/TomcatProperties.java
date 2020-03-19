package com.truthbean.debbie.tomcat;

import com.truthbean.debbie.bean.BeanFactoryHandler;
import com.truthbean.debbie.server.BaseServerProperties;
import com.truthbean.debbie.reflection.ClassLoaderUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

/**
 * @author TruthBean
 * @since 0.0.1
 * Created on 2019/3/10 14:36.
 */
public class TomcatProperties extends BaseServerProperties<TomcatConfiguration> {
    /**
     * The class name of default protocol used.
     */
    public static final String DEFAULT_PROTOCOL = "org.apache.coyote.http11.Http11NioProtocol";

    //===========================================================================
    private static final String TOMCAT_WEBAPP = "debbie.server.tomcat.webapp";
    private static final String DISABLE_MBEAN_REGISTRY = "debbie.server.tomcat.disable-mbean-registry";
    private static final String AUTO_DEPLOY = "debbie.server.tomcat.autoDeploy";
    private static final String TOMCAT_CONNECTOR_PROTOCOL = "debbie.server.tomcat.connector.protocol";
    private static final String TOMCAT_URI_ENCODING = "debbie.server.tomcat.uri-encoding";
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

        configuration.setDisableMBeanRegistry(properties.getBooleanValue(DISABLE_MBEAN_REGISTRY, false));
        configuration.setAutoDeploy(properties.getBooleanValue(AUTO_DEPLOY,false));

        configuration.setConnectorProtocol(properties.getStringValue(TOMCAT_CONNECTOR_PROTOCOL, DEFAULT_PROTOCOL));
        configuration.setUriEncoding(properties.getCharsetValue(TOMCAT_URI_ENCODING, StandardCharsets.UTF_8));

        return configuration;
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(TomcatProperties.class);
}
