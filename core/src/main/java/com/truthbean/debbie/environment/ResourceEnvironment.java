package com.truthbean.debbie.environment;

import java.util.Map;
import java.util.Properties;

/**
 * @author TruthBean
 * @since 0.5.3
 */
public interface ResourceEnvironment extends ProfiledEnvironment, EnvironmentContentLoggerSetter {

    Properties load(String resourceUri);

    /**
     * load resources
     */
    Map<String, ProfiledEnvironment> loadResources();

    @Override
    void clear();

    default Environment getEnvironment() {
        return this;
    }
}
