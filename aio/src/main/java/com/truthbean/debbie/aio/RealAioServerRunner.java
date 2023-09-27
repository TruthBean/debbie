package com.truthbean.debbie.aio;

import com.truthbean.Logger;
import com.truthbean.LoggerFactory;
import com.truthbean.core.concurrent.NamedThreadFactory;
import com.truthbean.core.concurrent.ThreadLoggerUncaughtExceptionHandler;
import com.truthbean.core.util.ReflectionUtils;
import com.truthbean.core.util.StringUtils;
import com.truthbean.debbie.core.ApplicationContext;
import com.truthbean.debbie.mvc.MvcConfiguration;
import com.truthbean.debbie.mvc.filter.RouterFilterManager;
import com.truthbean.debbie.mvc.router.MvcRouterRegister;
import com.truthbean.debbie.server.session.SessionManager;
import com.truthbean.debbie.server.session.SimpleSessionManager;

import java.lang.reflect.Method;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.StandardSocketOptions;
import java.nio.channels.AsynchronousChannelGroup;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.util.concurrent.*;
import java.util.function.Consumer;

/**
 * @author TruthBean/Rogar·Q
 * @since 0.5.5
 */
public class RealAioServerRunner implements Runnable{
    private volatile AsynchronousServerSocketChannel server;

    private final ApplicationContext applicationContext;
    private volatile MvcConfiguration mvcConfiguration;
    private final AioServerConfiguration configuration;

    private SessionManager sessionManager;

    public RealAioServerRunner(final ApplicationContext applicationContext, final AioServerConfiguration configuration) {
        this.applicationContext = applicationContext;
        this.configuration = configuration;
    }

    public RealAioServerRunner init(ApplicationContext applicationContext, final AioServerConfiguration configuration) {
        final MvcConfiguration mvcConfiguration = applicationContext.factory(MvcConfiguration.class);
        var beanInfoManager = applicationContext.getBeanInfoManager();
        MvcRouterRegister.registerRouter(mvcConfiguration, applicationContext);
        RouterFilterManager.registerFilter(mvcConfiguration, beanInfoManager);
        RouterFilterManager.registerCharacterEncodingFilter(mvcConfiguration, "/**");
        RouterFilterManager.registerCorsFilter(mvcConfiguration, "/**");
        RouterFilterManager.registerCsrfFilter(mvcConfiguration, "/**");
        RouterFilterManager.registerSecurityFilter(mvcConfiguration, "/**");

        this.mvcConfiguration = mvcConfiguration;

        final SessionManager sessionManager = new SimpleSessionManager();
        try {
            doInit(configuration, sessionManager);
        } catch (Exception e) {
            LOGGER.error("create aio server error", e);
        }
        return this;
    }

    @SuppressWarnings("unchecked")
    private void doInit(AioServerConfiguration configuration, final SessionManager sessionManager) throws Exception {
        int port = configuration.getPort();

        this.sessionManager = sessionManager;

        // 创建线程池
        var threadFactory = new NamedThreadFactory("AioServerThreadPool").setUncaughtExceptionHandler(new ThreadLoggerUncaughtExceptionHandler());
        int core = Runtime.getRuntime().availableProcessors();
        var executor = new ThreadPoolExecutor(core, core * 10,
                0L, TimeUnit.MICROSECONDS, new LinkedBlockingDeque<>(1024), threadFactory,
                new ThreadPoolExecutor.AbortPolicy());
        // 用于资源共享的异步通道管理器
        var asyncChannelGroup = AsynchronousChannelGroup.withThreadPool(executor);
        SocketAddress socketAddress;
        if (StringUtils.hasText(configuration.getSocketPath())) {
            Class<? extends SocketAddress> unixDomainSocketAddressClass = (Class<? extends SocketAddress>) Class.forName("java.net.UnixDomainSocketAddress");
            Method method = ReflectionUtils.getMethod(unixDomainSocketAddressClass, "of", new Class[]{String.class});
            Object o = ReflectionUtils.invokeStaticMethod(method, configuration.getSocketPath());
            socketAddress = (SocketAddress) o;
        } else {
            // todo 区分 tcp/udp/unix/ssl socket
            // 创建 用在服务端的异步Socket.以下简称服务器socket。
            // 异步通道管理器，会把服务端所用到的相关参数
            socketAddress = new InetSocketAddress(port);
        }
        server = AsynchronousServerSocketChannel.open(asyncChannelGroup)
                .setOption(StandardSocketOptions.SO_REUSEPORT, true)
                .bind(socketAddress);
    }

    void printMessage(Consumer<AioServerConfiguration> consumer) {
        consumer.accept(this.configuration);
    }

    @Override
    public void run() {
        try {
            LOGGER.debug(() -> "running .... ");
            // 为服务端socket指定接收操作对象.accept原型是：
            // accept(A attachment, CompletionHandler<AsynchronousSocketChannel, ? super A> handler)
            // 也就是这里的CompletionHandler的A型参数是实际调用accept方法的第一个参数
            // 即是listener。另一个参数V，就是原型中的客户端socket
            var mvcCompletionHandler = new ServerCompletionHandler(configuration, mvcConfiguration, sessionManager, applicationContext);
            server.accept(server, mvcCompletionHandler);
        } catch (Exception e) {
            LOGGER.error("", e);
        }
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(AioServerApplication.class);
}
