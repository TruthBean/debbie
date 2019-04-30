package com.truthbean.debbie.boot;

import com.truthbean.debbie.core.properties.AbstractProperties;
import com.truthbean.debbie.core.properties.DebbieConfiguration;
import com.truthbean.debbie.core.reflection.ClassLoaderUtils;
import com.truthbean.debbie.core.reflection.ReflectionHelper;
import com.truthbean.debbie.core.spi.SpiLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * @author TruthBean
 * @since 0.0.1
 */
public class DebbieConfigurationFactory {

    private static Map<Class<?>, Object> configurations = new HashMap<>();

    private static void loadConfiguration() {
        Map<Class<AbstractProperties>, Class> classClassMap = SpiLoader.loadPropertiesClasses();
        classClassMap.forEach((key, value) -> {
            System.out.println("...........");
            System.out.println(key);
            Object configuration = ReflectionHelper.invokeStaticMethod("toConfiguration", key);
            // should not be null
            assert configuration != null;
            configurations.put(configuration.getClass(), configuration);
        });
    }

    public static <C extends DebbieConfiguration> C factory(Class<C> configurationClass) {
        if (configurations.isEmpty()) {
            loadConfiguration();
        }
        for (Map.Entry<Class<?>, Object> classObjectEntry : configurations.entrySet()) {
            var key = classObjectEntry.getKey();
            var value = classObjectEntry.getValue();
            if (configurationClass.isAssignableFrom(key)) {
                LOGGER.debug("configuration class: " + key.getName());
                return (C) value;
            }
        }
        return null;
    }

    public static <C extends AbstractServerConfiguration> C factoryServer() {
        return (C) factory(AbstractServerConfiguration.class);
    }

    public static <C extends DebbieConfiguration> C factoryDataSources() {
        ClassLoader classLoader = ClassLoaderUtils.getClassLoader(DebbieConfigurationFactory.class);
        var dataSourcePropertiesClassName = "com.truthbean.debbie.jdbc.datasource.DataSourceProperties";
        try {
            Class<C> dataSourcePropertiesClass = (Class<C>) classLoader.loadClass(dataSourcePropertiesClassName);
            return factory(dataSourcePropertiesClass);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(DebbieConfigurationFactory.class);
}
