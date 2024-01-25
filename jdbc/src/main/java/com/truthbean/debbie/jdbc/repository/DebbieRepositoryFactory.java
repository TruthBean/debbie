/**
 * Copyright (c) 2024 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *         http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.jdbc.repository;

import com.truthbean.debbie.bean.*;
import com.truthbean.debbie.core.ApplicationContext;
import com.truthbean.debbie.jdbc.datasource.DataSourceConfiguration;
import com.truthbean.debbie.jdbc.entity.EntityResolver;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Set;

/**
 * @author truthbean
 * @since 0.1.0
 * Created on 2020/7/11 17:01.
 */
public class DebbieRepositoryFactory<R extends JdbcRepository<E, I>, E, I> implements BeanFactory<R> {

    private final Object reference = new Object();

    private DataSourceConfiguration configuration;

    private volatile R bean;

    private DebbieReflectionBeanFactory<R> beanFactory;

    private final ClassBeanInfo<R> beanInfo;
    private final JdbcTransactionRepository<E, I> repositoryHandler;
    private final EntityResolver entityResolver;

    public DebbieRepositoryFactory(ClassBeanInfo<R> beanInfo, EntityResolver entityResolver) {
        this.beanInfo = beanInfo;
        this.repositoryHandler = new JdbcTransactionRepository<>();
        this.entityResolver = entityResolver;
    }

    @Override
    @SuppressWarnings("unchecked")
    public R factoryBean(ApplicationContext applicationContext) {
        if (bean == null) {
            synchronized (reference) {
                if (bean == null) {
                    GlobalBeanFactory globalBeanFactory = applicationContext.getGlobalBeanFactory();
                    if (configuration == null) {
                        configuration = globalBeanFactory.factory(DataSourceConfiguration.class);
                    }
                    beanFactory = new DebbieReflectionBeanFactory<>(beanInfo);
                    R localBean = beanFactory.factoryBean(applicationContext);
                    localBean.setJdbcTransactionRepository(repositoryHandler);
                    localBean.setEntityResolver(entityResolver);
                    List<Type> types = beanInfo.getActualTypes();
                    if (types.size() == 2 && types.get(0) instanceof Class<?> && types.get(1) instanceof Class<?>) {
                        localBean.setEntityClass((Class<E>) types.get(0));
                        localBean.setIdClass((Class<I>) types.get(1));
                    }
                    bean = localBean;
                }
            }
        }
        return bean;
    }

    /*@Override
    @SuppressWarnings("unchecked")
    public R factoryNamedBean(String name, ApplicationContext applicationContext) {
        if (bean == null) {
            synchronized (reference) {
                if (bean == null) {
                    GlobalBeanFactory globalBeanFactory = applicationContext.getGlobalBeanFactory();
                    if (configuration == null) {
                        configuration = globalBeanFactory.factory(DataSourceConfiguration.class);
                    }
                    beanFactory = new DebbieReflectionBeanFactory<>(beanInfo);
                    R localBean = beanFactory.factoryNamedBean(name, applicationContext);
                    localBean.setJdbcTransactionRepository(repositoryHandler);
                    localBean.setEntityResolver(entityResolver);
                    List<Type> types = beanInfo.getActualTypes();
                    if (types.size() == 2 && types.get(0) instanceof Class<?> && types.get(1) instanceof Class<?>) {
                        localBean.setEntityClass((Class<E>) types.get(0));
                        localBean.setIdClass((Class<I>) types.get(1));
                    }
                    bean = localBean;
                }
            }
        }
        return bean;
    }*/

    @Override
    public Set<String> getAllName() {
        return beanInfo.getAllName();
    }

    @Override
    public Set<BeanCondition> getConditions() {
        return beanInfo.getConditions();
    }

    @Override
    public boolean isCreated() {
        return bean != null;
    }

    @Override
    public R getCreatedBean() {
        return bean;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Class<R> getBeanClass() {
        return (Class<R>) beanInfo.getBeanClass();
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

    @Override
    public void destruct(ApplicationContext applicationContext) {
        if (beanFactory != null) {
            beanFactory.destruct(applicationContext);
        }
    }

}
