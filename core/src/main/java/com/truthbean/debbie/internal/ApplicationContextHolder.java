package com.truthbean.debbie.internal;

import com.truthbean.debbie.core.ApplicationContext;

/**
 * @author TruthBean/Rogar·Q
 * @since 0.5.5
 */
public class ApplicationContextHolder {
    private volatile static DebbieApplicationContext applicationContext;

    static void setApplicationContext(DebbieApplicationContext applicationContext) {
        ApplicationContextHolder.applicationContext = applicationContext;
    }

    public static ApplicationContext getApplicationContext() {
        return applicationContext;
    }
}
