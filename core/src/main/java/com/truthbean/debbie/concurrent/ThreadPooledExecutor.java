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

import com.truthbean.debbie.concurrent.NamedThreadFactory;
import com.truthbean.debbie.lang.Callback;

import java.util.concurrent.*;

/**
 * @author truthbean/Rogar·Q
 * @since 0.0.2
 * Created on 2020-04-03 14:00.
 */
public class ThreadPooledExecutor implements Executor {

    private final int coreSize;
    private final int maximumPoolSize;
    private final ThreadFactory threadFactory;

    private final ExecutorService executorService;

    public ThreadPooledExecutor() {
        this.coreSize = 10;
        this.maximumPoolSize = 200;
        this.threadFactory = new NamedThreadFactory();

        this.executorService = new java.util.concurrent.ThreadPoolExecutor(coreSize, maximumPoolSize,
                0L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<Runnable>(1024), threadFactory, new java.util.concurrent.ThreadPoolExecutor.AbortPolicy());
    }

    public ThreadPooledExecutor(int coreSize, int maximumPoolSize, ThreadFactory threadFactory) {
        this.coreSize = coreSize;
        this.maximumPoolSize = maximumPoolSize;
        this.threadFactory = threadFactory;

        this.executorService = new java.util.concurrent.ThreadPoolExecutor(coreSize, maximumPoolSize,
                0L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<Runnable>(1024), threadFactory, new java.util.concurrent.ThreadPoolExecutor.AbortPolicy());
    }

    @Override
    public void execute(Runnable task) {
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
                executorService.awaitTermination(5000, TimeUnit.MILLISECONDS);
            } catch (InterruptedException e) {
                executorService.shutdownNow();
            }
        }
    }
}
