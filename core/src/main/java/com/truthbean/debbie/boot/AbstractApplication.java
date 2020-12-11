/**
 * Copyright (c) 2020 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 * http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.boot;

import com.truthbean.Logger;
import com.truthbean.debbie.DebbieVersion;
import com.truthbean.debbie.concurrent.NamedThreadFactory;
import com.truthbean.debbie.concurrent.ThreadPooledExecutor;
import com.truthbean.debbie.core.ApplicationContext;
import com.truthbean.debbie.core.ApplicationFactory;
import com.truthbean.debbie.internal.DebbieApplicationFactory;
import com.truthbean.debbie.properties.DebbieConfigurationCenter;

import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.sql.Timestamp;
import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.ThreadFactory;
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

    /**
     * Reference to the JVM shutdown hook, if registered.
     */
    private Thread shutdownHook;

    /**
     * startup and shutdown thread
     */
    private final ThreadFactory namedThreadFactory = new NamedThreadFactory("DebbieApplication-StartupShutDown", true);
    private final ThreadPooledExecutor startupShutdownThreadPool = new ThreadPooledExecutor(1, 2, namedThreadFactory);

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
     * @param configurationCenter configurationFactory
     * @see DebbieConfigurationCenter
     * @param applicationContext applicationContext
     * @param classLoader main class's classLoader
     * @see ApplicationContext
     * @return DebbieApplication implement
     */
    public abstract DebbieApplication init(DebbieConfigurationCenter configurationCenter, ApplicationContext applicationContext,
                                           ClassLoader classLoader);

    public void setBeforeStartTime(Instant beforeStartTime) {
        this.beforeStartTime = beforeStartTime;
    }

    protected void postBeforeStart() {
        if (applicationFactory instanceof DebbieApplicationFactory) {
            ((DebbieApplicationFactory) applicationFactory).postCallStarter();
        }
    }

    @Override
    public final void start(String... args) {
        startupShutdownThreadPool.execute(() -> {
            if (running.compareAndSet(false, true) && exited.get()) {
                registerShutdownHook(args);
                start(beforeStartTime, args);
                exited.set(false);
            }
        });
    }

    /**
     * run application
     *
     * @param beforeStartTime time of application starting spending
     * @param args            args
     */
    protected abstract void start(Instant beforeStartTime, String... args);

    protected void printStartTime() {
        final RuntimeMXBean mxBean = ManagementFactory.getRuntimeMXBean();
        final Instant now = Instant.now();
        final Duration between = Duration.between(beforeStartTime, now);
        final long uptime = mxBean.getUptime();
        final long startTime = mxBean.getStartTime();
        logger.info(() -> "application start spends " + between.toMillis() +
                "ms ( JVM started at "  + new Timestamp(startTime) + ", running for "  + uptime + "ms )");
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
    private void registerShutdownHook(String... args) {
        if (this.shutdownHook == null) {
            // No shutdown hook registered yet.
            this.shutdownHook = new Thread(SHUTDOWN_HOOK_THREAD_NAME) {
                @Override
                public void run() {
                    synchronized (startupShutdownMonitor) {
                        exit(args);
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
    public final void exit(String... args) {
        startupShutdownThreadPool.execute(() -> {
            if (running.get() && exited.compareAndSet(false, true)) {
                logger.info("application is exiting...");
                beforeExit(applicationContext, args);
                doExit(args);
            }
        });
        startupShutdownThreadPool.destroy();
    }

    /**
     * exit application
     * @param beforeStartTime before start time, long timestamp
     * @param args args
     */
    protected abstract void exit(Instant beforeStartTime, String... args);

    protected void printExitTime() {
        RuntimeMXBean mxBean = ManagementFactory.getRuntimeMXBean();
        Instant now = Instant.now();
        Duration between = Duration.between(beforeStartTime, now);
        long uptime = mxBean.getUptime();
        long startTime = mxBean.getStartTime();
        logger.info(() -> "JVM started at "  + new Timestamp(startTime) + ", running for "  + uptime + "ms");
        logger.info(() -> "application start spends " + between.toDays() + " days");
        logger.info(() -> "application start spends " + between.toMinutes() + " minutes");
        logger.info(() -> "application start spends " + between.toSeconds() + " seconds");
        logger.info(() -> "application start spends " + between.toMillis() + " million seconds");
        logger.info(() -> "application start spends " + between.toNanos() + " nano seconds");
    }

    public final void doExit(String... args) {
        synchronized (this.startupShutdownMonitor) {
            exit(beforeStartTime, args);
            if (applicationFactory instanceof DebbieApplicationFactory) {
                applicationFactory.release(args);
            }
            // If we registered a JVM shutdown hook, we don't need it anymore now:
            // We've already explicitly closed the context.
            if (this.shutdownHook != null) {
                try {
                    Runtime.getRuntime().removeShutdownHook(this.shutdownHook);
                    this.running.set(false);
                } catch (IllegalStateException ex) {
                    // ignore - VM is already shutting down
                }
            }
        }
    }
}
