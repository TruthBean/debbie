package com.truthbean.code.debbie.example;

import com.truthbean.code.debbie.boot.DebbieApplication;
import com.truthbean.code.debbie.core.net.NetWorkUtils;
import com.truthbean.code.debbie.tomcat.TomcatProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author TruthBean
 * @since version
 * @since 2019-03-31 18:02
 */
public class ExampleApplication {
    private static final Logger LOGGER = LoggerFactory.getLogger(ExampleApplication.class);

    public static void main(String[] args) {
        var configuration = TomcatProperties.loadProperties();
        var application = new DebbieApplication(configuration);
        application.start(args);
        LOGGER.debug("application start with http://" + NetWorkUtils.getLocalHost() + ":" + configuration.getPort());
    }
}