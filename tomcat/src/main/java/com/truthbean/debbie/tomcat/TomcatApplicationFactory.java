package com.truthbean.debbie.tomcat;

import com.truthbean.debbie.boot.AbstractApplicationFactory;
import com.truthbean.debbie.boot.DebbieApplication;
import com.truthbean.debbie.core.bean.BeanScanConfiguration;
import com.truthbean.debbie.core.net.NetWorkUtils;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.WebResourceRoot;
import org.apache.catalina.core.StandardContext;
import org.apache.catalina.startup.Tomcat;
import org.apache.catalina.webresources.DirResourceSet;
import org.apache.catalina.webresources.StandardRoot;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * @author TruthBean
 * @since 0.0.1
 * Created on 2019/3/10 13:44.
 */
public class TomcatApplicationFactory extends AbstractApplicationFactory<TomcatConfiguration> {

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
            try {
                webappDir = Files.createTempDirectory("default-doc-base").toFile().getAbsolutePath();
            } catch (IOException e) {
                LOGGER.error("create default-doc-base in temp directory error", e);
            }
        } else {
            webappDir = webappPath.getAbsolutePath();
        }

        return webappDir;
    }

    private void config(TomcatConfiguration configuration) {
        server.setPort(configuration.getPort());
        server.setHostname(configuration.getHost());

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
            Path path = Paths.get(getClass().getResource("/").toURI());
            LOGGER.debug("configuring app with basedir: " + path.toString());

            // Declare an alternative location for your "WEB-INF/classes" dir
            // Servlet 3.0 annotation will work
            WebResourceRoot resources = new StandardRoot(ctx);
            resources.addPreResources(new DirResourceSet(resources, "/WEB-INF/classes", path.toString(), "/"));
            ctx.setResources(resources);
        } catch (URISyntaxException e) {
            LOGGER.error("get resources and set base dir of tomcat error", e);
        }

        server.setBaseDir(webappDir);
        LOGGER.debug("webapp: " + webappDir);
        LOGGER.debug("tomcat config uri: http://" + configuration.getHost() + ":" + configuration.getPort());
    }

    @Override
    public DebbieApplication factory(TomcatConfiguration configuration) {
        config(configuration);
        return tomcatApplication(configuration);
    }

    private DebbieApplication tomcatApplication(TomcatConfiguration configuration) {
        return new DebbieApplication() {
            @Override
            public void start(String... args) {
                try {
                    server.init();
                    server.getConnector();
                    server.start();
                    LOGGER.info("application start with http://" + NetWorkUtils.getLocalHost() + ":" + configuration.getPort());
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
