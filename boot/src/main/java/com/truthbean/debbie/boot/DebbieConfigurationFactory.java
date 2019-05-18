package com.truthbean.debbie.boot;

import com.truthbean.debbie.core.properties.BaseProperties;
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

    private static void loadConfiguration(ClassLoader classLoader) {
        Map<Class<BaseProperties>, Class> classClassMap = SpiLoader.loadPropertiesClasses(classLoader);
        classClassMap.forEach((key, value) -> {
            Object configuration = ReflectionHelper.invokeStaticMethod("toConfiguration", key);
            // should not be null
            assert configuration != null;
            configurations.put(configuration.getClass(), configuration);
        });
    }

    public static <C extends DebbieConfiguration> C factory(Class<C> configurationClass) {
        if (configurations.isEmpty()) {
            var classLoader = ClassLoaderUtils.getDefaultClassLoader();
            loadConfiguration(classLoader);
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
