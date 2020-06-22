/**
 * Copyright (c) 2020 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *         http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.undertow;

import com.truthbean.debbie.bean.BeanFactoryHandler;
import com.truthbean.debbie.bean.BeanInitialization;
import com.truthbean.debbie.boot.AbstractDebbieApplication;
import com.truthbean.debbie.boot.DebbieApplication;
import com.truthbean.debbie.mvc.filter.RouterFilterInfo;
import com.truthbean.debbie.mvc.filter.RouterFilterManager;
import com.truthbean.debbie.mvc.router.MvcRouterRegister;
import com.truthbean.debbie.properties.DebbieConfigurationFactory;
import com.truthbean.debbie.server.AbstractWebServerApplicationFactory;
import com.truthbean.debbie.undertow.handler.DispatcherHttpHandler;
import com.truthbean.debbie.undertow.handler.HttpHandlerFilter;
import io.undertow.Undertow;
import io.undertow.server.HttpHandler;
import io.undertow.server.session.InMemorySessionManager;
import io.undertow.server.session.SessionAttachmentHandler;
import io.undertow.server.session.SessionCookieConfig;
import io.undertow.server.session.SessionManager;
import com.truthbean.Logger;
import com.truthbean.logger.LoggerFactory;

import java.lang.management.ManagementFactory;
import java.util.List;

/**
 * @author TruthBean
 * @since 0.0.1
 */
public final class UndertowServerApplicationFactory extends AbstractWebServerApplicationFactory {

    private Undertow server;

    @Override
    public boolean isWeb() {
        return true;
    }

    @Override
    public DebbieApplication factory(DebbieConfigurationFactory factory, BeanFactoryHandler beanFactoryHandler,
                                     ClassLoader classLoader) {
        UndertowConfiguration configuration = factory.factory(UndertowConfiguration.class, beanFactoryHandler);
        BeanInitialization beanInitialization = beanFactoryHandler.getBeanInitialization();
        MvcRouterRegister.registerRouter(configuration, beanFactoryHandler);
        RouterFilterManager.registerFilter(configuration, beanInitialization);
        RouterFilterManager.registerCharacterEncodingFilter(configuration, "/**");
        RouterFilterManager.registerCorsFilter(configuration, "/**");
        RouterFilterManager.registerCsrfFilter(configuration, "/**");
        RouterFilterManager.registerSecurityFilter(configuration, "/**");

        SessionManager sessionManager = new InMemorySessionManager(configuration.getName());
        SessionCookieConfig sessionConfig = new SessionCookieConfig();
        /*
         * Use the sessionAttachmentHandler to add the sessionManager and
         * sessionCofing to the exchange of every request
         */
        SessionAttachmentHandler sessionAttachmentHandler = new SessionAttachmentHandler(sessionManager, sessionConfig);

        // reverse order to fix the chain order
        List<RouterFilterInfo> filters = RouterFilterManager.getReverseOrderFilters();
        HttpHandler next = new DispatcherHttpHandler(configuration, beanFactoryHandler);
        for (RouterFilterInfo filter : filters) {
            next = new HttpHandlerFilter(next, filter, beanFactoryHandler, configuration);
        }

        // set as next handler your root handler
        sessionAttachmentHandler.setNext(next);

        // Undertow builder
        server = Undertow.builder()
                // Listener binding
                .addHttpListener(configuration.getPort(), configuration.getHost())
                // Default Handler
                .setHandler(sessionAttachmentHandler).build();

        return new AbstractDebbieApplication(LOGGER, beanFactoryHandler) {
            @Override
            public void start(long beforeStartTime, String... args) {
                server.start();
                printlnWebUrl(LOGGER, configuration.getPort());
                double uptime = ManagementFactory.getRuntimeMXBean().getUptime();
                LOGGER.info(() -> "application start time spends " + (System.currentTimeMillis() - beforeStartTime) +
                        "ms ( JVM running for "  + uptime + "ms )");
                postBeforeStart();
            }

            @Override
            public void exit(long beforeStartTime, String... args) {
                server.stop();
                if (LOGGER.isTraceEnabled()) {
                    LOGGER.trace("application running time spends " + (System.currentTimeMillis() - beforeStartTime) + "ms");
                }
            }
        };
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(UndertowServerApplicationFactory.class);

}
