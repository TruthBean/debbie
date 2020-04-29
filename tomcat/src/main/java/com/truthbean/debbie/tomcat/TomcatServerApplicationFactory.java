package com.truthbean.debbie.tomcat;

import com.truthbean.debbie.bean.BeanFactoryHandler;
import com.truthbean.debbie.boot.AbstractDebbieApplication;
import com.truthbean.debbie.boot.DebbieApplication;
import com.truthbean.debbie.io.PathUtils;
import com.truthbean.debbie.properties.DebbieConfigurationFactory;
import com.truthbean.debbie.server.AbstractWebServerApplicationFactory;
import com.truthbean.debbie.util.StringUtils;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.WebResourceRoot;
import org.apache.catalina.connector.Connector;
import org.apache.catalina.core.StandardContext;
import org.apache.catalina.startup.Tomcat;
import org.apache.catalina.webresources.DirResourceSet;
import org.apache.catalina.webresources.StandardRoot;
import org.apache.coyote.AbstractProtocol;
import org.apache.tomcat.util.descriptor.web.ErrorPage;
import org.apache.tomcat.util.modeler.Registry;
import org.apache.tomcat.util.scan.StandardJarScanFilter;
import org.apache.tomcat.util.scan.StandardJarScanner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

/**
 * @author TruthBean
 * @since 0.0.1
 * Created on 2019/3/10 13:44.
 */
public class TomcatServerApplicationFactory extends AbstractWebServerApplicationFactory {

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
            final String userDir = PathUtils.getUserDir(configuration.getClassLoader());
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

    private void customizeConnector(TomcatConfiguration configuration, Connector connector) {
        int port = Math.max(configuration.getPort(), 0);
        connector.setPort(port);
        if (StringUtils.hasText(configuration.getServerHeader())) {
            connector.setAttribute("server", configuration.getServerHeader());
        }
        if (connector.getProtocolHandler() instanceof AbstractProtocol) {
            if (StringUtils.hasText(configuration.getHost())) {
                var abstractProtocol = (AbstractProtocol<?>) connector.getProtocolHandler();
                try {
                    abstractProtocol.setAddress(InetAddress.getByName(configuration.getHost()));
                } catch (UnknownHostException e) {
                    LOGGER.error("tomcat connector bind address error. ", e);
                }
            }
        }

        if (configuration.getUriEncoding() != null) {
            connector.setURIEncoding(configuration.getUriEncoding().name());
        }
        // Don't bind to the socket prematurely if ApplicationContext is slow to start
        connector.setProperty("bindOnInit", "false");
        // TODO ssl
    }

    private void config(TomcatConfiguration configuration, ClassLoader classLoader, List<ErrorPage> errorPages) {
        if (configuration.isDisableMBeanRegistry()) {
            Registry.disableRegistry();
        }
        server.setPort(configuration.getPort());
        server.setHostname(configuration.getHost());

        Connector connector = new Connector(configuration.getConnectorProtocol());
        connector.setThrowOnFailure(true);
        server.getService().addConnector(connector);
        customizeConnector(configuration, connector);
        server.setConnector(connector);
        server.getHost().setAutoDeploy(configuration.isAutoDeploy());

        try {
            Path tempPath = Files.createTempDirectory("tomcat-base-dir");
            server.setBaseDir(tempPath.toString());
        } catch (IOException e) {
            LOGGER.error("create tomcat-base-dir in temp directory error", e);
        }

        String webappDir = configWebappDir(configuration);
        StandardContext ctx = (StandardContext) server.addWebapp("", webappDir);

        StandardJarScanFilter filter = new StandardJarScanFilter();
        filter.setTldSkip(TldSkipPatterns.tldSkipPatterns());
        StandardJarScanner jarScanner = (StandardJarScanner) ctx.getJarScanner();
        jarScanner.setJarScanFilter(filter);
        // jarScanner.setScanManifest(false);

        // disables Tomcat's reflective reference clearing to avoid reflective access warnings on Java 9 and later JVMs.
        ctx.setClearReferencesObjectStreamClassCaches(false);
        ctx.setClearReferencesRmiTargets(false);
        ctx.setClearReferencesThreadLocals(false);

        if (errorPages != null && !errorPages.isEmpty()) {
            for (ErrorPage errorPage : errorPages) {
                ctx.addErrorPage(errorPage);
            }
        }

        ctx.setParentClassLoader(classLoader);
        try {
            String path;
            var resource = classLoader.getResource("/");
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
            resources.setCachingAllowed(configuration.isCachingAllowed());
            resources.setCacheMaxSize(configuration.getCacheMaxSize());
            resources.addPreResources(new DirResourceSet(resources, "/WEB-INF/classes", path, "/"));
            ctx.setResources(resources);
        } catch (Exception e) {
            LOGGER.error("get resources and set base dir of tomcat error\n", e);
        }

        server.setBaseDir(webappDir);
        LOGGER.info("webapp: " + webappDir);
        LOGGER.debug("tomcat config uri: http://" + configuration.getHost() + ":" + configuration.getPort());
    }

    @Override
    public DebbieApplication factory(DebbieConfigurationFactory factory, BeanFactoryHandler beanFactoryHandler,
                                     ClassLoader classLoader) {
        TomcatConfiguration configuration = factory.factory(TomcatConfiguration.class, beanFactoryHandler);
        List<ErrorPage> errorPages = beanFactoryHandler.getBeanList(ErrorPage.class);
        config(configuration, classLoader, errorPages);
        return tomcatApplication(configuration, beanFactoryHandler);
    }

    private DebbieApplication tomcatApplication(TomcatConfiguration configuration, BeanFactoryHandler beanFactoryHandler) {
        return new AbstractDebbieApplication(LOGGER, beanFactoryHandler) {
            @Override
            public void start(long beforeStartTime, String... args) {
                try {
                    server.init();
                    server.getConnector();
                    server.start();
                    printlnWebUrl(LOGGER, configuration.getPort());
                    LOGGER.info("application start time spends " + (System.currentTimeMillis() - beforeStartTime) + "ms");
                    Runtime.getRuntime().addShutdownHook(new Thread(() -> exit(args)));
                } catch (LifecycleException e) {
                    Throwable cause = e.getCause();
                    if (cause == null) {
                        cause = e;
                    }
                    LOGGER.error("tomcat start error", cause);
                }
            }

            @Override
            public void exit(long beforeStartTime, String... args) {
                try {
                    server.stop();
                    server.destroy();
                } catch (LifecycleException e) {
                    LOGGER.error("tomcat stop error", e);
                } finally {
                    if (LOGGER.isTraceEnabled()) {
                        LOGGER.trace("application running time spends " + (System.currentTimeMillis() - beforeStartTime) + "ms");
                    }
                }
            }
        };
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(TomcatServerApplicationFactory.class);
}
