package com.truthbean.debbie.environment;

import com.truthbean.Logger;
import com.truthbean.LoggerFactory;

import java.util.Properties;

/**
 * @author TruthBean/Rogar·Q
 * @since 0.5.5
 */
public class EmptyEnvironment implements ProfiledEnvironment {
    private final Properties properties = new Properties();

    @Override
    public Properties properties() {
        return properties;
    }

    @Override
    public Logger getLogger() {
        return LOGGER;
    }

    @Override
    public void clear() {
        properties.clear();
    }

    @Override
    public int priority() {
        return Integer.MIN_VALUE;
    }

    @Override
    public String profile() {
        return "empty";
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(EmptyEnvironment.class);
}
