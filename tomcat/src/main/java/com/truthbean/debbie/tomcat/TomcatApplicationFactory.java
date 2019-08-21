package com.truthbean.debbie.tomcat;

import com.truthbean.debbie.boot.AbstractApplicationFactory;
import com.truthbean.debbie.boot.DebbieApplication;
import com.truthbean.debbie.bean.BeanFactoryHandler;
import com.truthbean.debbie.io.PathUtils;
import com.truthbean.debbie.properties.DebbieConfigurationFactory;
import com.truthbean.debbie.net.NetWorkUtils;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.WebResourceRoot;
import org.apache.catalina.connector.Connector;
import org.apache.catalina.core.StandardContext;
import org.apache.catalina.startup.Tomcat;
import org.apache.catalina.webresources.DirResourceSet;
import org.apache.catalina.webresources.StandardRoot;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * @author TruthBean
 * @since 0.0.1
 * Created on 2019/3/10 13:44.
 */
public class TomcatApplicationFactory extends AbstractApplicationFactory {

    @Override
    public boolean isWeb() {
        return true;
    }

    static {
        System.setProperty("org.apache.catalina.startup.EXIT_ON_INIT_FAILURE", "true");
    }

    private final Tomcat server = new Tomcat();

    private String configWebappDir(TomcatConfiguration configuration) {
        String webappDir = configuration.getWebappDir();
        var webappPath = new File(webappDir);
        if (!webappPath.exists()) {
            final String userDir = PathUtils.getUserDir();
            if (userDir == null) {
                try {
                    webappDir = Files.createTempDirectory("default-doc-base").toFile().getAbsolutePath();
                } catch (IOException e) {
                    LOGGER.error("create default-doc-base in temp directory error", e);
                }
            } else {
                webappDir = userDir + webappDir;
                webappPath = new File(webappDir);
                if (webappPath.exists()) {
                    webappDir = webappPath.getAbsolutePath();
                } else {
                    try {
                        webappDir = Files.createTempDirectory("default-doc-base").toFile().getAbsolutePath();
                    } catch (IOException e) {
                        LOGGER.error("create default-doc-base in temp directory error", e);
                    }
                }
            }
        } else {
            webappDir = webappPath.getAbsolutePath();
        }

        return webappDir;
    }

    private void config(TomcatConfiguration configuration) {
        server.setPort(configuration.getPort());
        server.setHostname(configuration.getHost());

        /*Connector connector = new Connector("HTTP/1.1");
        connector.setPort(configuration.getPort());
        connector.setAsyncTimeout(60000);
        server.setConnector(connector);*/

        try {
            Path tempPath = Files.createTempDirectory("tomcat-base-dir");
            server.setBaseDir(tempPath.toString());
        } catch (IOException e) {
            LOGGER.error("create tomcat-base-dir in temp directory error", e);
        }

        String webappDir = configWebappDir(configuration);
        StandardContext ctx = (StandardContext) server.addWebapp("", webappDir);
        ctx.setParentClassLoader(getClass().getClassLoader());

        try {
            String path;
            var resource = getClass().getResource("/");
            LOGGER.debug("resource: " + resource);
            if (resource == null) {
                path = new File("").getAbsolutePath();
            } else {
                path = Path.of(resource.toURI()).toString();
            }
            LOGGER.debug("configuring app with basedir: " + path);

            // Declare an alternative location for your "WEB-INF/classes" dir
            // Servlet 3.0 annotation will work
            WebResourceRoot resources = new StandardRoot(ctx);
            resources.addPreResources(new DirResourceSet(resources, "/WEB-INF/classes", path, "/"));
            ctx.setResources(resources);
        } catch (Exception e) {
            LOGGER.error("get resources and set base dir of tomcat error\n", e);
        }

        server.setBaseDir(webappDir);
        LOGGER.debug("webapp: " + webappDir);
        LOGGER.debug("tomcat config uri: http://" + configuration.getHost() + ":" + configuration.getPort());
    }

    @Override
    public DebbieApplication factory(DebbieConfigurationFactory factory, BeanFactoryHandler beanFactoryHandler) {
        TomcatConfiguration configuration = factory.factory(TomcatConfiguration.class, beanFactoryHandler);
        config(configuration);
        return tomcatApplication(configuration, beanFactoryHandler);
    }

    private DebbieApplication tomcatApplication(TomcatConfiguration configuration, BeanFactoryHandler beanFactoryHandler) {
        return new DebbieApplication() {
            @Override
            public void start(long beforeStartTime, String... args) {
                try {
                    server.init();
                    server.getConnector();
                    server.start();
                    this.beforeStart(LOGGER, beanFactoryHandler);
                    LOGGER.info("application start with http://" + NetWorkUtils.getLocalHost() + ":" + configuration.getPort());
                    LOGGER.info("application start time spends " + (System.currentTimeMillis() - beforeStartTime) + "ms");
                    Runtime.getRuntime().addShutdownHook(new Thread(() -> exit(args)));
                } catch (LifecycleException e) {
                    LOGGER.error("tomcat start error", e);
                }
            }

            @Override
            public void exit(String... args) {
                try {
                    server.stop();
                    server.destroy();
                } catch (LifecycleException e) {
                    LOGGER.error("tomcat stop error", e);
                }
            }
        };
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(TomcatApplicationFactory.class);
}
