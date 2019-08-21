package com.truthbean.debbie.boot;

import com.truthbean.debbie.bean.BeanFactoryHandler;
import org.slf4j.Logger;

/**
 * @author TruthBean
 * @since 0.0.1
 */
public abstract class DebbieApplication {

    private Logger logger;
    private long beforeStartTime;

    public void setBeforeStartTime(long beforeStartTime) {
        this.beforeStartTime = beforeStartTime;
    }

    protected void beforeStart(Logger logger, BeanFactoryHandler beanFactoryHandler) {
        this.logger = logger;
        beanFactoryHandler.autoCreateBeans();
    }

    /**
     * run application
     * @param beforeStartTime time of application starting spending
     * @param args args
     */
    protected abstract void start(long beforeStartTime, String... args);

    public final void start(String... args) {
        start(beforeStartTime, args);
    }

    /**
     * exit application
     * @param args args
     */
    public abstract void exit(String... args);
}
