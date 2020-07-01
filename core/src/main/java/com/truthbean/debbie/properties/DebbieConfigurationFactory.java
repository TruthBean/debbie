/**
 * Copyright (c) 2020 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *         http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.properties;

import com.truthbean.debbie.bean.*;
import com.truthbean.debbie.reflection.ReflectionHelper;
import com.truthbean.debbie.util.StringUtils;
import com.truthbean.Logger;
import com.truthbean.logger.LoggerFactory;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author TruthBean
 * @since 0.0.1
 */
public class DebbieConfigurationFactory implements DebbieApplicationContextAware {

    @SuppressWarnings({"rawtypes"})
    private static final Map<Class<? extends DebbieProperties>, DebbieConfiguration> configurations = new HashMap<>();

    private DebbieApplicationContext factoryHandler;
    private SingletonBeanRegister singletonBeanRegister;

    public DebbieConfigurationFactory() {
    }

    @Override
    public void setDebbieApplicationContext(DebbieApplicationContext applicationContext) {
        this.factoryHandler = applicationContext;
        this.singletonBeanRegister = new SingletonBeanRegister(applicationContext);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
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
                                                    DebbieApplicationContext applicationContext) {
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
                LOGGER.debug(() -> "configuration class: " + key.getName());
                return (C) value;
            }
            if (superConfigurationClass == value.getClass()) {
                LOGGER.debug(() -> "configuration class: " + key.getName());
                return (C) value;
            }
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    public <C extends DebbieConfiguration> C factory(Class<C> configurationClass, DebbieApplicationContext applicationContext) {
        if (configurations.isEmpty()) {
            return null;
        }
        for (Map.Entry<Class<? extends DebbieProperties>, DebbieConfiguration> classObjectEntry : configurations.entrySet()) {
            var key = classObjectEntry.getKey();
            var value = classObjectEntry.getValue();
            if (configurationClass == value.getClass()) {
                LOGGER.debug(() -> "configuration class: " + key.getName());
                return (C) value;
            }
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    public <C extends DebbieConfiguration> Set<C> getConfigurations(Class<C> configurationClass, DebbieApplicationContext applicationContext) {
        Set<C> result = new HashSet<>();
        if (configurations.isEmpty()) {
            return null;
        }
        for (Map.Entry<Class<? extends DebbieProperties>, DebbieConfiguration> classObjectEntry : configurations.entrySet()) {
            var key = classObjectEntry.getKey();
            DebbieConfiguration value = classObjectEntry.getValue();
            if (configurationClass == value.getClass()) {
                LOGGER.debug(() -> "properties class: " + key.getName() + ", configuration class: " + value.getClass());
                result.add((C) value);
            }
        }
        return result;
    }

    @SuppressWarnings("unchecked")
    public <P extends BaseProperties, C extends BeanScanConfiguration>
    C factory(Class<C> configurationClass, Class<P> propertiesClass, DebbieApplicationContext applicationContext) {
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
                LOGGER.debug(() -> "configuration class: " + key.getName());
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
