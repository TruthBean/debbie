package com.truthbean.debbie.properties;

import com.truthbean.debbie.bean.BeanFactoryHandler;
import com.truthbean.debbie.bean.BeanScanConfiguration;
import com.truthbean.debbie.reflection.ClassLoaderUtils;
import com.truthbean.debbie.reflection.ReflectionHelper;
import com.truthbean.debbie.spi.SpiLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author TruthBean
 * @since 0.0.1
 */
public class DebbieConfigurationFactory {

    private Map<Class<? extends DebbieProperties>, DebbieConfiguration> configurations = new HashMap<>();

    private void loadConfiguration(ClassLoader classLoader, BeanFactoryHandler beanFactoryHandler) {
        Map<Class<DebbieProperties>, Class<DebbieConfiguration>> classClassMap = SpiLoader.loadPropertiesClasses(classLoader);
        if (classClassMap != null && !classClassMap.isEmpty()) {
            classClassMap.forEach((key, value) -> {
                DebbieProperties properties = ReflectionHelper.newInstance(key);
                // should not be null
                assert properties != null;
                configurations.put(key, properties.toConfiguration(beanFactoryHandler));
            });
        }
    }

    public <C extends DebbieConfiguration> C factory(Class<C> configurationClass, BeanFactoryHandler beanFactoryHandler) {
        if (configurations.isEmpty()) {
            var classLoader = ClassLoaderUtils.getDefaultClassLoader();
            loadConfiguration(classLoader, beanFactoryHandler);
        }
        for (Map.Entry<Class<? extends DebbieProperties>, DebbieConfiguration> classObjectEntry : configurations.entrySet()) {
            var key = classObjectEntry.getKey();
            var value = classObjectEntry.getValue();
            if (configurationClass == value.getClass()) {
                LOGGER.debug("configuration class: " + key.getName());
                return (C) value;
            }
            /*if (configurationClass.isAssignableFrom(value.getClass())) {
                LOGGER.debug("configuration class: " + key.getName());
                return (C) value;
            }*/
        }
        return null;
    }

    public <C extends DebbieConfiguration> Set<C> getConfigurations(Class<C> configurationClass, BeanFactoryHandler beanFactoryHandler) {
        Set<C> result = new HashSet<>();
        if (configurations.isEmpty()) {
            var classLoader = ClassLoaderUtils.getDefaultClassLoader();
            loadConfiguration(classLoader, beanFactoryHandler);
        }
        for (Map.Entry<Class<? extends DebbieProperties>, DebbieConfiguration> classObjectEntry : configurations.entrySet()) {
            var key = classObjectEntry.getKey();
            DebbieConfiguration value = classObjectEntry.getValue();
            /*if (configurationClass.isAssignableFrom(value.getClass())) {
                LOGGER.debug("configuration class: " + key.getName());
                result.add((C) value);
            }*/
            if (configurationClass == value.getClass()) {
                LOGGER.debug("properties class: " + key.getName() + ", configuration class: " + value.getClass());
                result.add((C) value);
            }
        }
        return result;
    }

    public <P extends BaseProperties, C extends BeanScanConfiguration>
    C factory(Class<C> configurationClass, Class<P> propertiesClass, BeanFactoryHandler beanFactoryHandler) {
        if (configurations.isEmpty()) {
            var classLoader = ClassLoaderUtils.getDefaultClassLoader();
            loadConfiguration(classLoader, beanFactoryHandler);
        }
        if (configurations.containsKey(propertiesClass)) {
            return (C) configurations.get(propertiesClass);
        }
        for (Map.Entry<Class<? extends DebbieProperties>, DebbieConfiguration> classObjectEntry : configurations.entrySet()) {
            var key = classObjectEntry.getKey();
            var value = classObjectEntry.getValue();
            /*if (configurationClass.isAssignableFrom(key)) {
                LOGGER.debug("configuration class: " + key.getName());
                return (C) value;
            }*/
            if (configurationClass == value.getClass()) {
                LOGGER.debug("configuration class: " + key.getName());
                return (C) value;
            }
        }
        return null;
    }

    public void reset() {
        configurations.clear();
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(DebbieConfigurationFactory.class);
}
