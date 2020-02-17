package com.truthbean.debbie.bean;

import com.truthbean.debbie.properties.DebbieConfiguration;

import java.util.HashMap;
import java.util.Map;

/**
 * @author truthbean/Rogar·Q
 * @since 0.0.2
 */
public class DebbieConfigurationCenter {
    private static Map<Class<? extends DebbieConfiguration>, DebbieConfiguration> configurations = new HashMap<>();

    public static <C extends DebbieConfiguration> void addConfiguration(C configuration) {
        configurations.put(configuration.getClass(), configuration);
    }

    @SuppressWarnings("unchecked")
    public static <C extends DebbieConfiguration> C getConfiguration(Class<C> configurationClass) {
        return (C) configurations.get(configurationClass);
    }

    public static void clear() {
        configurations.clear();
    }
}
