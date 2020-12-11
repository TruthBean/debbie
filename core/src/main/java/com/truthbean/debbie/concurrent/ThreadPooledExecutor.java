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

import com.truthbean.debbie.lang.Callback;
import com.truthbean.debbie.properties.BaseProperties;
import com.truthbean.logger.LogLevel;
import com.truthbean.logger.LoggerFactory;

import java.util.concurrent.*;

/**
 * @author truthbean/Rogar·Q
 * @since 0.0.2
 * Created on 2020-04-03 14:00.
 */
public class ThreadPooledExecutor implements Executor {

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

    public boolean isRunning() {
        return !executorService.isShutdown() && !executorService.isTerminated();
    }

    public <R> Future<R> submit(Callback<R> task, Object...args) {
        return this.executorService.submit(() -> task.call(args));
    }

    public void destroy() {
        if (isRunning()) {
            try {
                executorService.shutdown();
                executorService.awaitTermination(awaitTerminationTime, TimeUnit.MILLISECONDS);
            } catch (InterruptedException e) {
                e.printStackTrace();
                executorService.shutdownNow();
            }
        }
    }
}
