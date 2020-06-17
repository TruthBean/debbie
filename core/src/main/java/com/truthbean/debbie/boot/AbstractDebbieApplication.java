/**
 * Copyright (c) 2020 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *         http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.boot;

import com.truthbean.debbie.DebbieVersion;
import com.truthbean.debbie.bean.BeanFactoryHandler;
import com.truthbean.debbie.concurrent.NamedThreadFactory;
import com.truthbean.debbie.concurrent.ThreadPooledExecutor;
import org.slf4j.Logger;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author truthbean/Rogar·Q
 * @since 0.0.2
 * Created on 2020-04-28 15:07.
 */
public abstract class AbstractDebbieApplication implements DebbieApplication {
    private final Logger logger;
    private long beforeStartTime;
    private final BeanFactoryHandler beanFactoryHandler;

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

    public AbstractDebbieApplication(Logger logger, BeanFactoryHandler beanFactoryHandler) {
        this.logger = logger;
        this.beanFactoryHandler = beanFactoryHandler;
    }

    void setBeforeStartTime(long beforeStartTime) {
        this.beforeStartTime = beforeStartTime;
    }

    protected void postBeforeStart() {
        if (beanFactoryHandler instanceof DebbieApplicationFactory) {
            ((DebbieApplicationFactory) beanFactoryHandler).postCallStarter();
        }
    }

    @Override
    public final void start(String... args) {
        startupShutdownThreadPool.execute(() -> {
            logger.debug("debbie ("+ DebbieVersion.getVersion() +") application start in thread ...");
            if (running.compareAndSet(false, true) && exited.get()) {
                registerShutdownHook();
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
    protected abstract void start(long beforeStartTime, String... args);

    /**
     * Register a shutdown hook {@linkplain Thread#getName() named}
     * {@code SpringContextShutdownHook} with the JVM runtime, closing this
     * context on JVM shutdown unless it has already been closed at that time.
     * <p>Delegates to {@code doClose()} for the actual closing procedure.
     *
     * @see Runtime#addShutdownHook
     * @see ConfigurableApplicationContext#SHUTDOWN_HOOK_THREAD_NAME
     * @see #close()
     * @see #doClose()
     */
    private void registerShutdownHook() {
        if (this.shutdownHook == null) {
            // No shutdown hook registered yet.
            this.shutdownHook = new Thread(SHUTDOWN_HOOK_THREAD_NAME) {
                @Override
                public void run() {
                    synchronized (startupShutdownMonitor) {
                        exit();
                    }
                }
            };
            Runtime.getRuntime().addShutdownHook(this.shutdownHook);
        }
    }

    private synchronized void beforeExit(BeanFactoryHandler handler, String... args) {
        handler.release(args);
    }

    @Override
    public final void exit(String... args) {
        startupShutdownThreadPool.execute(() -> {
            if (running.get() && exited.compareAndSet(false, true)) {
                logger.debug("application exiting...");
                beforeExit(beanFactoryHandler, args);
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
    protected abstract void exit(long beforeStartTime, String... args);

    public final void doExit(String... args) {
        synchronized (this.startupShutdownMonitor) {
            exit(beforeStartTime, args);
            // If we registered a JVM shutdown hook, we don't need it anymore now:
            // We've already explicitly closed the context.
            if (this.shutdownHook != null) {
                try {
                    Runtime.getRuntime().removeShutdownHook(this.shutdownHook);
                } catch (IllegalStateException ex) {
                    // ignore - VM is already shutting down
                }
            }
        }
    }
}