/**
 * Copyright (c) 2026 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 * http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.boot;

import com.truthbean.Console;
import com.truthbean.Logger;
import com.truthbean.LoggerFactory;
import com.truthbean.core.concurrent.NamedThreadFactory;
import com.truthbean.core.concurrent.ThreadLoggerUncaughtExceptionHandler;
import com.truthbean.debbie.concurrent.PooledExecutor;
import com.truthbean.debbie.concurrent.ThreadPooledExecutor;
import com.truthbean.debbie.core.ApplicationContext;
import com.truthbean.debbie.core.ApplicationFactory;
import com.truthbean.debbie.environment.Environment;
import com.truthbean.debbie.event.DebbieReadyEvent;
import com.truthbean.debbie.internal.DebbieApplicationBootContext;
import com.truthbean.debbie.internal.DebbieApplicationFactory;

import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.sql.Timestamp;
import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

/**
 * Base implementation of a Debbie application, providing lifecycle
 * management (start/exit/forceExit), startup and shutdown thread pools,
 * a JVM shutdown hook, and timing utilities.
 * <p>
 * Concrete subclasses implement {@link #init}, {@link #start} and
 * {@link #exit} to define application-specific behaviour.
 *
 * @author truthbean/Rogar·Q
 * @since 0.0.1
 */
public abstract class AbstractApplication implements DebbieApplication {
    /** logger for this application instance */
    private Logger logger;
    /** instant recorded just before the application starts */
    private Instant beforeStartTime;
    /** the Debbie application context */
    private ApplicationContext applicationContext;
    /** the factory that created this application */
    private ApplicationFactory applicationFactory;

    /** whether the application is currently running */
    private final AtomicBoolean running = new AtomicBoolean(false);
    /** whether the application has fully exited */
    private final AtomicBoolean exited = new AtomicBoolean(true);

    /**
     * Reference to the JVM shutdown hook, if registered.
     */
    private Thread shutdownHook;

    /**
     * startup thread
     */
    private final ThreadFactory startupNamedThreadFactory = new NamedThreadFactory("DebbieApplication-Startup", true)
            .setUncaughtExceptionHandler(new ThreadLoggerUncaughtExceptionHandler());
    private final ExecutorService startupExecutorService = new java.util.concurrent.ThreadPoolExecutor(1, 1,
            0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>(1024),
            startupNamedThreadFactory, new ThreadPoolExecutor.AbortPolicy());
    /** pooled executor wrapper for startup tasks */
    private final PooledExecutor startupExecutor = new ThreadPooledExecutor(startupExecutorService, 5000L, "DebbieApplication-Startup");

    /**
     * shutdown thread
     */
    private final ThreadFactory shutdownNamedThreadFactory = new NamedThreadFactory("DebbieApplication-ShutDown", true)
            .setUncaughtExceptionHandler(new ThreadLoggerUncaughtExceptionHandler());
    private final ExecutorService shutdownExecutorService = new java.util.concurrent.ThreadPoolExecutor(1, 1,
            0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>(1024),
            shutdownNamedThreadFactory, new ThreadPoolExecutor.AbortPolicy());
    /** pooled executor wrapper for shutdown tasks */
    private final PooledExecutor shutdownExecutor = new ThreadPooledExecutor(shutdownExecutorService, 5000L, "DebbieApplication-ShutDow");

    /** whether to load configuration from properties files */
    private boolean useProperties = true;

    /** boot context exposed to {@code then}/{@code afterStarted} callbacks */
    private ApplicationBootContext applicationBootContext;

    /** Sets whether to load configuration from properties files. */
    public void setUseProperties(boolean useProperties) {
        this.useProperties = useProperties;
    }

    /** Returns whether properties-based configuration is enabled. */
    public boolean useProperties() {
        return useProperties;
    }

    /** Returns whether this application is a web application. */
    public boolean isWeb() {
        return false;
    }

    /**
     * Returns whether this application is enabled under the given environment.
     *
     * @param environment the current environment
     * @return {@code true} by default
     */
    public boolean isEnable(Environment environment) {
        return true;
    }

    /**
     * Binds the application factory and derives the application context
     * and boot context from it.
     *
     * @param applicationFactory the factory that created this application
     */
    public void setApplicationFactory(ApplicationFactory applicationFactory) {
        this.applicationFactory = applicationFactory;
        this.applicationContext = applicationFactory.getApplicationContext();
        this.applicationBootContext = new DebbieApplicationBootContext(applicationContext);
    }

    /**
     * Registers a callback to run immediately against the boot context.
     *
     * @param applicationBootContextConsumer callback receiving the boot context
     * @return this application for chaining
     */
    @Override
    public DebbieApplication then(Consumer<ApplicationBootContext> applicationBootContextConsumer) {
        applicationBootContextConsumer.accept(this.applicationBootContext);
        return this;
    }

    /**
     * Registers a callback to run after the application has fully started.
     * Blocks the calling thread until startup is complete.
     *
     * @param applicationBootContextConsumer callback receiving the boot context
     * @return the started application handle
     */
    @Override
    public DebbieStartedApplication afterStarted(Consumer<ApplicationBootContext> applicationBootContextConsumer) {
        waitUntilStarted();
        applicationBootContextConsumer.accept(this.applicationBootContext);
        return this;
    }

    /** Blocks until the application is running and no longer exiting. */
    private void waitUntilStarted() {
        while (!(running.get() && !exited.get() && !applicationContext.isExiting())) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                logger.warn("Waiting started happens wrong");
            }
            logger.debug("application running: " + running.get());
            logger.debug("application exiting: " + applicationContext.isExiting());
            logger.debug("application exited: " + exited.get());
        }
    }

    /** Sets the logger used by this application instance. */
    protected void setLogger(Logger logger) {
        this.logger = logger;
    }

    /**
     * init application
     *
     * @param applicationContext applicationContext
     * @param classLoader        main class's classLoader
     * @return DebbieApplication implement
     * @see ApplicationContext
     */
    public abstract DebbieApplication init(ApplicationContext applicationContext,
                                           ClassLoader classLoader);

    /** Records the instant just before the application starts. */
    public void setBeforeStartTime(Instant beforeStartTime) {
        this.beforeStartTime = beforeStartTime;
    }

    /** Calls post-start starters via the application factory, if applicable. */
    protected void postBeforeStart() {
        if (applicationFactory instanceof DebbieApplicationFactory debbieApplicationFactory) {
            debbieApplicationFactory.postCallStarter(this);
        }
    }

    /**
     * Starts the application asynchronously on the startup thread pool.
     * Registers a JVM shutdown hook and publishes a
     * {@link DebbieReadyEvent} once started.
     *
     * @return this application (as a started handle) for chaining
     */
    @Override
    public final DebbieStartedApplication start() {
        startupExecutor.execute(() -> {
            logger.trace("application running: " + running.get());
            logger.trace("application exiting: " + applicationContext.isExiting());
            logger.trace("application exited: " + exited.get());
            if (running.compareAndSet(false, true) && exited.get()) {
                ApplicationArgs applicationArgs = applicationContext.getApplicationArgs();
                registerShutdownHook();
                try {
                    start(beforeStartTime, applicationArgs);
                    applicationContext.publishEvent(new DebbieReadyEvent(applicationContext, this));
                    exited.set(false);
                } catch (Throwable e) {
                    exited.set(false);
                    logger.error("Application start error: \n", e);
                    exit();
                }
                logger.trace("application exited: " + exited.get());
            }
        });
        return this;
    }

    /**
     * run application
     *
     * @param beforeStartTime time of application starting spending
     * @param args            args
     */
    protected abstract void start(Instant beforeStartTime, ApplicationArgs args);

    /** Logs the time spent starting the application and JVM uptime. */
    protected void printStartTime() {
        final RuntimeMXBean mxBean = ManagementFactory.getRuntimeMXBean();
        final Instant now = Instant.now();
        final Duration between = Duration.between(beforeStartTime, now);
        final long uptime = mxBean.getUptime();
        final long startTime = mxBean.getStartTime();
        logger.info(() -> "application start spends " + between.toMillis() +
                "ms ( JVM started at " + new Timestamp(startTime) + ", running for " + uptime + "ms )");
    }

    /**
     * Register a shutdown hook {@linkplain Thread#getName() named}
     * {@code SpringContextShutdownHook} with the JVM runtime, closing this
     * context on JVM shutdown unless it has already been closed at that time.
     * <p>Delegates to {@code doClose()} for the actual closing procedure.
     *
     * @see Runtime#addShutdownHook
     * @see #SHUTDOWN_HOOK_THREAD_NAME
     * @see #exit
     * @see #doExit
     */
    private void registerShutdownHook() {
        if (this.shutdownHook == null) {
            // No shutdown hook registered yet.
            this.shutdownHook = new Thread(SHUTDOWN_HOOK_THREAD_NAME) {
                @Override
                public void run() {
                    if (!exited.get()) {
                        logger.debug("shutdown hook to exit...");
                        exit();
                    }
                }
            };
            Runtime.getRuntime().addShutdownHook(this.shutdownHook);
        }
    }

    /** Releases application context resources before exiting. */
    private synchronized void beforeExit(ApplicationContext applicationContext, String... args) {
        applicationContext.release(args);
    }

    /**
     * Waits until the application has started, then runs the callback and
     * exits.
     *
     * @param applicationBootContextConsumer callback receiving the boot context
     * @return the exited application handle
     */
    @Override
    public DebbieExitedApplication exit(Consumer<ApplicationBootContext> applicationBootContextConsumer) {
        waitUntilStarted();
        applicationBootContextConsumer.accept(this.applicationBootContext);
        return exit();
    }

    /**
     * Gracefully exits the application on the shutdown thread pool,
     * destroying both startup and shutdown executors.
     *
     * @return the exited application handle
     */
    @Override
    public final DebbieExitedApplication exit() {
        logger.debug("application running: " + running.get());
        logger.debug("application exiting: " + applicationContext.isExiting());
        logger.debug("application exited: " + exited.get());
        if (running.get() && !exited.get() && !applicationContext.isExiting()) {
            shutdownExecutor.execute(() -> {
                try {
                    if (running.get() && !exited.get()) {
                        logger.debug("application is exiting...");
                        beforeExit(applicationContext);
                        doExit(applicationContext.getApplicationArgs());
                    }
                    logger.info("DebbieApplication-Startup thread will close...");
                    startupExecutor.destroy();
                    logger.info("DebbieApplication-ShutDown thread will close...");
                    shutdownExecutor.destroy();
                    exited.set(true);
                } catch (Throwable e) {
                    logger.error("", e);
                }
            });
        }
        LoggerFactory.destroy();
        return this;
    }

    /**
     * Runs the callback then force-exits the application without waiting
     * for startup to complete.
     *
     * @param applicationBootContextConsumer callback receiving the boot context
     */
    @Override
    public void forceExit(Consumer<ApplicationBootContext> applicationBootContextConsumer) {
        try {
            applicationBootContextConsumer.accept(this.applicationBootContext);
        } catch (Throwable e) {
            logger.error("do action error before force exit.", e);
        }
        forceExit();
    }

    /** Force-exits the application synchronously on the calling thread. */
    @Override
    public void forceExit() {
        try {
            logger.debug("application is exiting...");
            beforeExit(applicationContext);
            doExit(applicationContext.getApplicationArgs());
            logger.info("DebbieApplication-Startup thread will close...");
            startupExecutor.destroy();
            logger.info("DebbieApplication-ShutDown thread will close...");
            shutdownExecutor.destroy();
            exited.set(true);
        } catch (Throwable e) {
            logger.error("", e);
        }
    }

    /**
     * exit application
     *
     * @param beforeStartTime before start time, long timestamp
     * @param args            args
     */
    protected abstract void exit(Instant beforeStartTime, ApplicationArgs args);

    /** Logs elapsed time and uptime information when exiting. */
    protected void printExitTime() {
        RuntimeMXBean mxBean = ManagementFactory.getRuntimeMXBean();
        Instant now = Instant.now();
        Duration between = Duration.between(beforeStartTime, now);
        long uptime = mxBean.getUptime();
        long startTime = mxBean.getStartTime();
        logger.info(() -> "JVM started at " + new Timestamp(startTime) + ", had run for " + uptime + "ms");
        if (logger.isDebugEnabled()) {
            logger.debug(() -> "application start spends " + between.toDays() + " days");
            logger.debug(() -> "application start spends " + between.toMinutes() + " minutes");
            logger.debug(() -> "application start spends " + between.toSeconds() + " seconds");
            logger.debug(() -> "application start spends " + between.toMillis() + " million seconds");
            logger.debug(() -> "application start spends " + between.toNanos() + " nano seconds");
        }
        logger.info(() -> "application is exiting");
    }

    /**
     * Performs the actual exit: calls the abstract {@link #exit}, releases
     * the application factory, removes the JVM shutdown hook, and invokes
     * {@code System.gc()}.
     *
     * @param args application arguments
     */
    public final void doExit(ApplicationArgs args) {
        try {
            exit(beforeStartTime, args);
            applicationFactory.release();
            // If we registered a JVM shutdown hook, we don't need it anymore now:
            // We've already explicitly closed the context.
            if (this.shutdownHook != null) {
                try {
                    Runtime.getRuntime().removeShutdownHook(this.shutdownHook);
                    this.running.set(false);
                } catch (IllegalStateException ex) {
                    // VM is already shutting down
                    logger.info("JVM is shutting down (" + ex.getMessage() + ")");
                }
            }
        } catch (Exception e) {
            logger.error("do application exiting error. ", e);
        } finally {
            // call gc
            System.gc();
        }
    }
}
