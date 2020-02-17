package com.truthbean.debbie.undertow;

import com.truthbean.debbie.bean.BeanFactoryHandler;
import com.truthbean.debbie.bean.BeanInitialization;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
    public DebbieApplication factory(DebbieConfigurationFactory factory, BeanFactoryHandler beanFactoryHandler) {
        UndertowConfiguration configuration = factory.factory(UndertowConfiguration.class, beanFactoryHandler);
        BeanInitialization beanInitialization = beanFactoryHandler.getBeanInitialization();
        MvcRouterRegister.registerRouter(configuration, beanFactoryHandler);
        RouterFilterManager.registerFilter(configuration, beanInitialization);

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
            next = new HttpHandlerFilter(next, filter, beanFactoryHandler);
        }

        // set as next handler your root handler
        sessionAttachmentHandler.setNext(next);

        // Undertow builder
        server = Undertow.builder()
                // Listener binding
                .addHttpListener(configuration.getPort(), configuration.getHost())
                // Default Handler
                .setHandler(sessionAttachmentHandler).build();

        return new DebbieApplication() {
            @Override
            public void start(long beforeStartTime, String... args) {
                server.start();
                this.beforeStart(LOGGER, beanFactoryHandler);
                printlnWebUrl(LOGGER, configuration.getPort());
                LOGGER.info("application start time spends " + (System.currentTimeMillis() - beforeStartTime) + "ms");
                Runtime.getRuntime().addShutdownHook(new Thread(() -> exit(args)));
            }

            @Override
            public void exit(String... args) {
                beforeExit(beanFactoryHandler, args);
                server.stop();
            }
        };
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(UndertowServerApplicationFactory.class);

}
