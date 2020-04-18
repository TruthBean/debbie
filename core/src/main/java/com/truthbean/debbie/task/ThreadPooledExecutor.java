package com.truthbean.debbie.task;

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

    public <R> Future<R> submit(Callback<R> task, Object...args) {
        return this.executorService.submit(() -> {
            return task.call(args);
        });
    }

    public void destroy() {
        if (executorService != null) {
            if (!executorService.isShutdown() && !executorService.isTerminated()) {
                try {
                    executorService.shutdown();
                    executorService.awaitTermination(5000, TimeUnit.MILLISECONDS);
                } catch (InterruptedException e) {
                    executorService.shutdownNow();
                }
            }
        }
    }
}
