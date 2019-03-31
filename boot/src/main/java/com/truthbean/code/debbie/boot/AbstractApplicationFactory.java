package com.truthbean.code.debbie.boot;

import com.truthbean.code.debbie.core.bean.BeanScanConfiguration;

/**
 * @author TruthBean
 * @since 0.0.1
 */
public abstract class AbstractApplicationFactory<C extends BeanScanConfiguration> {

    private boolean useProperties = true;

    public void setUseProperties(boolean useProperties) {
        this.useProperties = useProperties;
    }

    public boolean useProperties() {
        return useProperties;
    }

    public boolean isWeb() {
        return false;
    }

    /**
     * application factory
     * @param configuration application configuration
     */
    public abstract void factory(C configuration);

    /**
     * run application
     * @param args args
     */
    public abstract void run(String... args);

    /**
     * exit application
     * @param args args
     */
    public abstract void exit(String... args);

}
