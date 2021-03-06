/**
 * Copyright (c) 2021 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 * http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.aio;

import com.truthbean.debbie.boot.ApplicationArgs;
import com.truthbean.debbie.boot.DebbieApplication;
import com.truthbean.debbie.concurrent.NamedThreadFactory;
import com.truthbean.debbie.concurrent.PooledExecutor;
import com.truthbean.debbie.concurrent.ThreadLoggerUncaughtExceptionHandler;
import com.truthbean.debbie.concurrent.ThreadPooledExecutor;
import com.truthbean.debbie.core.ApplicationContext;
import com.truthbean.debbie.mvc.filter.RouterFilterManager;
import com.truthbean.debbie.mvc.router.MvcRouterRegister;
import com.truthbean.debbie.properties.DebbieConfigurationCenter;
import com.truthbean.debbie.server.AbstractWebServerApplication;
import com.truthbean.debbie.server.session.SessionManager;
import com.truthbean.debbie.server.session.SimpleSessionManager;

import com.truthbean.Logger;
import com.truthbean.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.channels.AsynchronousChannelGroup;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.time.Instant;
import java.util.concurrent.*;

/**
 * @author TruthBean/Rogar·Q
 * @since 0.0.2
 * Created on 2019-12-17 19:55
 */
public class AioServerApplication extends AbstractWebServerApplication implements Runnable {

    private AsynchronousServerSocketChannel server;

    private ApplicationContext applicationContext;
    private AioServerConfiguration configuration;

    private SessionManager sessionManager;

    @Override
    public DebbieApplication init(DebbieConfigurationCenter factory, ApplicationContext applicationContext,
                                  ClassLoader classLoader) {
        final AioServerConfiguration configuration = factory.factory(AioServerConfiguration.class, applicationContext);
        if (configuration == null) {
            LOGGER.info("com.truthbean.debbie.aio.AioServerApplication is not enable.");
            return null;
        }
        var beanInitialization = applicationContext.getBeanInitialization();
        MvcRouterRegister.registerRouter(configuration, applicationContext);
        RouterFilterManager.registerFilter(configuration, beanInitialization);
        RouterFilterManager.registerCharacterEncodingFilter(configuration, "/**");
        RouterFilterManager.registerCorsFilter(configuration, "/**");
        RouterFilterManager.registerCsrfFilter(configuration, "/**");
        RouterFilterManager.registerSecurityFilter(configuration, "/**");
        final SessionManager sessionManager = new SimpleSessionManager();
        try {
            doInit(applicationContext, configuration, sessionManager);
        } catch (IOException e) {
            LOGGER.error("create aio server error", e);
        }
        super.setLogger(LOGGER);
        return this;
    }

    private void doInit(ApplicationContext applicationContext, AioServerConfiguration configuration,
                        final SessionManager sessionManager) throws IOException {
        int port = configuration.getPort();
        this.applicationContext = applicationContext;
        this.configuration = configuration;

        this.sessionManager = sessionManager;

        // 创建线程池
        var threadFactory = NamedThreadFactory.defaultThreadFactory();
        int core = Runtime.getRuntime().availableProcessors();
        var executor = new ThreadPoolExecutor(core, core * 10,
                0L, TimeUnit.MICROSECONDS, new LinkedBlockingDeque<>(1024), threadFactory,
                new ThreadPoolExecutor.AbortPolicy());
        // 用于资源共享的异步通道管理器
        var asyncChannelGroup = AsynchronousChannelGroup.withThreadPool(executor);
        // 创建 用在服务端的异步Socket.以下简称服务器socket。
        // 异步通道管理器，会把服务端所用到的相关参数
        SocketAddress socketAddress = new InetSocketAddress(port);
        server = AsynchronousServerSocketChannel.open(asyncChannelGroup).bind(socketAddress);
    }

    private final ThreadFactory namedThreadFactory = new NamedThreadFactory("aio-server-application-")
            .setUncaughtExceptionHandler(new ThreadLoggerUncaughtExceptionHandler());
    private final PooledExecutor singleThreadPool = new ThreadPooledExecutor(1, 1, namedThreadFactory);

    @Override
    protected void start(Instant beforeStartTime, ApplicationArgs args) {
        LOGGER.debug(() -> "aio server config uri: http://" + configuration.getHost() + ":" + configuration.getPort());
        printlnWebUrl(LOGGER, configuration.getPort());
        printStartTime();
        postBeforeStart();
        singleThreadPool.execute(this);
    }

    @Override
    protected void exit(Instant beforeStartTime, ApplicationArgs args) {
        LOGGER.debug(() -> "destroy running thread");
        printExitTime();
        singleThreadPool.destroy();
    }

    @Override
    public void run() {
        try {
            LOGGER.debug(() -> "running .... ");
            // 为服务端socket指定接收操作对象.accept原型是：
            // accept(A attachment, CompletionHandler<AsynchronousSocketChannel, ? super A> handler)
            // 也就是这里的CompletionHandler的A型参数是实际调用accept方法的第一个参数
            // 即是listener。另一个参数V，就是原型中的客户端socket
            var mvcCompletionHandler = new ServerCompletionHandler(configuration, sessionManager, applicationContext);
            server.accept(server, mvcCompletionHandler);
        } catch (Exception e) {
            LOGGER.error("", e);
        }
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(AioServerApplication.class);
}
