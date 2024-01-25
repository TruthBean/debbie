/**
 * Copyright (c) 2024 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 * http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 *//*

package com.truthbean.debbie.controversial;

import com.truthbean.Logger;
import com.truthbean.debbie.bean.BeanComponentInfo;
import com.truthbean.debbie.bean.BeanComponentParser;
import com.truthbean.debbie.bean.BeanFactory;
import com.truthbean.debbie.bean.ClassBeanInfo;
import com.truthbean.debbie.controversial.FactoryBeanInfo;
import com.truthbean.debbie.core.ApplicationContext;
import com.truthbean.debbie.reflection.ReflectionHelper;
import com.truthbean.LoggerFactory;

import java.lang.annotation.Annotation;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Supplier;

*/
/**
 * @author TruthBean/Rogar·Q
 * @since 0.2.0
 * Created on 2020-12-10 21:57
 *//*

class DebbieClassFactoryBeanInfo<Bean> extends ClassBeanInfo<Bean> implements FactoryBeanInfo<Bean> {
    private int order;
    private boolean created;

    private BeanFactory<Bean> beanFactory;
    private Bean bean;

    public DebbieClassFactoryBeanInfo(Class<Bean> beanClass) {
        super(beanClass);
    }

    public DebbieClassFactoryBeanInfo(Class<Bean> beanClass, Map<Class<? extends Annotation>, BeanComponentParser> componentAnnotationTypes) {
        super(beanClass, componentAnnotationTypes);
    }

    public DebbieClassFactoryBeanInfo(FactoryBeanInfo<Bean> factoryBeanInfo) {
        super(factoryBeanInfo.getBeanClass());
        this.bean = factoryBeanInfo.getBean();
        this.addBeanName(factoryBeanInfo.getName());
        this.addBeanName(factoryBeanInfo.getBeanNames());
        this.beanFactory = factoryBeanInfo.getBeanFactory();
        setBeanType(factoryBeanInfo.getBeanType());
        super.setMethods();
    }

    public void setBeanFactory(BeanFactory<Bean> beanFactory) {
        this.beanFactory = beanFactory;
    }

    @Override
    public BeanFactory<Bean> getBeanFactory() {
        return beanFactory;
    }

    @Override
    public boolean hasBeanFactory() {
        return beanFactory != null;
    }

    @Override
    @SuppressWarnings({"rawtypes", "unchecked"})
    protected void setBeanComponent(BeanComponentInfo info) {
        super.setBeanComponent(info);

        if (beanFactory == null && info.getFactory() != null) {
            Class<? extends BeanFactory> factory = info.getFactory();
            if (factory != null && factory != BeanFactory.class) {
                BeanFactory beanFactory = ReflectionHelper.newInstance(factory, new Class[]{FactoryBeanInfo.class},
                        new Object[]{this});
                setBeanFactory(beanFactory);
            }
        }
    }

    @Override
    public void setBean(Bean bean) {
        this.bean = bean;
    }

    @Override
    public <T extends Bean> void setBean(Supplier<T> bean) {
        this.bean = bean.get();
    }

    @Override
    @SuppressWarnings("unchecked")
    public Class<Bean> getBeanClass() {
        return (Class<Bean>) super.getBeanClass();
    }

    @Override
    public Bean getBean() {
        return bean;
    }

    public Bean getCreatedBean(ApplicationContext applicationContext) {
        if (created) {
            return bean;
        }
        if (beanFactory != null) {
            return beanFactory.factoryBean(applicationContext);
        }
        return null;
    }

    @Override
    public boolean isCreated() {
        return created;
    }

    @Override
    public void setCreated(boolean created) {
        this.created = created;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    @Override
    public Optional<Bean> optional() {
        return Optional.ofNullable(bean);
    }

    @Override
    public boolean isEmpty() {
        return bean == null;
    }

    @Override
    public boolean isPresent() {
        return bean != null;
    }

    @Override
    public Supplier<Bean> getBeanSupplier() {
        return () -> bean;
    }

    @Override
    public void consumer(Consumer<Bean> consumer) {
        consumer.accept(bean);
    }

    @Override
    public DebbieClassFactoryBeanInfo<Bean> copy() {
        DebbieClassFactoryBeanInfo<Bean> beanInfo = new DebbieClassFactoryBeanInfo<>(getClazz());
        if (beanFactory != null) {
            beanInfo.setBeanFactory(beanFactory);
        }
        beanInfo.copyFrom(beanInfo);

        return beanInfo;
    }

    @Override
    public void copyFrom(ClassBeanInfo<Bean> beanInfo) {
        if (beanInfo instanceof DebbieClassFactoryBeanInfo classBeanInfo) {
            this.beanFactory = classBeanInfo.beanFactory;
        }
        super.copyFrom(beanInfo);
    }

    @Override
    public void destruct(ApplicationContext applicationContext) {
        if (beanFactory != null) {
            beanFactory.destruct(applicationContext);
        } else {
            close(applicationContext);
        }
        order = 0;
        bean = null;
        super.destruct(applicationContext);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DebbieClassFactoryBeanInfo<?> that = (DebbieClassFactoryBeanInfo<?>) o;
        return isEquals(o) &&
                Objects.equals(this.beanFactory, that.beanFactory);
    }

    @Override
    public int hashCode() {
        return Objects.hash(getHashCode(super.hashCode()), beanFactory);
    }

    @Override
    public String toString() {
        return "\"DebbieClassFactoryBeanInfo\"" + super.toString();
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(DebbieClassFactoryBeanInfo.class);
}*/
