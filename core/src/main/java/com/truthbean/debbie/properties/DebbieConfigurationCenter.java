/**
 * Copyright (c) 2021 TruthBean(Rogar·Q)
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
import com.truthbean.debbie.core.ApplicationContext;
import com.truthbean.debbie.core.ApplicationContextAware;
import com.truthbean.debbie.env.EnvironmentContent;
import com.truthbean.debbie.env.EnvironmentContentHolder;
import com.truthbean.debbie.proxy.BeanProxyType;
import com.truthbean.debbie.reflection.ReflectionHelper;
import com.truthbean.common.mini.util.StringUtils;
import com.truthbean.LoggerFactory;

import java.util.*;

/**
 * @author TruthBean
 * @since 0.0.1
 */
public class DebbieConfigurationCenter implements ApplicationContextAware {

    @SuppressWarnings({"rawtypes"})
    // TODO
    private static final Map<Class<? extends DebbieProperties>, Set<DebbieConfiguration>> configurations = new HashMap<>();
    private static final String PROPERTIES_NAME = "debbie.properties.class-name";

    private ApplicationContext applicationContext;
    private BeanInitialization beanInitialization;

    public DebbieConfigurationCenter() {
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
        this.beanInitialization = applicationContext.getBeanInitialization();
    }

    public static <C extends DebbieConfiguration> void addConfiguration(Class<C> configurationClass, C configuration,
                                                                        BeanInitialization beanInitialization) {
        DebbieBeanInfo<C> beanInfo = new DebbieBeanInfo<>(configurationClass);
        beanInfo.setBean(configuration);
        beanInfo.setBeanType(BeanType.SINGLETON);
        beanInfo.setBeanProxyType(BeanProxyType.JDK);
        String name = configuration.getName();
        if (!StringUtils.hasText(name) || "default".equals(name)) {
            var beanName = StringUtils.toFirstCharLowerCase(configurationClass.getName());
            beanInfo.addBeanName(beanName);
        }
        if (StringUtils.hasText(name)) {
            beanInfo.addBeanName(name);
        }
        // beanInfo.addProperty(PROPERTIES_NAME);
        beanInitialization.initBean(beanInfo);
    }

    public <C extends DebbieConfiguration> void addConfiguration(Class<C> configurationClass, C configuration) {
        addConfiguration(configurationClass, configuration, beanInitialization);
    }

    public <P extends DebbieProperties<S>, C extends DebbieConfiguration, S extends C>
    void register(Class<P> propertiesClass, Class<C> configurationClass) {
        // TODO 不用反射的形式....
        DebbieProperties<S> properties = ReflectionHelper.newInstance(propertiesClass);
        C configuration = properties.toConfiguration(applicationContext);
        Set<DebbieConfiguration> list;
        if (configurations.containsKey(propertiesClass)) {
            list = configurations.get(propertiesClass);
        } else {
            list = new HashSet<>();
        }
        list.add(configuration);
        configurations.put(propertiesClass, list);
        addConfiguration(configurationClass, configuration, beanInitialization);
        applicationContext.refreshBeans();
    }

    @SuppressWarnings("unchecked")
    public <P extends DebbieProperties<S>, C extends DebbieConfiguration, S extends C>
    void register(P properties, Class<C> configurationClass) {
        var propertiesClass = (Class<? extends DebbieProperties<S>>) properties.getClass();
        C configuration = properties.toConfiguration(applicationContext);
        Set<DebbieConfiguration> list;
        if (configurations.containsKey(propertiesClass)) {
            list = configurations.get(propertiesClass);
        } else {
            list = new HashSet<>();
        }
        list.add(configuration);
        configurations.put(propertiesClass, list);
        addConfiguration(configurationClass, configuration, beanInitialization);
        applicationContext.refreshBeans();
    }

    public <P extends DebbieProperties<S>, C extends DebbieConfiguration, S extends C>
    void register(Class<P> propertiesClass, Class<C> configurationClass, C configuration) {
        Set<DebbieConfiguration> list;
        if (configurations.containsKey(propertiesClass)) {
            list = configurations.get(propertiesClass);
        } else {
            list = new HashSet<>();
        }
        list.add(configuration);
        configurations.put(propertiesClass, list);
        addConfiguration(configurationClass, configuration, beanInitialization);
        applicationContext.refreshBeans();
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public <P extends EnvironmentContent, C extends BeanScanConfiguration>
    C getConfigurationBySuperClassOrPropertiesClass(Class<C> superConfigurationClass, Class<P> propertiesClass,
                                                    ApplicationContext applicationContext) {
        if (configurations.isEmpty()) {
            return null;
        }
        if (configurations.containsKey(propertiesClass)) {
            return (C) configurations.get(propertiesClass);
        }
        for (Map.Entry<Class<? extends DebbieProperties>, Set<DebbieConfiguration>> classObjectEntry : configurations.entrySet()) {
            var key = classObjectEntry.getKey();
            var list = classObjectEntry.getValue();
            for (DebbieConfiguration value : list) {
                if (superConfigurationClass.isAssignableFrom(key)) {
                    LOGGER.debug(() -> "configuration class: " + key.getName());
                    return (C) value;
                }
                if (superConfigurationClass == value.getClass()) {
                    LOGGER.debug(() -> "configuration class: " + key.getName());
                    return (C) value;
                }
            }
        }
        return null;
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public <C extends DebbieConfiguration> C factory(Class<C> configurationClass, ApplicationContext applicationContext) {
        if (configurations.isEmpty()) {
            return null;
        }
        for (Map.Entry<Class<? extends DebbieProperties>, Set<DebbieConfiguration>> classObjectEntry : configurations.entrySet()) {
            var key = classObjectEntry.getKey();
            var value = classObjectEntry.getValue();
            for (DebbieConfiguration v : value) {
                if (configurationClass == v.getClass()) {
                    LOGGER.debug(() -> "configuration class: " + key.getName());
                    return (C) v;
                }
            }
        }
        return beanInitialization.getRegisterBean(configurationClass);
    }

    public static <C extends DebbieConfiguration> C factoryConfiguration(Class<C> configurationClass, ApplicationContext applicationContext) {
        return applicationContext.getBeanInitialization().getRegisterBean(configurationClass);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public <C extends DebbieConfiguration> Set<C> getConfigurations(Class<C> configurationClass, ApplicationContext applicationContext) {
        Set<C> result = new HashSet<>();
        if (configurations.isEmpty()) {
            return new HashSet<>();
        }
        for (Map.Entry<Class<? extends DebbieProperties>, Set<DebbieConfiguration>> classObjectEntry : configurations.entrySet()) {
            var key = classObjectEntry.getKey();
            Set<DebbieConfiguration> values = classObjectEntry.getValue();
            for (DebbieConfiguration value : values) {
                if (configurationClass == value.getClass()) {
                    LOGGER.debug(() -> "properties class: " + key.getName() + ", configuration class: " + value.getClass());
                    result.add((C) value);
                }
            }
        }
        return result;
    }

    @SuppressWarnings("unchecked")
    public <P extends EnvironmentContent, C extends BeanScanConfiguration>
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
        Collection<Set<DebbieConfiguration>> values = configurations.values();
        if (!values.isEmpty()) {
            for (Set<DebbieConfiguration> list : values) {
                for (DebbieConfiguration value : list) {
                    if (configurationClass == value.getClass()) {
                        return (C) value;
                    }
                }
            }
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    public static <C extends DebbieConfiguration> C getConfiguration(Class<C> configurationClass, String name) {
        Collection<Set<DebbieConfiguration>> values = configurations.values();
        if (!values.isEmpty()) {
            for (Set<DebbieConfiguration> list : values) {
                for (DebbieConfiguration value : list) {
                    if (configurationClass == value.getClass() && name.equals(value.getName())) {
                        return (C) value;
                    }
                }
            }
        }
        return null;
    }

    public void reset() {
        configurations.forEach((properties, configurations) -> configurations.forEach(DebbieConfiguration::reset));
        configurations.clear();
        EnvironmentContentHolder.clear();
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(DebbieConfigurationCenter.class);
}
