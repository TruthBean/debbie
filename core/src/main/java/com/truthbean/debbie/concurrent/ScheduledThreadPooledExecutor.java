/**
 * Copyright (c) 2021 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 * http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.concurrent;

import com.truthbean.debbie.lang.Callback;
import com.truthbean.logger.LogLevel;
import com.truthbean.LoggerFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.*;

/**
 * @author TruthBean/Rogar·Q
 * @since 0.2.0
 * Created on 2020-12-21 11:03
 */
public class ScheduledThreadPooledExecutor implements ScheduledPooledExecutor {
    private final long awaitTerminationTime;

    private final ScheduledExecutorService executorService;

    public ScheduledThreadPooledExecutor() {
        this(Runtime.getRuntime().availableProcessors(),
                new NamedThreadFactory()
                        .setUncaughtExceptionHandler((t, e) ->
                                LoggerFactory.getLogger(LogLevel.ERROR, t.getClass()).error("", e)), 5000L);
    }

    public ScheduledThreadPooledExecutor(int coreSize, ThreadFactory threadFactory) {
        this(coreSize, threadFactory, 5000L);
    }

    public ScheduledThreadPooledExecutor(int coreSize, ThreadFactory threadFactory, long awaitTerminationTime) {
        this.awaitTerminationTime = awaitTerminationTime;

        this.executorService = new java.util.concurrent.ScheduledThreadPoolExecutor(coreSize, threadFactory);
    }

    @Override
    public void execute(Runnable task) {
        if (isRunning())
            this.executorService.execute(task);
    }

    @Override
    public <R> Future<R> submit(Callback<R> task, Object... args) {
        return this.executorService.submit(() -> task.call(args));
    }

    @Override
    public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks, long timeout) {
        try {
            return this.executorService.invokeAll(tasks, timeout, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    @Override
    public ScheduledFuture<?> schedule(Runnable command, long delay) {
        return this.executorService.schedule(command, delay, TimeUnit.MILLISECONDS);
    }

    @Override
    public ScheduledFuture<?> scheduleAtFixedRate(Runnable command, long initialDelay, long period) {
        return this.executorService.scheduleAtFixedRate(command, initialDelay, period, TimeUnit.MILLISECONDS);
    }

    @Override
    public boolean isRunning() {
        return !executorService.isShutdown() && !executorService.isTerminated();
    }

    @Override
    public void destroy() {
        if (isRunning()) {
            try {
                executorService.shutdown();
                while (!executorService.awaitTermination(awaitTerminationTime, TimeUnit.MILLISECONDS)) {
                    // wait
                    System.out.println("wait");
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
                executorService.shutdownNow();
            }
        }
    }

}
