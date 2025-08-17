package com.truthbean.debbie.jdbc.datasource;

import com.truthbean.debbie.bean.BeanFactory;
import com.truthbean.debbie.bean.BeanInfo;
import com.truthbean.debbie.bean.BeanInfoManager;
import com.truthbean.debbie.core.ApplicationContext;
import com.truthbean.debbie.environment.EnvironmentDepositoryHolder;
import com.truthbean.debbie.jdbc.datasource.multi.DefaultMultiDataSourceFactory;
import com.truthbean.debbie.jdbc.datasource.pool.DefaultDataSourcePoolFactory;
import com.truthbean.debbie.properties.PropertiesConfigurationBeanFactory;
import com.truthbean.debbie.reflection.ReflectionHelper;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author TruthBean/Rogar·Q
 * @since 0.5.5
 */
public class DataSourceFactoryFactory<T extends DataSourceFactory> implements BeanFactory<T> {
    private final Set<String> names = new HashSet<>();

    private T dataSourceFactory;

    public DataSourceFactoryFactory() {
        names.add("dataSourceFactory");
        names.add(DataSourceFactory.class.getName());
    }

    @SuppressWarnings("unchecked")
    @Override
    public T factoryBean(ApplicationContext applicationContext) {
        if (dataSourceFactory != null) {
            return dataSourceFactory;
        }
        BeanInfoManager beanInfoManager = applicationContext.getBeanInfoManager();
        List<BeanInfo<? extends DataSourceConfiguration>> list = beanInfoManager.getBeanInfoList(DataSourceConfiguration.class, false);
        for (BeanInfo<? extends DataSourceConfiguration> info : list) {
            if (info instanceof PropertiesConfigurationBeanFactory<? extends DataSourceConfiguration, ?> propertiesConfigurationBeanFactory) {
                String profile = EnvironmentDepositoryHolder.DEFAULT_PROFILE;
                String category = EnvironmentDepositoryHolder.DEFAULT_CATEGORY;
                if (propertiesConfigurationBeanFactory.contains(profile, category, applicationContext)) {
                    DataSourceConfiguration configuration = propertiesConfigurationBeanFactory.factory(profile, category, applicationContext);
                    Class<? extends DataSourceFactory> factoryClass = configuration.getDataSourceFactoryClass();
                    DataSourceFactory factory;
                    if (factoryClass == null || DefaultDataSourceFactory.class.equals(factoryClass)) {
                        factory = new DefaultDataSourceFactory();
                    } else if (DefaultDataSourcePoolFactory.class.equals(factoryClass)) {
                        factory = new DefaultDataSourcePoolFactory();
                    } else if (DefaultMultiDataSourceFactory.class.equals(factoryClass)) {
                        factory = new DefaultMultiDataSourceFactory();
                    } else {
                        factory = ReflectionHelper.newInstance(factoryClass);
                    }
                    if (factory.support(configuration)) {
                        dataSourceFactory = (T) factory.factory(configuration);
                        break;
                    }
                }
            }
        }
        return dataSourceFactory;
    }

    @Override
    public boolean isCreated() {
        return dataSourceFactory != null;
    }

    @Override
    public T getCreatedBean() {
        return dataSourceFactory;
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
