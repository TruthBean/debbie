package com.truthbean.debbie.undertow;

import com.truthbean.debbie.boot.AbstractApplicationFactory;
import com.truthbean.debbie.core.bean.BeanInitializationHandler;
import com.truthbean.debbie.mvc.router.MvcRouterRegister;
import com.truthbean.debbie.undertow.handler.DispatcherHttpHandler;
import io.undertow.Undertow;

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
    public void factory(UndertowConfiguration configuration) {
        BeanInitializationHandler.init(configuration.getTargetClasses());
        MvcRouterRegister.registerRouter(configuration);
        // Undertow builder
        server = Undertow.builder()
                // Listener binding
                .addHttpListener(configuration.getPort(), configuration.getHost())
                // Default Handler
                .setHandler(new DispatcherHttpHandler(configuration)).build();
    }

    @Override
    public void run(String... args) {
        this.server.start();
    }

    @Override
    public void exit(String... args) {
        this.server.stop();
    }

}