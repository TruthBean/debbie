package com.truthbean.debbie.task;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author truthbean/Rogar·Q
 * @since 0.0.2
 * Created on 2020-04-03 14:04.
 */
public class NamedThreadFactory implements ThreadFactory {
    private static final AtomicInteger poolNumber = new AtomicInteger(1);
    private final ThreadGroup group;
    private final AtomicInteger threadNumber = new AtomicInteger(1);
    private final String namePrefix;

    private Boolean daemon;
    private Integer priority;

    public NamedThreadFactory() {
        SecurityManager s = System.getSecurityManager();
        group = (s != null) ? s.getThreadGroup() : Thread.currentThread().getThreadGroup();
        namePrefix = "debbie-" + poolNumber.getAndIncrement() + "-thread-";
    }

    public NamedThreadFactory(String prefix) {
        SecurityManager s = System.getSecurityManager();
        group = (s != null) ? s.getThreadGroup() : Thread.currentThread().getThreadGroup();
        if (prefix == null)
            prefix = "debbie-";
        namePrefix = prefix + poolNumber.getAndIncrement() + "-thread-";
    }

    public NamedThreadFactory(String prefix, ThreadGroup group) {
        if (group == null) {
            SecurityManager s = System.getSecurityManager();
            this.group = (s != null) ? s.getThreadGroup() : Thread.currentThread().getThreadGroup();
        } else {
            this.group = group;
        }
        if (prefix == null)
            prefix = "debbie-";
        namePrefix = prefix + poolNumber.getAndIncrement() + "-thread-";
    }

    public Boolean getDaemon() {
        return daemon;
    }

    public void setDaemon(Boolean daemon) {
        this.daemon = daemon;
    }

    @Override
    public Thread newThread(Runnable r) {
        Thread thread = new Thread(group, r,
                namePrefix + threadNumber.getAndIncrement(),
                0);
        if (daemon != null) {
            thread.setDaemon(daemon);
        } else if (thread.isDaemon()) {
            thread.setDaemon(false);
        }

        if (priority != null) {
            thread.setPriority(priority);
        } else if (thread.getPriority() != Thread.NORM_PRIORITY)
            thread.setPriority(Thread.NORM_PRIORITY);
        return thread;
    }
}
