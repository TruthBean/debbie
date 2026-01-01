package com.truthbean.debbie.concurrent;

/**
 * @author TruthBean/Rogar·Q
 * @since 0.6.0
 */
public class ThreadPoolConfiguration {
    private int coreSize = Runtime.getRuntime().availableProcessors();
    private int maximumPoolSize = Runtime.getRuntime().availableProcessors() * 10;
    private int queueSize = 1024;

    public int getCoreSize() {
        return coreSize;
    }

    public void setCoreSize(int coreSize) {
        this.coreSize = coreSize <= 0 ? Runtime.getRuntime().availableProcessors() : coreSize;
    }

    public int getMaximumPoolSize() {
        return maximumPoolSize;
    }

    public void setMaximumPoolSize(int maximumPoolSize) {
        this.maximumPoolSize = maximumPoolSize <= 0 ? Runtime.getRuntime().availableProcessors() * 10 : maximumPoolSize;
    }

    public int getQueueSize() {
        return queueSize;
    }

    public void setQueueSize(int queueSize) {
        this.queueSize = queueSize <= 0 ? 1024 : queueSize;
    }

    @Override
    public String toString() {
        return "{" +
                "\"coreSize\":" + coreSize +
                ",\"maximumPoolSize\":" + maximumPoolSize +
                ",\"queueSize\":" + queueSize +
                '}';
    }
}
