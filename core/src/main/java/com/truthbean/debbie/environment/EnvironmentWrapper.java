package com.truthbean.debbie.environment;

import com.truthbean.Logger;
import com.truthbean.LoggerFactory;

import java.util.Properties;

/**
 * @author TruthBean
 * @since 0.5.5
 * Created on 2022/04/08 14:32.
 */
public record EnvironmentWrapper(Properties properties, String profile, int priority) implements ProfiledEnvironment {

    public Environment getEnvironment() {
        return this;
    }

    @Override
    public Logger getLogger() {
        return LOGGER;
    }

    @Override
    public void clear() {
        properties.clear();
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(EnvironmentWrapper.class);
}
