package com.truthbean.debbie.jdbc.repository;

import com.truthbean.debbie.bean.BeanFactory;
import com.truthbean.debbie.core.ApplicationContext;

import java.util.HashSet;
import java.util.Set;

/**
 * @author TruthBean/Rogar·Q
 * @since 0.6.0
 */
public class SqlRepositoryFactory implements BeanFactory<SqlRepository> {
    private volatile SqlRepository sqlRepository;
    private final Set<String> names = new HashSet<>();

    public SqlRepositoryFactory(String name) {
        names.add(name);
    }

    @Override
    public SqlRepository factoryBean(ApplicationContext applicationContext) {
        if (sqlRepository == null) {
            sqlRepository = new SqlRepository();
        }
        return sqlRepository;
    }

    @Override
    public boolean isCreated() {
        return sqlRepository != null;
    }

    @Override
    public SqlRepository getCreatedBean() {
        return sqlRepository;
    }

    @Override
    public Class<?> getBeanClass() {
        return SqlRepository.class;
    }

    @Override
    public Set<String> getAllName() {
        return names;
    }
}
