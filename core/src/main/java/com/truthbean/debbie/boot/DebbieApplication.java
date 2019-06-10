package com.truthbean.debbie.boot;

/**
 * @author TruthBean
 * @since 0.0.1
 */
public interface DebbieApplication {

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
