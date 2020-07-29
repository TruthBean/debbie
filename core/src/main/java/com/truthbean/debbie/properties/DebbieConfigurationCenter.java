/**
 * Copyright (c) 2020 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 * http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.properties;

import com.truthbean.Logger;
import com.truthbean.debbie.bean.BeanInitialization;
import com.truthbean.debbie.bean.BeanScanConfiguration;
import com.truthbean.debbie.bean.BeanType;
import com.truthbean.debbie.bean.DebbieBeanInfo;
import com.truthbean.debbie.bean.SingletonBeanRegister;
import com.truthbean.debbie.core.ApplicationContext;
import com.truthbean.debbie.core.ApplicationContextAware;
import com.truthbean.debbie.properties.BaseProperties;
import com.truthbean.debbie.properties.DebbieConfiguration;
import com.truthbean.debbie.properties.DebbieProperties;
import com.truthbean.debbie.reflection.ReflectionHelper;
import com.truthbean.debbie.util.StringUtils;
import com.truthbean.logger.LoggerFactory;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author TruthBean
 * @since 0.0.1
 */
public class DebbieConfigurationCenter implements ApplicationContextAware {

    @SuppressWarnings({"rawtypes"})
    private static final Map<Class<? extends DebbieProperties>, DebbieConfiguration> configurations = new HashMap<>();

    private ApplicationContext applicationContext;
    private SingletonBeanRegister singletonBeanRegister;

    public DebbieConfigurationCenter() {
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
        this.singletonBeanRegister = new SingletonBeanRegister(applicationContext);
    }

    public static <C extends DebbieConfiguration> void addConfiguration(Class<C> configurationClass, C configuration,
                                                                        BeanInitialization beanInitialization) {
        var beanName = StringUtils.toFirstCharLowerCase(configurationClass.getName());
        DebbieBeanInfo<C> beanInfo = new DebbieBeanInfo<>(configurationClass);
        beanInfo.setBean(configuration);
        beanInfo.setBeanType(BeanType.SINGLETON);
        beanInfo.addBeanName(beanName);
        beanInitialization.initSingletonBean(beanInfo);
    }

    public <C extends DebbieConfiguration> void addConfiguration(Class<C> configurationClass, C configuration) {
        var beanName = StringUtils.toFirstCharLowerCase(configurationClass.getName());
        singletonBeanRegister.registerSingletonBean(configuration, configurationClass, beanName);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public <P extends DebbieProperties, C extends DebbieConfiguration>
    void register(Class<P> propertiesClass, Class<C> configurationClass) {
        DebbieProperties<C> properties = (DebbieProperties<C>) ReflectionHelper.newInstance(propertiesClass);
        C configuration = properties.toConfiguration(applicationContext);
        var beanName = StringUtils.toFirstCharLowerCase(configurationClass.getName());
        this.singletonBeanRegister.registerSingletonBean(configuration, configurationClass, beanName);
        configurations.put(propertiesClass, configuration);
        applicationContext.refreshBeans();
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public <P extends BaseProperties, C extends BeanScanConfiguration>
    C getConfigurationBySuperClassOrPropertiesClass(Class<C> superConfigurationClass, Class<P> propertiesClass,
                                                    ApplicationContext applicationContext) {
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

    @SuppressWarnings({"unchecked", "rawtypes"})
    public <C extends DebbieConfiguration> C factory(Class<C> configurationClass, ApplicationContext applicationContext) {
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

    @SuppressWarnings({"unchecked", "rawtypes"})
    public <C extends DebbieConfiguration> Set<C> getConfigurations(Class<C> configurationClass, ApplicationContext applicationContext) {
        Set<C> result = new HashSet<>();
        if (configurations.isEmpty()) {
            return new HashSet<>();
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
    C factory(Class<C> configurationClass, Class<P> propertiesClass, ApplicationContext applicationContext) {
        if (configurations.isEmpty()) {
            return null;
        }
        if (configurations.containsKey(propertiesClass)) {
            return (C) configurations.get(propertiesClass);
        }
        return factory(configurationClass, applicationContext);
    }

    @SuppressWarnings("unchecked")
    public static <C extends DebbieConfiguration> C getConfiguration(Class<C> configurationClass) {
        Collection<DebbieConfiguration> values = configurations.values();
        if (!values.isEmpty()) {
            for (DebbieConfiguration value : values) {
                if (configurationClass == value.getClass()) {
                    return (C) value;
                }
            }
        }
        return null;
    }

    public void reset() {
        configurations.forEach((properties, configurations) -> configurations.reset());
        configurations.clear();
        BaseProperties.reset();
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(DebbieConfigurationCenter.class);
}
