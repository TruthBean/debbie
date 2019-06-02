package com.truthbean.debbie.netty;

import com.truthbean.debbie.boot.AbstractApplicationFactory;
import com.truthbean.debbie.boot.DebbieApplication;
import com.truthbean.debbie.core.bean.BeanFactoryHandler;
import com.truthbean.debbie.core.bean.BeanInitialization;
import com.truthbean.debbie.core.net.NetWorkUtils;
import com.truthbean.debbie.mvc.request.filter.RouterFilterManager;
import com.truthbean.debbie.mvc.router.MvcRouterRegister;
import com.truthbean.debbie.netty.session.SessionManager;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author TruthBean
 * @since 0.0.1
 * Created on 2018-03-08 14:59
 */
public class NettyApplicationFactory extends AbstractApplicationFactory<NettyConfiguration> {
    @Override
    public boolean isWeb() {
        return true;
    }

    @Override
    public DebbieApplication factory(NettyConfiguration configuration) {
        BeanFactoryHandler handler = new BeanFactoryHandler();

        MvcRouterRegister.registerRouter(configuration);
        RouterFilterManager.registerFilter(configuration);
        final SessionManager sessionManager = new SessionManager();
        return new DebbieApplication() {
            @Override
            public void start(String... args) {
                run(configuration, sessionManager, handler);
                LOGGER.debug("application start with http://" + NetWorkUtils.getLocalHost() + ":" + configuration.getPort());
            }

            @Override
            public void exit(String... args) {
                stop();
            }
        };
    }

    // (1)
    private EventLoopGroup bossGroup = new NioEventLoopGroup();
    private EventLoopGroup workerGroup = new NioEventLoopGroup();

    private void run(NettyConfiguration configuration, SessionManager sessionManager, BeanFactoryHandler beanFactoryHandler) {

        try {
            // (2)
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup)
                    // (3)
                    .channel(NioServerSocketChannel.class)
                    // (4))
                    .childHandler(new HttpChannelInitializer(configuration, sessionManager, beanFactoryHandler))
                    // (5)
                    .option(ChannelOption.SO_BACKLOG, 1024)
                    // (6)
                    .childOption(ChannelOption.SO_KEEPALIVE, true);

            // (7) Bind and start to accept incoming connections.
            ChannelFuture f = b.bind(configuration.getPort()).sync();
            LOGGER.debug("netty config uri: http://" + configuration.getHost() + ":" + configuration.getPort());
            LOGGER.info("application start with http://" + NetWorkUtils.getLocalHost() + ":" + configuration.getPort());

            // Wait until the server socket is closed.
            // In this example, this does not happen, but you can do that to gracefully
            // shut down your server.
            f.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            LOGGER.error("netty server start error. ", e);
        } finally {
            stop();
        }
    }

    private void stop() {
        workerGroup.shutdownGracefully();
        bossGroup.shutdownGracefully();
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(NettyApplicationFactory.class);

}
