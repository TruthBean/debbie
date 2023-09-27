package com.truthbean.debbie.environment;

import com.truthbean.Logger;

import java.util.Properties;

/**
 * @author TruthBean
 * @since 0.5.3
 */
public class SystemEnvironment implements ProfiledEnvironment, EnvironmentContentLoggerSetter {

    private Logger logger;

    @Override
    public int priority() {
        return 0;
    }

    @Override
    public String profile() {
        return EnvironmentDepository.SYSTEM;
    }

    @Override
    public Properties properties() {
        Properties properties = new Properties();
        properties.putAll(System.getProperties());
        return properties;
    }

    @Override
    public Environment setLogger(Logger logger) {
        this.logger = logger;
        return this;
    }

    @Override
    public Logger getLogger() {
        return logger;
    }

    @Override
    public void clear() {
    }

}
