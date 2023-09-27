package com.truthbean.debbie.properties;

import com.truthbean.Logger;
import com.truthbean.LoggerFactory;
import com.truthbean.core.util.StringUtils;
import com.truthbean.debbie.bean.BeanFactory;
import com.truthbean.debbie.bean.BeanType;
import com.truthbean.debbie.core.ApplicationContext;
import com.truthbean.debbie.environment.EnvironmentDepositoryHolder;
import com.truthbean.debbie.proxy.BeanProxyType;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author TruthBean
 * @since 0.5.3
 * Created on 2021/12/18 22:23.
 */
public class PropertiesConfigurationBeanFactory<Configuration extends DebbieConfiguration, Property extends DebbieProperties<Configuration>>
        implements BeanFactory<Configuration> {

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

    @Override
    public Configuration factoryNamedBean(String name, ApplicationContext applicationContext) {
        String simpleName = configurationClass.getSimpleName();
        var defaultConfigurationName = "default" + simpleName;
        var configurationName = StringUtils.toFirstCharLowerCase(simpleName);
        if (!StringUtils.hasText(name) || name.equals(defaultConfigurationName) || name.equals(configurationName)) {
            return property.getConfiguration(EnvironmentDepositoryHolder.DEFAULT_PROFILE, EnvironmentDepositoryHolder.DEFAULT_CATEGORY, applicationContext);
        }
        if (name.endsWith(simpleName)) {
            var newName = name.substring(0, name.length() - simpleName.length());
            return property.getConfiguration(newName, EnvironmentDepositoryHolder.DEFAULT_CATEGORY, applicationContext);
        }
        return property.getConfiguration(name, EnvironmentDepositoryHolder.DEFAULT_CATEGORY, applicationContext);
    }

    @Override
    public Configuration factory(String profile, String category, String name, Class beanClass, BeanType type, BeanProxyType proxyType, ApplicationContext applicationContext) {
        if (StringUtils.hasText(profile)) {
            profile = EnvironmentDepositoryHolder.DEFAULT_PROFILE;
        }
        if (StringUtils.hasText(category)) {
            category = EnvironmentDepositoryHolder.DEFAULT_CATEGORY;
        }
        return property.getConfiguration(profile, category, applicationContext);
    }

    @Override
    public Set<String> getBeanNames() {
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
