/**
 * Copyright (c) 2020 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *         http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.concurrent;

import com.truthbean.Logger;
import com.truthbean.debbie.lang.Callback;
import com.truthbean.logger.LogLevel;
import com.truthbean.logger.LoggerFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.*;

/**
 * @author truthbean/Rogar·Q
 * @since 0.0.2
 * Created on 2020-04-03 14:00.
 */
public class ThreadPooledExecutor implements PooledExecutor {

    private final long awaitTerminationTime;

    private final ExecutorService executorService;

    public ThreadPooledExecutor() {
        this(Runtime.getRuntime().availableProcessors(), Runtime.getRuntime().availableProcessors() * 10,
                new NamedThreadFactory()
                .setUncaughtExceptionHandler((t, e) ->
                        LoggerFactory.getLogger(LogLevel.ERROR, t.getClass()).error("", e)), 5000L);
    }

    public ThreadPooledExecutor(int coreSize, int maximumPoolSize, ThreadFactory threadFactory) {
        this(coreSize, maximumPoolSize, threadFactory, 5000L);
    }

    public ThreadPooledExecutor(int coreSize, int maximumPoolSize, ThreadFactory threadFactory, long awaitTerminationTime) {
        this.awaitTerminationTime = awaitTerminationTime;

        this.executorService = new java.util.concurrent.ThreadPoolExecutor(coreSize, maximumPoolSize,
                0L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<>(1024), threadFactory, new java.util.concurrent.ThreadPoolExecutor.AbortPolicy());
    }

    @Override
    public void execute(Runnable task) {
        if (isRunning())
            this.executorService.execute(task);
    }

    @Override
    public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks, long timeout) {
        try {
            return this.executorService.invokeAll(tasks, timeout, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            LOGGER.error("executorService.invokeAll error. ", e);
        }
        return new ArrayList<>();
    }

    @Override
    public boolean isRunning() {
        return !executorService.isShutdown() && !executorService.isTerminated();
    }

    @Override
    public <R> Future<R> submit(Callback<R> task, Object...args) {
        return this.executorService.submit(() -> task.call(args));
    }

    @Override
    public void destroy() {
        if (isRunning()) {
            try {
                executorService.shutdown();
                while (!executorService.awaitTermination(awaitTerminationTime, TimeUnit.MILLISECONDS)) {
                    // wait
                    LOGGER.info("waiting for executorService termination");
                }
            } catch (InterruptedException e) {
                LOGGER.error("executorService.destroy error. ", e);
                executorService.shutdownNow();
            }
        }
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(ThreadPooledExecutor.class);
}
