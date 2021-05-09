package com.truthbean.debbie.netty;

import com.truthbean.debbie.bean.BeanInitialization;
import com.truthbean.debbie.boot.AbstractApplication;
import com.truthbean.debbie.boot.ApplicationArgs;
import com.truthbean.debbie.boot.DebbieApplication;
import com.truthbean.debbie.core.ApplicationContext;
import com.truthbean.debbie.mvc.filter.RouterFilterManager;
import com.truthbean.debbie.mvc.router.MvcRouterRegister;
import com.truthbean.debbie.properties.DebbieConfigurationCenter;
import com.truthbean.debbie.server.AbstractWebServerApplication;
import com.truthbean.debbie.server.session.SessionManager;
import com.truthbean.debbie.server.session.SimpleSessionManager;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import com.truthbean.Logger;
import com.truthbean.LoggerFactory;

import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.time.Duration;
import java.time.Instant;

/**
 * @author TruthBean
 * @since 0.0.1
 * Created on 2018-03-08 14:59
 */
public class NettyServerApplication extends AbstractWebServerApplication {

    static {
        // --add-opens java.base/jdk.internal.misc=ALL-UNNAMED
        // -Dio.netty.tryReflectionSetAccessible=true
        // --illegal-access=warn
        System.setProperty("io.netty.tryReflectionSetAccessible", "true");
    }

    private NettyConfiguration configuration;
    private SessionManager sessionManager;
    private ApplicationContext applicationContext;

    @Override
    public boolean isWeb() {
        return true;
    }

    @Override
    public DebbieApplication init(DebbieConfigurationCenter factory, ApplicationContext applicationContext,
                                     ClassLoader classLoader) {
        NettyConfiguration configuration = factory.factory(NettyConfiguration.class, applicationContext);
        BeanInitialization beanInitialization = applicationContext.getBeanInitialization();
        MvcRouterRegister.registerRouter(configuration, applicationContext);
        RouterFilterManager.registerFilter(configuration, beanInitialization);
        RouterFilterManager.registerCharacterEncodingFilter(configuration, "/**");
        RouterFilterManager.registerCorsFilter(configuration, "/**");
        RouterFilterManager.registerCsrfFilter(configuration, "/**");
        RouterFilterManager.registerSecurityFilter(configuration, "/**");
        final SessionManager sessionManager = new SimpleSessionManager();

        this.configuration = configuration;
        this.sessionManager = sessionManager;
        this.applicationContext = applicationContext;
        super.setLogger(LOGGER);

        return this;
    }

    // (1)
    private final EventLoopGroup bossGroup = new NioEventLoopGroup();
    private final EventLoopGroup workerGroup = new NioEventLoopGroup();
    private ChannelFuture channelFuture = null;

    private void run(final NettyConfiguration configuration, final SessionManager sessionManager,
                     final ApplicationContext applicationContext,
                     Instant beforeStartTime) {
        try {
            // (2)
            ServerBootstrap b = new ServerBootstrap();
            HttpChannelInitializer httpChannelInitializer = new HttpChannelInitializer(configuration, sessionManager, applicationContext);
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
            LOGGER.debug(() -> "netty config uri: http://" + configuration.getHost() + ":" + configuration.getPort());
            printlnWebUrl(LOGGER, configuration.getPort());
            super.printStartTime();
            super.postBeforeStart();

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
        LOGGER.debug(() -> "stop netty server...");
        try {
            workerGroup.shutdownGracefully().sync();
            bossGroup.shutdownGracefully().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void shutdown() {
        LOGGER.debug(() -> "shutdown netty server...");
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

    @Override
    protected void postBeforeStart() {
        super.postBeforeStart();
    }

    @Override
    protected void start(Instant beforeStartTime, ApplicationArgs args) {
        run(configuration, sessionManager, applicationContext, beforeStartTime);
    }

    @Override
    protected void exit(Instant beforeStartTime, ApplicationArgs args) {
        shutdown();
        super.printExitTime();
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(NettyServerApplication.class);

}
