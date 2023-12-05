package com.truthbean.debbie.jdbc.datasource;

import com.truthbean.debbie.bean.BeanFactory;
import com.truthbean.debbie.core.ApplicationContext;

import java.util.Set;

/**
 * @author TruthBean/Rogar·Q
 * @since 0.5.5
 */
public class DataSourceFactoryFactory<T extends DataSourceFactory> implements BeanFactory<T> {
    /*@Override
    public T factoryNamedBean(String name, ApplicationContext applicationContext) {
        return null;
    }*/

    @Override
    public T factoryBean(ApplicationContext applicationContext) {
        // todo
        return null;
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
        return null;
    }

    @Override
    public Set<String> getAllName() {
        return null;
    }
}
