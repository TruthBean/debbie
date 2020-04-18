package com.truthbean.debbie.aio;

import com.truthbean.debbie.bean.BeanFactoryHandler;
import com.truthbean.debbie.bean.BeanScanConfiguration;
import com.truthbean.debbie.mvc.MvcConfiguration;
import com.truthbean.debbie.mvc.MvcProperties;
import com.truthbean.debbie.properties.ClassesScanProperties;
import com.truthbean.debbie.server.BaseServerProperties;

/**
 * @author TruthBean/Rogar·Q
 * @since 0.0.2
 * Created on 2019-12-17 20:38
 */
public class AioServerProperties extends BaseServerProperties<AioServerConfiguration> {

    private AioServerConfiguration configuration = null;

    // ===========================================================================
    private static final String HTTP_VERSION = "debbie.server.aio.http.version";
    private static final String SERVER_MESSAGE = "debbie.server.aio.message";
    // ===========================================================================

    @Override
    public AioServerConfiguration toConfiguration(BeanFactoryHandler beanFactoryHandler) {
        if (configuration != null) {
            return configuration;
        }

        var classLoader = beanFactoryHandler.getClassLoader();

        configuration = new AioServerConfiguration(classLoader);

        BeanScanConfiguration beanConfiguration = ClassesScanProperties.toConfiguration(classLoader);
        MvcConfiguration mvcConfiguration = MvcProperties.toConfiguration(classLoader);
        configuration.copyFrom(mvcConfiguration);
        configuration.copyFrom(beanConfiguration);

        String httpVersion = getStringValue(HTTP_VERSION, "1.1");
        configuration.setHttpVersion(httpVersion);
        String serverMessage = getStringValue(SERVER_MESSAGE, "A Simple Java Aio WebServer by Debbie Framework");
        configuration.setServerMessage(serverMessage);

        AioServerProperties properties = new AioServerProperties();
        properties.loadAndSet(properties, configuration);

        return configuration;
    }
}
