package com.truthbean.debbie.properties;

import com.truthbean.debbie.bean.BeanFactoryHandler;
import com.truthbean.debbie.bean.BeanScanConfiguration;
import com.truthbean.debbie.bean.DebbieConfigurationCenter;
import com.truthbean.debbie.bean.SingletonBeanRegister;
import com.truthbean.debbie.reflection.ReflectionHelper;
import com.truthbean.debbie.util.StringUtils;
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

    private final Map<Class<? extends DebbieProperties>, DebbieConfiguration> configurations = new HashMap<>();

    private final BeanFactoryHandler factoryHandler;
    private final SingletonBeanRegister singletonBeanRegister;

    public DebbieConfigurationFactory(BeanFactoryHandler beanFactoryHandler) {
        this.factoryHandler = beanFactoryHandler;
        this.singletonBeanRegister = new SingletonBeanRegister(beanFactoryHandler);
    }

    @SuppressWarnings("unchecked")
    public <P extends DebbieProperties, C extends DebbieConfiguration>
    void register(Class<P> propertiesClass, Class<C> configurationClass) {
        DebbieProperties<C> properties = (DebbieProperties<C>) ReflectionHelper.newInstance(propertiesClass);
        C configuration = properties.toConfiguration(factoryHandler);
        var beanName = StringUtils.toFirstCharLowerCase(configurationClass.getName());
        this.singletonBeanRegister.registerSingletonBean(configuration, configurationClass, beanName);
        configurations.put(propertiesClass, configuration);
        DebbieConfigurationCenter.addConfiguration(configuration);
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
        configurations.forEach((properties, configurations) -> {
            configurations.reset();
        });
        configurations.clear();
        BaseProperties.reset();
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(DebbieConfigurationFactory.class);
}
