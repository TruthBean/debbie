package com.truthbean.debbie.aio;

import com.truthbean.debbie.bean.BeanFactoryHandler;
import com.truthbean.debbie.boot.DebbieApplication;
import com.truthbean.debbie.mvc.filter.RouterFilterManager;
import com.truthbean.debbie.mvc.router.MvcRouterRegister;
import com.truthbean.debbie.properties.DebbieConfigurationFactory;
import com.truthbean.debbie.server.AbstractWebServerApplicationFactory;
import com.truthbean.debbie.server.session.SessionManager;
import com.truthbean.debbie.server.session.SimpleSessionManager;
import com.truthbean.debbie.task.DebbieTaskStarter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.channels.AsynchronousChannelGroup;
import java.nio.channels.AsynchronousServerSocketChannel;

import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author TruthBean/Rogar·Q
 * @since 0.0.2
 * Created on 2019-12-17 19:55
 */
public class AioServerApplicationFactory extends AbstractWebServerApplicationFactory {

    @Override
    public DebbieApplication factory(DebbieConfigurationFactory factory, BeanFactoryHandler beanFactoryHandler) {
        final AioServerConfiguration configuration = factory.factory(AioServerConfiguration.class, beanFactoryHandler);
        var beanInitialization = beanFactoryHandler.getBeanInitialization();
        MvcRouterRegister.registerRouter(configuration, beanFactoryHandler);
        RouterFilterManager.registerFilter(configuration, beanInitialization);
        final SessionManager sessionManager = new SimpleSessionManager();
        try {
            return new AioServerApplication(beanFactoryHandler, configuration, sessionManager);
        } catch (IOException e) {
            LOGGER.error("create aio server error", e);
            return null;
        }
    }

    private class AioServerApplication extends DebbieApplication implements Runnable {

        private final AsynchronousServerSocketChannel server;

        private final BeanFactoryHandler beanFactoryHandler;
        private final AioServerConfiguration configuration;

        private final SessionManager sessionManager;

        AioServerApplication(BeanFactoryHandler beanFactoryHandler, AioServerConfiguration configuration,
                             final SessionManager sessionManager) throws IOException {
            int port = configuration.getPort();
            this.beanFactoryHandler = beanFactoryHandler;
            this.configuration = configuration;

            this.sessionManager = sessionManager;

            // 创建线程池
            var threadFactory = Executors.defaultThreadFactory();
            var executor = new ThreadPoolExecutor(5, 200,
                    0L, TimeUnit.MICROSECONDS, new LinkedBlockingDeque<>(1024), threadFactory,
                    new ThreadPoolExecutor.AbortPolicy());
            // 异步通道管理器
            var asyncChannelGroup = AsynchronousChannelGroup.withThreadPool(executor);
            // 创建 用在服务端的异步Socket.以下简称服务器socket。
            // 异步通道管理器，会把服务端所用到的相关参数
            SocketAddress socketAddress = new InetSocketAddress(port);
            server = AsynchronousServerSocketChannel.open(asyncChannelGroup).bind(socketAddress);
        }

        private Thread thread = Thread.currentThread();
        private final Thread watcherThread = new Thread(() -> {
            LOGGER.info("type 'ENTER' to finished server");
            try {
                System.in.read();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }, "Aio-Server-Watcher");

        @Override
        protected void start(long beforeStartTime, String... args) {
            thread = new Thread(this);
            var taskStarter = new DebbieTaskStarter();
            taskStarter.start(beanFactoryHandler);
            LOGGER.debug("aio server config uri: http://" + configuration.getHost() + ":" + configuration.getPort());
            beforeStart(LOGGER, beanFactoryHandler);
            printlnWebUrl(LOGGER, configuration.getPort());
            LOGGER.info("application start time spends " + (System.currentTimeMillis() - beforeStartTime) + "ms");
            thread.start();
        }

        @Override
        public void exit(String... args) {
            beforeExit(beanFactoryHandler, args);
            thread.interrupt();
            watcherThread.interrupt();
        }

        @Override
        public void run() {
            try {
                // 为服务端socket指定接收操作对象.accept原型是：
                // accept(A attachment, CompletionHandler<AsynchronousSocketChannel, ? super A> handler)
                // 也就是这里的CompletionHandler的A型参数是实际调用accept方法的第一个参数
                // 即是listener。另一个参数V，就是原型中的客户端socket
                var mvcCompletionHandler = new ServerCompletionHandler(configuration, sessionManager, beanFactoryHandler, server);
                server.accept(server, mvcCompletionHandler);

                Runtime.getRuntime().addShutdownHook(new Thread(this::stop));
                watcherThread.start();
            } catch (Exception e) {
                LOGGER.error("", e);
            }
        }

        private void stop() {
            thread.interrupt();
            watcherThread.interrupt();
        }
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(AioServerApplication.class);
}
