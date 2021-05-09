/**
 * Copyright (c) 2021 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 * http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.bean;

import com.truthbean.Logger;
import com.truthbean.common.mini.util.StringUtils;
import com.truthbean.LoggerFactory;

import java.io.Closeable;
import java.io.IOException;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * @author TruthBean
 * @since 0.0.1
 */
public class DebbieBeanInfo<Bean> implements MutableBeanInfo<Bean> {
    private final Set<String> beanNames = new HashSet<>();
    private int order;

    private BeanType beanType;
    private Boolean lazyCreate;

    private final Set<BeanCondition> conditions = new HashSet<>();

    private final Class<Bean> beanClass;
    private BeanFactory<Bean> beanFactory;
    private Bean bean;
    private final Map<String, Object> properties = new HashMap<>();

    public DebbieBeanInfo(Class<Bean> beanClass) {
        this.beanClass = beanClass;
    }

    public DebbieBeanInfo(BeanInfo<Bean> beanInfo) {
        this.beanClass = beanInfo.getBeanClass();
        this.bean = beanInfo.getBean();
        this.addBeanName(beanInfo.getServiceName());
        this.addBeanNames(beanInfo.getBeanNames());
        this.beanFactory = beanInfo.getBeanFactory();
        this.beanType = beanInfo.getBeanType();
    }

    @Override
    public Set<BeanCondition> getConditions() {
        return conditions;
    }

    public void addConditions(Set<BeanCondition> conditions) {
        this.conditions.addAll(conditions);
    }

    public void addCondition(BeanCondition... conditions) {
        if (conditions != null && conditions.length > 0) {
            this.conditions.addAll(Arrays.asList(conditions));
        }
    }

    @Override
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

    /**
     * WARN: only used by BeanInitialization.init(DebbieBeanInfo)
     *
     * @param beanType BeanType
     */
    @Override
    public void setBeanType(BeanType beanType) {
        this.beanType = beanType;
    }

    @Override
    public boolean isSingleton() {
        return this.beanType != null && this.beanType == BeanType.SINGLETON;
    }

    @Override
    public Class<Bean> getBeanClass() {
        return beanClass;
    }

    @Override
    public String getServiceName() {
        String name = this.beanNames.isEmpty() ? null : this.beanNames.iterator().next();
        if (name == null || name.isBlank()) {
            name = this.getBeanClass().getSimpleName();
            name = StringUtils.toFirstCharLowerCase(name);
            this.beanNames.add(name);
        }
        return name;
    }

    @Override
    public boolean containName(String name) {
        return this.beanNames.contains(name);
    }

    @Override
    public Set<String> getBeanNames() {
        return this.beanNames;
    }

    @Override
    public BeanType getBeanType() {
        return beanType;
    }

    @Override
    public void addBeanName(String beanName) {
        if (beanName != null && !beanName.isBlank())
            this.beanNames.add(beanName);
    }

    @Override
    public void addBeanNames(Set<String> beanNames) {
        this.beanNames.addAll(beanNames);
    }

    @Override
    public void setBean(Bean bean) {
        this.bean = bean;
    }

    @Override
    public void setBean(Supplier<Bean> bean) {
        this.bean = bean.get();
    }

    @Override
    public void addProperty(String name, Object value) {
        properties.put(name, value);
    }

    @Override
    public Object getProperty(String name) {
        return properties.get(name);
    }

    @Override
    public boolean isLazyCreate() {
        return lazyCreate != null && lazyCreate;
    }

    @Override
    public Bean getBean() {
        return bean;
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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DebbieBeanInfo)) return false;
        if (!super.equals(o)) return false;
        DebbieBeanInfo<?> beanInfo = (DebbieBeanInfo<?>) o;
        Set<String> beanNames = getBeanNames();
        Set<String> oBeanNames = beanInfo.getBeanNames();
        boolean beanNameEmpty = beanNames == null || beanNames.isEmpty() || oBeanNames == null || oBeanNames.isEmpty();
        if (beanNameEmpty) return true;
        return Objects.equals(beanNames, oBeanNames);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), this.beanNames);
    }

    @Override
    public DebbieBeanInfo<Bean> copy() {
        DebbieBeanInfo<Bean> beanInfo = new DebbieBeanInfo<>(getBeanClass());

        if (!beanNames.isEmpty()) {
            beanInfo.beanNames.addAll(beanNames);
        }
        if (beanFactory != null) {
            beanInfo.setBeanFactory(beanFactory);
        }
        if (beanType != null)
            beanInfo.setBeanType(beanType);

        return beanInfo;
    }

    @Override
    public void release() {
        if (beanFactory != null) {
            beanFactory.destroy();
        } else {
            close();
        }
        order = 0;
        bean = null;
    }

    @Override
    public String toString() {
        return "\"DebbieBeanInfo\":{" +
                "\"beanNames\":" + beanNames + "," +
                "\"order\":" + order + "," +
                "\"beanType\":" + beanType + "," +
                "\"lazyCreate\":" + lazyCreate + "," +
                "\"beanClass\":" + beanClass + "," +
                "\"beanFactory\":" + beanFactory + "," +
                "\"bean\":" + bean + "}";
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(DebbieBeanInfo.class);
}
