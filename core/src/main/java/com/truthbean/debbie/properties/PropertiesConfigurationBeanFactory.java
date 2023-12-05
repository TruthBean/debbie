package com.truthbean.debbie.properties;

import com.truthbean.Logger;
import com.truthbean.LoggerFactory;
import com.truthbean.core.util.StringUtils;
import com.truthbean.debbie.bean.BeanFactory;
import com.truthbean.debbie.bean.BeanInjection;
import com.truthbean.debbie.bean.BeanType;
import com.truthbean.debbie.core.ApplicationContext;
import com.truthbean.debbie.environment.EnvironmentDepositoryHolder;
import com.truthbean.debbie.proxy.BeanProxyType;

import java.util.*;

/**
 * @author TruthBean
 * @since 0.5.3
 * Created on 2021/12/18 22:23.
 */
public class PropertiesConfigurationBeanFactory<Configuration extends DebbieConfiguration, Property extends DebbieProperties<Configuration>>
        implements BeanFactory<Configuration> {

    public static final String profileResourceKey = "config.profile";
    public static final String categoryResourceKey = "config.category";

    private final Set<String> names = new HashSet<>();
    private final Class<Configuration> configurationClass;
    private final Property property;

    public PropertiesConfigurationBeanFactory(Property property, Class<Configuration> configurationClass) {
        this.property = property;
        this.configurationClass = configurationClass;

        var className = configurationClass.getSimpleName();
        names.add(StringUtils.toFirstCharLowerCase(className));
        var profiles = property.getCategories();
        for (String profile : profiles) {
            names.add(profile + className);
        }
    }

    public Map<String, Configuration> factoryCategoryBean(String profile, ApplicationContext applicationContext) {
        return property.getCategoryConfigurationMap(profile, applicationContext);
    }

    @Override
    public Configuration factoryBean(ApplicationContext applicationContext) {
        return property.getConfiguration(applicationContext);
    }

    public Collection<Configuration> factoryBeans(ApplicationContext applicationContext) {
        Map<String, Map<String, Configuration>> configurationMap = property.getAllProfiledCategoryConfiguration(applicationContext);
        Map<String, Configuration> map = configurationMap.get(EnvironmentDepositoryHolder.DEFAULT_PROFILE);
        return map.values();
    }

    public Configuration factory(String profile, String category, String name, Class beanClass, BeanType type, BeanProxyType proxyType, ApplicationContext applicationContext) {
        if (!StringUtils.hasText(profile)) {
            profile = EnvironmentDepositoryHolder.DEFAULT_PROFILE;
        }
        if (!StringUtils.hasText(category)) {
            category = EnvironmentDepositoryHolder.DEFAULT_CATEGORY;
        }
        return property.getConfiguration(profile, category, applicationContext);
    }

    public Configuration factory(String profile, String category, ApplicationContext applicationContext) {
        if (!StringUtils.hasText(profile)) {
            profile = EnvironmentDepositoryHolder.DEFAULT_PROFILE;
        }
        if (!StringUtils.hasText(category)) {
            category = EnvironmentDepositoryHolder.DEFAULT_CATEGORY;
        }
        return property.getConfiguration(profile, category, applicationContext);
    }

    public Configuration factory(BeanInjection<Configuration> beanInjection, ApplicationContext applicationContext) {
        String profile = (String) beanInjection.getResource(profileResourceKey);
        String category = (String) beanInjection.getResource(categoryResourceKey);
        return factory(profile, category, applicationContext);
    }

    @Override
    public Set<String> getAllName() {
        return names;
    }

    @Override
    public BeanProxyType getBeanProxyType() {
        return BeanProxyType.NO;
    }

    @Override
    public boolean isCreated() {
        return false;
    }

    @Override
    public Configuration getCreatedBean() {
        return null;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

    @Override
    public Class<?> getBeanClass() {
        return configurationClass;
    }

    @Override
    public int hashCode() {
        return getHashCode(super.hashCode());
    }

    @Override
    public boolean equals(Object obj) {
        return isEquals(obj);
    }

    @Override
    public void destruct(ApplicationContext applicationContext) {
        try {
            property.close();
        } catch (Exception e) {
            LOGGER.error("", e);
        }
    }

    @Override
    public String toString() {
        return "\"PropertiesConfigurationBeanFactory\":{" + "\"names\":" + names + "," + "\"configurationClass\":" + configurationClass + "," + "\"property\":" + property + '}';
    }

    @Override
    public Logger getLogger() {
        return LOGGER;
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(PropertiesConfigurationBeanFactory.class);
}
