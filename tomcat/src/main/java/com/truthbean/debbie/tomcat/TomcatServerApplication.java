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

import com.truthbean.debbie.bean.GlobalBeanFactory;
import com.truthbean.debbie.boot.DebbieApplication;
import com.truthbean.debbie.core.ApplicationContext;
import com.truthbean.debbie.io.PathUtils;
import com.truthbean.debbie.properties.DebbieConfigurationCenter;
import com.truthbean.debbie.server.AbstractWebServerApplication;
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
import com.truthbean.Logger;
import com.truthbean.logger.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.time.Instant;
import java.util.List;

/**
 * @author TruthBean
 * @since 0.0.1
 * Created on 2019/3/10 13:44.
 */
public class TomcatServerApplication extends AbstractWebServerApplication {

    @Override
    public boolean isWeb() {
        return true;
    }

    static {
        System.setProperty("org.apache.catalina.startup.EXIT_ON_INIT_FAILURE", "true");
    }

    private final Tomcat server = new Tomcat();
    private TomcatConfiguration configuration;

    private String configWebappDir() {
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

    private void customizeConnector(Connector connector) {
        int port = Math.max(configuration.getPort(), 0);
        connector.setPort(port);
        if (StringUtils.hasText(configuration.getServerHeader())) {
            connector.setProperty("server", configuration.getServerHeader());
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
        // Don't bind to the socket prematurely if DebbieApplicationContext is slow to start
        connector.setProperty("bindOnInit", "false");
        // TODO ssl
    }

    private void config(ClassLoader classLoader, List<ErrorPage> errorPages) {
        if (this.configuration.isDisableMBeanRegistry()) {
            Registry.disableRegistry();
        }
        server.setPort(this.configuration.getPort());
        server.setHostname(this.configuration.getHost());

        Connector connector = new Connector(this.configuration.getConnectorProtocol());
        connector.setThrowOnFailure(true);
        server.getService().addConnector(connector);
        customizeConnector(connector);
        server.setConnector(connector);
        server.getHost().setAutoDeploy(this.configuration.isAutoDeploy());

        try {
            Path tempPath = Files.createTempDirectory("tomcat-base-dir");
            server.setBaseDir(tempPath.toString());
        } catch (IOException e) {
            LOGGER.error("create tomcat-base-dir in temp directory error", e);
        }

        String webappDir = configWebappDir();
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
            LOGGER.debug(() -> "resource: " + resource);
            if (resource == null) {
                path = new File("").getAbsolutePath();
            } else {
                path = Path.of(resource.toURI()).toString();
            }
            LOGGER.debug(() -> "configuring app with basedir: " + path);

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
        LOGGER.info(() -> "webapp: " + webappDir);
        LOGGER.debug(() -> "tomcat config uri: http://" + configuration.getHost() + ":" + configuration.getPort());
    }

    @Override
    public DebbieApplication init(DebbieConfigurationCenter factory, ApplicationContext applicationContext,
                                     ClassLoader classLoader) {
        this.configuration = factory.factory(TomcatConfiguration.class, applicationContext);

        GlobalBeanFactory globalBeanFactory = applicationContext.getGlobalBeanFactory();
        List<ErrorPage> errorPages = globalBeanFactory.getBeanList(ErrorPage.class);
        config(classLoader, errorPages);

        super.setLogger(LOGGER);

        return this;
    }

    @Override
    public void start(Instant beforeStartTime, String... args) {
        try {
            server.init();
            server.getConnector();
            server.start();
            printlnWebUrl(LOGGER, configuration.getPort());
            super.printStartTime();
            postBeforeStart();
        } catch (LifecycleException e) {
            Throwable cause = e.getCause();
            if (cause == null) {
                cause = e;
            }
            LOGGER.error("tomcat start error", cause);
        }
    }

    @Override
    public void exit(Instant beforeStartTime, String... args) {
        try {
            server.stop();
            server.destroy();
        } catch (LifecycleException e) {
            LOGGER.error("tomcat stop error", e);
        } finally {
            super.printExitTime();
        }
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(TomcatServerApplication.class);
}
