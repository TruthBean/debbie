package com.truthbean.debbie.test;

import com.truthbean.Logger;
import com.truthbean.debbie.environment.EnvironmentContentLoggerSetter;
import com.truthbean.debbie.environment.EnvironmentSpi;

import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @author TruthBean/Rogar·Q
 * @since 0.6.2
 */
public class DebbieTestEnvironment implements EnvironmentSpi, EnvironmentContentLoggerSetter {
    private Logger logger;
    private static final ConcurrentMap<String, String> CACHE = new ConcurrentHashMap<>();

    @Override
    public int priority() {
        return Integer.MAX_VALUE;
    }

    @Override
    public String profile() {
        return "test";
    }

    static void addCache(Map<String, String> map) {
        if (map != null && !map.isEmpty()) CACHE.putAll(map);
    }

    @Override
    public Properties properties() {
        Properties properties = new Properties();
        if (!CACHE.isEmpty()) properties.putAll(CACHE);
        return properties;
    }

    @Override
    public DebbieTestEnvironment setLogger(Logger logger) {
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
