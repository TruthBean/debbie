package com.truthbean.debbie.boot;

import com.truthbean.debbie.bean.BeanFactoryHandler;
import org.slf4j.Logger;

/**
 * @author TruthBean
 * @since 0.0.1
 */
public interface DebbieApplication {

    default void beforeStart(Logger logger, BeanFactoryHandler beanFactoryHandler) {
        beanFactoryHandler.autoCreateBeans();
    }

    /**
     * run application
     * @param args args
     */
    void start(String... args);

    /**
     * exit application
     * @param args args
     */
    void exit(String... args);
}
