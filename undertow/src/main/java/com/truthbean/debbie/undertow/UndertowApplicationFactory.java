package com.truthbean.debbie.undertow;

import com.truthbean.debbie.boot.AbstractApplicationFactory;
import com.truthbean.debbie.boot.DebbieApplication;
import com.truthbean.debbie.core.bean.BeanInitializationHandler;
import com.truthbean.debbie.core.net.NetWorkUtils;
import com.truthbean.debbie.mvc.request.filter.RouterFilterInfo;
import com.truthbean.debbie.mvc.request.filter.RouterFilterManager;
import com.truthbean.debbie.mvc.router.MvcRouterRegister;
import com.truthbean.debbie.undertow.handler.DispatcherHttpHandler;
import com.truthbean.debbie.undertow.handler.HttpHandlerFilter;
import io.undertow.Undertow;
import io.undertow.server.HttpHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * @author TruthBean
 * @since 0.0.1
 */
public final class UndertowApplicationFactory extends AbstractApplicationFactory<UndertowConfiguration> {

    private Undertow server;

    @Override
    public boolean isWeb() {
        return true;
    }

    @Override
    public DebbieApplication factory(UndertowConfiguration configuration) {
        BeanInitializationHandler.init(configuration.getTargetClasses());
        MvcRouterRegister.registerRouter(configuration);
        RouterFilterManager.registerFilter(configuration);

        // reverse order to fix the chain order
        List<RouterFilterInfo> filters = RouterFilterManager.getReverseOrderFilters();
        HttpHandler next = new DispatcherHttpHandler(configuration);
        for (RouterFilterInfo filter : filters) {
            next = new HttpHandlerFilter(next, filter);
        }

        // Undertow builder
        server = Undertow.builder()
                // Listener binding
                .addHttpListener(configuration.getPort(), configuration.getHost())
                // Default Handler
                .setHandler(next).build();

        return new DebbieApplication() {
            @Override
            public void start(String... args) {
                server.start();
                LOGGER.debug("application start with http://" + NetWorkUtils.getLocalHost() + ":" + configuration.getPort());
            }

            @Override
            public void exit(String... args) {
                server.stop();
            }
        };
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(UndertowApplicationFactory.class);

}
