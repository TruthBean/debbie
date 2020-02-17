package com.truthbean.debbie.properties;

import com.truthbean.debbie.bean.BeanFactoryHandler;
import com.truthbean.debbie.bean.BeanScanConfiguration;
import com.truthbean.debbie.bean.DebbieConfigurationCenter;
import com.truthbean.debbie.reflection.ReflectionHelper;
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

    private final BeanFactoryHandler factoryHandler;

    public DebbieConfigurationFactory(BeanFactoryHandler beanFactoryHandler) {
        this.factoryHandler = beanFactoryHandler;
    }

    public void register(Class<? extends DebbieProperties> propertiesClass) {
        DebbieProperties properties = ReflectionHelper.newInstance(propertiesClass);
        DebbieConfiguration debbieConfiguration = properties.toConfiguration(factoryHandler);
        configurations.put(propertiesClass, debbieConfiguration);
        DebbieConfigurationCenter.addConfiguration(debbieConfiguration);
    }

    @SuppressWarnings("unchecked")
    public <P extends BaseProperties, C extends BeanScanConfiguration>
    C getConfigurationBySuperClassOrPropertiesClass(Class<C> superConfigurationClass, Class<P> propertiesClass,
                                                    BeanFactoryHandler beanFactoryHandler) {
        if (configurations.isEmpty()) {
            return null;
        }
        if (configurations.containsKey(propertiesClass)) {
            return (C) configurations.get(propertiesClass);
        }
        for (Map.Entry<Class<? extends DebbieProperties>, DebbieConfiguration> classObjectEntry : configurations.entrySet()) {
            var key = classObjectEntry.getKey();
            var value = classObjectEntry.getValue();
            if (superConfigurationClass.isAssignableFrom(key)) {
                LOGGER.debug("configuration class: " + key.getName());
                return (C) value;
            }
            if (superConfigurationClass == value.getClass()) {
                LOGGER.debug("configuration class: " + key.getName());
                return (C) value;
            }
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    public <C extends DebbieConfiguration> C factory(Class<C> configurationClass, BeanFactoryHandler beanFactoryHandler) {
        if (configurations.isEmpty()) {
            return null;
        }
        for (Map.Entry<Class<? extends DebbieProperties>, DebbieConfiguration> classObjectEntry : configurations.entrySet()) {
            var key = classObjectEntry.getKey();
            var value = classObjectEntry.getValue();
            if (configurationClass == value.getClass()) {
                LOGGER.debug("configuration class: " + key.getName());
                return (C) value;
            }
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    public <C extends DebbieConfiguration> Set<C> getConfigurations(Class<C> configurationClass, BeanFactoryHandler beanFactoryHandler) {
        Set<C> result = new HashSet<>();
        if (configurations.isEmpty()) {
            return null;
        }
        for (Map.Entry<Class<? extends DebbieProperties>, DebbieConfiguration> classObjectEntry : configurations.entrySet()) {
            var key = classObjectEntry.getKey();
            DebbieConfiguration value = classObjectEntry.getValue();
            if (configurationClass == value.getClass()) {
                LOGGER.debug("properties class: " + key.getName() + ", configuration class: " + value.getClass());
                result.add((C) value);
            }
        }
        return result;
    }

    @SuppressWarnings("unchecked")
    public <P extends BaseProperties, C extends BeanScanConfiguration>
    C factory(Class<C> configurationClass, Class<P> propertiesClass, BeanFactoryHandler beanFactoryHandler) {
        if (configurations.isEmpty()) {
            return null;
        }
        if (configurations.containsKey(propertiesClass)) {
            return (C) configurations.get(propertiesClass);
        }
        for (Map.Entry<Class<? extends DebbieProperties>, DebbieConfiguration> classObjectEntry : configurations.entrySet()) {
            var key = classObjectEntry.getKey();
            var value = classObjectEntry.getValue();
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
