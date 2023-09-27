package com.truthbean.debbie.environment;

import com.truthbean.Logger;

import java.util.Properties;

/**
 * @author TruthBean/Rogar·Q
 * @since 0.5.5
 */
public class EnvEnvironment implements ProfiledEnvironment, EnvironmentContentLoggerSetter {

    private Logger logger;

    private Properties cache;

    @Override
    public Properties properties() {
        if (cache != null) {
            return cache;
        } else {
            cache = new Properties();
            cache.putAll(System.getenv());
        }
        return new Properties();
    }

    @Override
    public Logger getLogger() {
        return logger;
    }

    @Override
    public void clear() {
    }

    @Override
    public int priority() {
        return 2;
    }

    @Override
    public String profile() {
        return EnvironmentDepository.ENV;
    }

    @Override
    public Environment setLogger(Logger logger) {
        this.logger = logger;
        return this;
    }
}
