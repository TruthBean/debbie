package com.truthbean.debbie.concurrent;

import com.truthbean.Logger;
import com.truthbean.LoggerFactory;

import java.util.concurrent.Future;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;

public class DiscardOldestPolicy implements RejectedExecutionHandler {
    /**
     * Creates a {@code DiscardOldestPolicy} for the given executor.
     */
    public DiscardOldestPolicy() {
    }

    /**
     * Obtains and ignores the next task that the executor
     * would otherwise execute, if one is immediately available,
     * and then retries execution of task r, unless the executor
     * is shut down, in which case task r is instead discarded.
     *
     * @param r the runnable task requested to be executed
     * @param e the executor attempting to execute this task
     */
    @Override
    public void rejectedExecution(Runnable r, ThreadPoolExecutor e) {
        if (!e.isShutdown()) {
            Runnable dropped = e.getQueue().poll();
            if (dropped instanceof Future<?>) {
                ((Future<?>) dropped).cancel(false);
            }
            LOGGER.warn("drop the oldest task " + dropped);
        }
    }

    private final static Logger LOGGER = LoggerFactory.getLogger(DiscardOldestPolicy.class);
}