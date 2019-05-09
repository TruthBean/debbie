package com.truthbean.debbie.netty;

import com.truthbean.debbie.boot.AbstractApplicationFactory;
import com.truthbean.debbie.boot.DebbieApplication;
import com.truthbean.debbie.core.bean.BeanInitialization;
import com.truthbean.debbie.core.net.NetWorkUtils;
import com.truthbean.debbie.mvc.request.filter.RouterFilterManager;
import com.truthbean.debbie.mvc.router.MvcRouterRegister;
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
        new BeanInitialization().init(configuration.getTargetClasses());
        MvcRouterRegister.registerRouter(configuration);
        RouterFilterManager.registerFilter(configuration);
        return new DebbieApplication() {
            @Override
            public void start(String... args) {
                run(configuration);
                LOGGER.debug("application start with http://" + NetWorkUtils.getLocalHost() + ":" + configuration.getPort());
            }

            @Override
            public void exit(String... args) {
                stop();
            }
        };
    }

    private void run(NettyConfiguration configuration) {
        // (1)
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            // (2)
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup)
                    // (3)
                    .channel(NioServerSocketChannel.class)
                    // (4))
                    .childHandler(new HttpChannelInitializer(configuration))
                    // (5)
                    .option(ChannelOption.SO_BACKLOG, 128)
                    // (6)
                    .childOption(ChannelOption.SO_KEEPALIVE, true);

            // (7) Bind and start to accept incoming connections.
            ChannelFuture f = b.bind(configuration.getPort()).sync();

            // Wait until the server socket is closed.
            // In this example, this does not happen, but you can do that to gracefully
            // shut down your server.
            f.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }
    }

    private void stop() {
        // todo
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(NettyApplicationFactory.class);

}
