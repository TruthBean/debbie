package com.truthbean.debbie.empty;

import com.truthbean.debbie.boot.DebbieApplication;
import com.truthbean.debbie.core.ApplicationContext;

/**
 * @author TruthBean
 * @since 0.5.3
 */
public class EmptyDebbieApplication implements DebbieApplication {

    private final ApplicationContext applicationContext = new EmptyApplicationContext();

    @Override
    public void start() {
        // applicationContext.refreshBeans();
    }

    @Override
    public void exit() {
        applicationContext.release();
    }

    @Override
    public ApplicationContext getApplicationContext() {
        return applicationContext;
    }
}
