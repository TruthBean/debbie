/**
 * Copyright (c) 2023 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 * http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.boot;

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
import com.truthbean.debbie.internal.DebbieApplicationFactory;

import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.sql.Timestamp;
import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author truthbean/Rogar·Q
 * @since 0.0.1
 */
public abstract class AbstractApplication implements DebbieApplication {
    private Logger logger;
    private Instant beforeStartTime;
    private ApplicationContext applicationContext;
    private ApplicationFactory applicationFactory;

    private final AtomicBoolean running = new AtomicBoolean(false);
    private final AtomicBoolean exited = new AtomicBoolean(true);

    /**
     * Synchronization monitor for the "refresh" and "destroy".
     */
    private final Object startupShutdownMonitor = new Object();
    // private final Lock startupShutdownLock = new ReentrantLock(true);

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
    private final PooledExecutor startupExecutor = new ThreadPooledExecutor(startupExecutorService, 5000L, "DebbieApplication-Startup");

    /**
     * shutdown thread
     */
    private final ThreadFactory shutdownNamedThreadFactory = new NamedThreadFactory("DebbieApplication-ShutDown", true)
            .setUncaughtExceptionHandler(new ThreadLoggerUncaughtExceptionHandler());
    private final ExecutorService shutdownExecutorService = new java.util.concurrent.ThreadPoolExecutor(1, 1,
            0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>(1024),
            shutdownNamedThreadFactory, new ThreadPoolExecutor.AbortPolicy());
    private final PooledExecutor shutdownExecutor = new ThreadPooledExecutor(shutdownExecutorService, 5000L, "DebbieApplication-ShutDow");

    private boolean useProperties = true;

    public void setUseProperties(boolean useProperties) {
        this.useProperties = useProperties;
    }

    public boolean useProperties() {
        return useProperties;
    }

    public boolean isWeb() {
        return false;
    }

    public boolean isEnable(Environment environment) {
        return true;
    }

    public void setApplicationFactory(ApplicationFactory applicationFactory) {
        this.applicationFactory = applicationFactory;
        this.applicationContext = applicationFactory.getApplicationContext();
    }

    @Override
    public ApplicationContext getApplicationContext() {
        return this.applicationContext;
    }

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

    public void setBeforeStartTime(Instant beforeStartTime) {
        this.beforeStartTime = beforeStartTime;
    }

    protected void postBeforeStart() {
        if (applicationFactory instanceof DebbieApplicationFactory debbieApplicationFactory) {
            debbieApplicationFactory.postCallStarter(this);
        }
    }

    @Override
    public final void start() {
        startupExecutor.execute(() -> {
            logger.debug("application running: " + running.get());
            logger.debug("application exiting: " + applicationContext.isExiting());
            logger.debug("application exited: " + exited.get());
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
            }
        });
    }

    /**
     * run application
     *
     * @param beforeStartTime time of application starting spending
     * @param args            args
     */
    protected abstract void start(Instant beforeStartTime, ApplicationArgs args);

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

    private synchronized void beforeExit(ApplicationContext applicationContext, String... args) {
        applicationContext.release(args);
    }

    @Override
    public final void exit() {
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
    }

    /**
     * exit application
     *
     * @param beforeStartTime before start time, long timestamp
     * @param args            args
     */
    protected abstract void exit(Instant beforeStartTime, ApplicationArgs args);

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

    public final void doExit(ApplicationArgs args) {
        // if (startupShutdownLock.tryLock()) {
        try {
            exit(beforeStartTime, args);
            if (applicationFactory instanceof DebbieApplicationFactory) {
                applicationFactory.release();
            }
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
            // startupShutdownLock.unlock();
            // call gc
            System.gc();
        }
        // }
    }
}
