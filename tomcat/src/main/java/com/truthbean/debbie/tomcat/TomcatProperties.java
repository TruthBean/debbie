/**
 * Copyright (c) 2020 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *         http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.tomcat;

import com.truthbean.debbie.bean.BeanFactoryHandler;
import com.truthbean.debbie.reflection.ClassLoaderUtils;
import com.truthbean.debbie.server.BaseServerProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;
import java.nio.charset.StandardCharsets;

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

    private static final String TOMCAT_RESOURCES_CACHING_ALLOWED = "debbie.server.tomcat.resources.caching-allowed";
    private static final String TOMCAT_RESOURCES_MAX_CACHE = "debbie.server.tomcat.resources.max-cache";
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

        configuration.setCachingAllowed(properties.getBooleanValue(TOMCAT_RESOURCES_CACHING_ALLOWED, true));
        configuration.setCacheMaxSize(properties.getIntegerValue(TOMCAT_RESOURCES_MAX_CACHE, 102400));

        return configuration;
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(TomcatProperties.class);
}
