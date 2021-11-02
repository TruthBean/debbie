package com.truthbean.debbie.env;

import com.truthbean.Logger;

import java.util.Properties;

/**
 * @author TruthBean
 * @since 0.5.3
 */
public class JvmEnvironmentContent implements EnvironmentContent, EnvironmentContentLoggerSetter {

    private Logger logger;

    @Override
    public int getPriority() {
        return 1;
    }

    @Override
    public String getProfile() {
        return EnvironmentContentProfile.JVM;
    }

    @Override
    public Properties getProperties() {
        Properties properties = new Properties();
        properties.putAll(System.getProperties());
        return properties;
    }

    public EnvironmentContent setLogger(Logger logger) {
        this.logger = logger;
        return this;
    }

    @Override
    public Logger getLogger() {
        return logger;
    }

}
