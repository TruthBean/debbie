/**
 * Copyright (c) 2023 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 * http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 *//*

package com.truthbean.debbie.controversial;

import com.truthbean.Logger;
import com.truthbean.core.util.StringUtils;
import com.truthbean.LoggerFactory;
import com.truthbean.debbie.bean.BeanCondition;
import com.truthbean.debbie.bean.BeanFactory;
import com.truthbean.debbie.bean.BeanType;
import com.truthbean.debbie.core.ApplicationContext;
import com.truthbean.debbie.proxy.BeanProxyType;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Supplier;

*/
/**
 * @author TruthBean
 * @since 0.0.1
 *//*

class DebbieFactoryBeanInfo<Bean> implements MutableFactoryBeanInfo<Bean> {
    private final Set<String> beanNames = new HashSet<>();
    private int order;

    private BeanType beanType;
    private BeanProxyType beanProxyType;
    private Boolean lazyCreate;

    private final Set<BeanCondition> conditions = new HashSet<>();

    private final Class<Bean> beanClass;
    private BeanFactory<Bean> beanFactory;
    private Bean bean;
    private final Map<String, Object> properties = new HashMap<>();

    public DebbieFactoryBeanInfo(Class<Bean> beanClass) {
        this.beanClass = beanClass;
    }

    public DebbieFactoryBeanInfo(Class<Bean> beanClass, Boolean lazyCreate) {
        this.beanClass = beanClass;
        this.lazyCreate = lazyCreate;
    }

    public DebbieFactoryBeanInfo(FactoryBeanInfo<Bean> factoryBeanInfo) {
        this.beanClass = factoryBeanInfo.getBeanClass();
        this.bean = factoryBeanInfo.getBean();
        this.addBeanName(factoryBeanInfo.getName());
        this.addBeanName(factoryBeanInfo.getBeanNames());
        this.beanFactory = factoryBeanInfo.getBeanFactory();
        this.beanType = factoryBeanInfo.getBeanType();
        this.beanProxyType = factoryBeanInfo.getBeanProxyType();
        this.lazyCreate = factoryBeanInfo.isLazyCreate();
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

    */
/**
     * WARN: only used by BeanInfoManager.register(DebbieFactoryBeanInfo)
     *
     * @param beanType BeanType
     *//*

    @Override
    public void setBeanType(BeanType beanType) {
        this.beanType = beanType;
    }

    @Override
    public BeanProxyType getBeanProxyType() {
        return beanProxyType;
    }

    @Override
    public void setBeanProxyType(BeanProxyType beanProxyType) {
        this.beanProxyType = beanProxyType;
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
    public String getName() {
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
    public void addBeanName(String... beanName) {
        for (String name : beanName) {
            if (name != null && !name.isBlank()) {
                this.beanNames.add(name);
            }
        }
    }

    @Override
    public void addBeanName(Set<String> beanNames) {
        this.beanNames.addAll(beanNames);
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
        if (!(o instanceof FactoryBeanInfo<?> factoryBeanInfo)) return false;
        // if (!super.equals(o)) return false;
        Set<String> beanNames = getBeanNames();
        Set<String> oBeanNames = factoryBeanInfo.getBeanNames();
        boolean beanNameEmpty = beanNames == null || beanNames.isEmpty() || oBeanNames == null || oBeanNames.isEmpty();
        if (beanNameEmpty) return true;
        if (beanNames.size() == oBeanNames.size()) {
            boolean[] equals = new boolean[beanNames.size()];
            int i = 0;
            for (String s1 : beanNames) {
                for (String s2 : oBeanNames) {
                    if (s1.equals(s2)) {
                        equals[i] = true;
                        break;
                    }
                }
                i++;
            }
            for (boolean equal : equals) {
                if (!equal) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    @Override
    public int hashCode() {
        if (beanNames.isEmpty()) {
            return Objects.hash(super.hashCode(), this.beanNames);
        }
        // 重新计算hashcode
        int h = 0;
        for (String obj : beanNames) {
            if (obj != null)
                h += obj.hashCode();
        }
        return h;
    }

    @Override
    public DebbieFactoryBeanInfo<Bean> copy() {
        DebbieFactoryBeanInfo<Bean> beanInfo = new DebbieFactoryBeanInfo<>(getBeanClass());

        if (!beanNames.isEmpty()) {
            beanInfo.beanNames.addAll(beanNames);
        }
        if (beanFactory != null) {
            beanInfo.setBeanFactory(beanFactory);
        }
        if (beanType != null)
            beanInfo.setBeanType(beanType);

        if (beanProxyType != null) {
            beanInfo.setBeanProxyType(beanProxyType);
        }

        return beanInfo;
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
    }

    @Override
    public String toString() {
        return "\"DebbieFactoryBeanInfo\":{" +
                "\"beanNames\":" + beanNames + "," +
                "\"order\":" + order + "," +
                "\"beanType\":" + beanType + "," +
                "\"lazyCreate\":" + lazyCreate + "," +
                "\"beanClass\":" + beanClass + "," +
                "\"beanFactory\":" + beanFactory + "," +
                "\"bean\":" + bean + "}";
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(DebbieFactoryBeanInfo.class);
}
*/
