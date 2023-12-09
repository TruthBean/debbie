package com.truthbean.debbie.jdbc.datasource;

import com.truthbean.debbie.bean.BeanFactory;
import com.truthbean.debbie.bean.GlobalBeanFactory;
import com.truthbean.debbie.core.ApplicationContext;
import com.truthbean.debbie.environment.EnvironmentDepositoryHolder;

import java.util.HashSet;
import java.util.Set;

/**
 * @author TruthBean/Rogar·Q
 * @since 0.5.5
 */
public class DataSourceFactoryFactory<T extends DataSourceFactory> implements BeanFactory<T> {
    private final Set<String> names = new HashSet<>();
    public DataSourceFactoryFactory() {
        names.add("dataSourceFactory");
        names.add(DataSourceFactory.class.getName());
    }

    @Override
    public T factoryBean(ApplicationContext applicationContext) {
        GlobalBeanFactory globalBeanFactory = applicationContext.getGlobalBeanFactory();
        DataSourceConfiguration configuration = globalBeanFactory.factoryConfiguration(DataSourceConfiguration.class, EnvironmentDepositoryHolder.DEFAULT_PROFILE, EnvironmentDepositoryHolder.DEFAULT_CATEGORY);
        return (T) DataSourceFactory.loadFactory(configuration);
    }

    @Override
    public boolean isCreated() {
        return false;
    }

    @Override
    public T getCreatedBean() {
        return null;
    }

    @Override
    public Class<?> getBeanClass() {
        return DataSourceFactory.class;
    }

    @Override
    public Set<String> getAllName() {
        return names;
    }
}
