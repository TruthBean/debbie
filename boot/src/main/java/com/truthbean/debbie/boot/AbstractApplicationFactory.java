package com.truthbean.debbie.boot;

import com.truthbean.debbie.core.bean.BeanScanConfiguration;

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
     *
     * @return DebbieApplication
     */
    public abstract DebbieApplication factory(C configuration);

}
