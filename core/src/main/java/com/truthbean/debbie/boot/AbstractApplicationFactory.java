package com.truthbean.debbie.boot;

import com.truthbean.debbie.bean.BeanFactoryHandler;
import com.truthbean.debbie.properties.DebbieConfigurationFactory;

/**
 * @author TruthBean
 * @since 0.0.1
 */
public abstract class AbstractApplicationFactory {

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
     * @param factory configurationFactory
     * @see DebbieConfigurationFactory
     * @param beanFactoryHandler beanFactoryHandler
     * @see BeanFactoryHandler
     * @return DebbieApplication
     */
    public abstract DebbieApplication factory(DebbieConfigurationFactory factory, BeanFactoryHandler beanFactoryHandler);

}
