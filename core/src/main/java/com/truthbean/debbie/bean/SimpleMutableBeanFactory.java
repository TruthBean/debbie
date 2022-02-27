/**
 * Copyright (c) 2022 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 * http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.bean;

import com.truthbean.Logger;
import com.truthbean.LoggerFactory;
import com.truthbean.debbie.core.ApplicationContext;
import com.truthbean.debbie.proxy.BeanProxyType;

import java.util.*;
import java.util.function.Supplier;

/**
 * @author TruthBean/Rogar·Q
 * @since 0.1.0
 * Created on 2020-10-07 22:35
 */
public class SimpleMutableBeanFactory<Bean extends I, I> extends SimpleBeanFactory<Bean, I> implements MutableBeanFactory<Bean> {

    public SimpleMutableBeanFactory(Class<I> beanClass) {
        super(null, beanClass);
    }

    public SimpleMutableBeanFactory(Bean bean) {
        super(bean);
    }

    public SimpleMutableBeanFactory(Bean bean, Class<I> beanClass) {
        super(bean, beanClass);
    }

    public SimpleMutableBeanFactory(Bean bean, Class<I> beanClass, BeanProxyType beanProxyType, String... names) {
        super(bean, beanClass, beanProxyType, names);
    }

    public SimpleMutableBeanFactory(Bean bean, BeanType beanType, BeanProxyType beanProxyType, BeanCondition...conditions) {
        super(bean, beanType, beanProxyType, conditions);
    }

    public SimpleMutableBeanFactory(Bean bean, BeanType beanType, BeanProxyType beanProxyType, String beanName, BeanCondition...conditions) {
        super(bean, beanType, beanProxyType, beanName, conditions);
    }

    public SimpleMutableBeanFactory(Bean bean,
                                    Class<I> beanClass, BeanType beanType, BeanProxyType beanProxyType, String beanName,
                                    BeanCondition...conditions) {
        super(bean, beanClass, beanType, beanProxyType, beanName, conditions);
    }

    public SimpleMutableBeanFactory(Bean bean,
                                    Class<I> beanClass, BeanType beanType, BeanProxyType beanProxyType,
                                    Set<String> beanNames, BeanCondition...conditions) {
        super(bean, beanClass, beanType, beanProxyType, beanNames, conditions);
    }

    public SimpleMutableBeanFactory(Class<I> beanClass, BeanType beanType, BeanProxyType beanProxyType,
                                    String beanName, BeanCondition...conditions) {
        super(beanClass, beanType, beanProxyType, beanName, conditions);
    }

    public SimpleMutableBeanFactory(Class<I> beanClass, BeanType beanType, BeanProxyType beanProxyType,
                                    Set<String> beanNames, BeanCondition...conditions) {
        super(beanClass, beanType, beanProxyType, beanNames, conditions);
    }
    @Override
    @SuppressWarnings("unchecked")
    public Bean factoryNamedBean(String name, ApplicationContext applicationContext) {
        this.bean = super.factoryNamedBean(name, applicationContext);
        return this.bean;
    }

    @Override
    public void setBeanProxyType(BeanProxyType beanProxyType) {
        this.beanProxyType = beanProxyType;
    }

    @Override
    public void setBeanType(BeanType beanType) {
        this.beanType = beanType;
    }

    @Override
    public void addBeanName(String... beanNames) {
        this.beanNames.addAll(List.of(beanNames));
    }

    @Override
    public void addBeanName(Set<String> beanNames) {
        this.beanNames.addAll(beanNames);
    }

    @Override
    public void setBean(Bean bean) {
        this.bean = bean;
        this.created = true;
    }

    @Override
    public <T extends Bean> void setBean(Supplier<T> bean) {
        this.bean = bean.get();
        this.created = true;
    }

    @Override
    public Bean getCreatedBean(ApplicationContext applicationContext) {
        if (created) {
            return bean;
        }
        return factoryBean(applicationContext);
    }

    @Override
    public Logger getLogger() {
        return LOGGER;
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(SimpleMutableBeanFactory.class);
}
