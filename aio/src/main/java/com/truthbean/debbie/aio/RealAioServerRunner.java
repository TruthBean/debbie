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
import java.net.SocketOption;
import java.net.StandardSocketOptions;
import java.nio.channels.AsynchronousChannelGroup;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.util.Set;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

/**
 * The actual runner for the AIO (NIO.2) HTTP server.
 * <p>
 * Creates an {@link AsynchronousServerSocketChannel}, binds it to the
 * configured address (TCP or Unix domain socket), and starts accepting
 * connections via a {@link ServerCompletionHandler}. Router and filter
 * registration is performed during {@link #init}.
 *
 * @author TruthBean/Rogar·Q
 * @since 0.5.5
 */
public class RealAioServerRunner implements Runnable {
    /**
     * the asynchronous server socket channel, created during init
     */
    private volatile AsynchronousServerSocketChannel server;

    /**
     * the Debbie application context for bean lookup
     */
    private final ApplicationContext applicationContext;
    /**
     * MVC configuration resolved from the bean factory
     */
    private volatile MvcConfiguration mvcConfiguration;
    /**
     * AIO server configuration (port, charset, thread pool, etc.)
     */
    private final AioServerConfiguration configuration;

    /**
     * session manager for HTTP sessions
     */
    private SessionManager sessionManager;

    /**
     * Creates a runner with the given application context and configuration.
     *
     * @param applicationContext the Debbie application context
     * @param configuration      the AIO server configuration
     */
    public RealAioServerRunner(final ApplicationContext applicationContext, final AioServerConfiguration configuration) {
        this.applicationContext = applicationContext;
        this.configuration = configuration;
    }

    /**
     * Initializes the AIO server: resolves MVC configuration, registers
     * routers and filters (character encoding, CORS, CSRF, security),
     * creates the session manager, and opens/binds the asynchronous
     * server socket channel.
     *
     * @param applicationContext the Debbie application context
     * @param configuration      the AIO server configuration
     * @return this runner instance for chaining
     */
    public RealAioServerRunner init(ApplicationContext applicationContext, final AioServerConfiguration configuration) {
        final MvcConfiguration mvcConfiguration = applicationContext.getGlobalBeanFactory().factory(MvcConfiguration.class);
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

    /**
     * Opens and binds the asynchronous server socket channel.
     * <p>
     * Supports both TCP ({@link InetSocketAddress}) and Unix domain sockets
     * (via reflection on {@code java.net.UnixDomainSocketAddress}). Sets
     * common socket options (SO_REUSEPORT, SO_REUSEADDR, SO_RCVBUF,
     * SO_SNDBUF, SO_KEEPALIVE) when supported by the platform.
     *
     * @param configuration  the AIO server configuration
     * @param sessionManager the session manager to use
     * @throws Exception if the channel cannot be opened or bound
     */
    @SuppressWarnings("unchecked")
    private void doInit(AioServerConfiguration configuration, final SessionManager sessionManager) throws Exception {
        int port = configuration.getPort();

        this.sessionManager = sessionManager;

        // 创建线程池
        var threadFactory = new NamedThreadFactory("AioServerThreadPool").setUncaughtExceptionHandler(new ThreadLoggerUncaughtExceptionHandler());
        var executor = new ThreadPoolExecutor(configuration.getThreadPoolConfig().getCoreSize(), configuration.getThreadPoolConfig().getMaximumPoolSize(),
                0L, TimeUnit.MICROSECONDS, new LinkedBlockingDeque<>(configuration.getThreadPoolConfig().getQueueSize()),
                threadFactory, new ThreadPoolExecutor.AbortPolicy());
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
            socketAddress = new InetSocketAddress(port);
        }
        AsynchronousServerSocketChannel server = AsynchronousServerSocketChannel.open(asyncChannelGroup);
        Set<SocketOption<?>> socketOptions = server.supportedOptions();
        if (socketOptions.contains(StandardSocketOptions.SO_REUSEPORT)) {
            server.setOption(StandardSocketOptions.SO_REUSEPORT, true);
        }
        if (socketOptions.contains(StandardSocketOptions.SO_REUSEADDR)) {
            server.setOption(StandardSocketOptions.SO_REUSEADDR, true);
        }
        if (socketOptions.contains(StandardSocketOptions.SO_RCVBUF)) {
            server.setOption(StandardSocketOptions.SO_RCVBUF, 64 * 1024);
        }
        if (socketOptions.contains(StandardSocketOptions.SO_SNDBUF)) {
            server.setOption(StandardSocketOptions.SO_SNDBUF, 64 * 1024);
        }
        if (socketOptions.contains(StandardSocketOptions.SO_KEEPALIVE)) {
            server.setOption(StandardSocketOptions.SO_KEEPALIVE, true);
        }
        server.bind(socketAddress);
        this.server = server;
    }

    /**
     * Passes the AIO server configuration to the given consumer, typically
     * used to print server startup information.
     *
     * @param consumer the consumer receiving the configuration
     */
    void printMessage(Consumer<AioServerConfiguration> consumer) {
        consumer.accept(this.configuration);
    }

    /**
     * Starts accepting connections by invoking
     * {@link AsynchronousServerSocketChannel#accept} with a
     * {@link ServerCompletionHandler}.
     */
    @Override
    public void run() {
        try {
            LOGGER.debug(() -> "running .... ");
            var mvcCompletionHandler = new ServerCompletionHandler(configuration, mvcConfiguration, sessionManager, applicationContext, server);
            server.accept(server, mvcCompletionHandler);
        } catch (Exception e) {
            LOGGER.error("", e);
        }
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(AioServerApplication.class);
}
