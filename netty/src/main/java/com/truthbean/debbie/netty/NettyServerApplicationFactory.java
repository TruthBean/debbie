package com.truthbean.debbie.netty;

import com.truthbean.debbie.bean.BeanFactoryHandler;
import com.truthbean.debbie.bean.BeanInitialization;
import com.truthbean.debbie.boot.AbstractDebbieApplication;
import com.truthbean.debbie.boot.DebbieApplication;
import com.truthbean.debbie.mvc.filter.RouterFilterManager;
import com.truthbean.debbie.mvc.router.MvcRouterRegister;
import com.truthbean.debbie.properties.DebbieConfigurationFactory;
import com.truthbean.debbie.server.AbstractWebServerApplicationFactory;
import com.truthbean.debbie.server.session.SessionManager;
import com.truthbean.debbie.server.session.SimpleSessionManager;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.management.ManagementFactory;

/**
 * @author TruthBean
 * @since 0.0.1
 * Created on 2018-03-08 14:59
 */
public class NettyServerApplicationFactory extends AbstractWebServerApplicationFactory {

    static {
        System.setProperty("io.netty.tryReflectionSetAccessible", "true");
    }

    @Override
    public boolean isWeb() {
        return true;
    }

    @Override
    public DebbieApplication factory(DebbieConfigurationFactory factory, BeanFactoryHandler beanFactoryHandler,
                                     ClassLoader classLoader) {
        NettyConfiguration configuration = factory.factory(NettyConfiguration.class, beanFactoryHandler);
        BeanInitialization beanInitialization = beanFactoryHandler.getBeanInitialization();
        MvcRouterRegister.registerRouter(configuration, beanFactoryHandler);
        RouterFilterManager.registerFilter(configuration, beanInitialization);
        RouterFilterManager.registerCharacterEncodingFilter(configuration, "/**");
        RouterFilterManager.registerCorsFilter(configuration, "/**");
        RouterFilterManager.registerCsrfFilter(configuration, "/**");
        RouterFilterManager.registerSecurityFilter(configuration, "/**");
        final SessionManager sessionManager = new SimpleSessionManager();
        return new NettyDebbieApplication(configuration, sessionManager, beanFactoryHandler, LOGGER);
    }

    // (1)
    private final EventLoopGroup bossGroup = new NioEventLoopGroup();
    private final EventLoopGroup workerGroup = new NioEventLoopGroup();
    private ChannelFuture channelFuture = null;

    private void run(NettyConfiguration configuration, SessionManager sessionManager,
                     BeanFactoryHandler beanFactoryHandler,
                     long beforeStartTime, NettyDebbieApplication debbieApplication) {
        try {
            // (2)
            ServerBootstrap b = new ServerBootstrap();
            HttpChannelInitializer httpChannelInitializer = new HttpChannelInitializer(configuration, sessionManager, beanFactoryHandler);
            b.group(bossGroup, workerGroup)
                // (3)
                .channel(NioServerSocketChannel.class)
                // (4))
                .childHandler(httpChannelInitializer)
                // (5)
                .option(ChannelOption.SO_BACKLOG, 1024)
                // (6)
                .childOption(ChannelOption.SO_KEEPALIVE, true);

            // (7) Bind and start to accept incoming connections.
            ChannelFuture channelFuture = b.bind(configuration.getPort()).sync();
            LOGGER.debug("netty config uri: http://" + configuration.getHost() + ":" + configuration.getPort());
            printlnWebUrl(LOGGER, configuration.getPort());
            double uptime = ManagementFactory.getRuntimeMXBean().getUptime();
            LOGGER.info("application start time spends " + (System.currentTimeMillis() - beforeStartTime) + "ms ( JVM running for "  + uptime + "ms )");
            debbieApplication.postBeforeStart();

            // Wait until the server socket is closed.
            // In this example, this does not happen, but you can do that to gracefully
            // shut down your server.
            this.channelFuture = channelFuture.channel().closeFuture();
            // this.channelFuture.sync();
        } catch (InterruptedException e) {
            LOGGER.error("netty server start error. ", e);
            stop();
        } /*finally {
            stop();
        }*/
    }

    private void stop() {
        LOGGER.debug("stop netty server...");
        try {
            workerGroup.shutdownGracefully().sync();
            bossGroup.shutdownGracefully().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void shutdown() {
        LOGGER.debug("shutdown netty server...");
        try {
            if (channelFuture != null) {
                channelFuture.cancel(true);
            }
            workerGroup.terminationFuture().getNow();
            workerGroup.shutdownGracefully().sync();
            bossGroup.terminationFuture().getNow();
            bossGroup.shutdownGracefully().sync();
        } catch (InterruptedException e) {
            LOGGER.error("shutdown netty server error. ", e);
        }
    }

    private class NettyDebbieApplication extends AbstractDebbieApplication {

        private final NettyConfiguration configuration;
        private final SessionManager sessionManager;
        private final BeanFactoryHandler beanFactoryHandler;

        NettyDebbieApplication(NettyConfiguration configuration, SessionManager sessionManage,
                               BeanFactoryHandler beanFactoryHandler, Logger logger) {
            super(logger, beanFactoryHandler);
            this.configuration = configuration;
            this.sessionManager = sessionManage;
            this.beanFactoryHandler = beanFactoryHandler;
        }

        @Override
        protected void postBeforeStart() {
            super.postBeforeStart();
        }

        @Override
        public void start(long beforeStartTime, String... args) {
            run(configuration, sessionManager, beanFactoryHandler, beforeStartTime, this);
        }

        @Override
        public void exit(long beforeStartTime, String... args) {
            shutdown();
            if (LOGGER.isTraceEnabled()) {
                LOGGER.trace("application running time spends " + (System.currentTimeMillis() - beforeStartTime) + "ms");
            }
        }
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(NettyServerApplicationFactory.class);

}
