/**
 * Copyright (c) 2023 TruthBean(Rogar·Q)
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

/**
 * @author TruthBean/Rogar·Q
 * @since 0.1.0
 * Created on 2020-10-07 22:35
 */
public class SimpleBeanFactory<Bean extends I, I> implements BeanFactory<Bean> {

    protected volatile Bean bean;

    private final Class<I> beanClass;
    protected BeanType beanType;
    protected BeanProxyType beanProxyType;
    protected final Set<String> beanNames;
    private final Set<BeanCondition> conditions = new HashSet<>();

    private final Map<String, Object> properties = new HashMap<>();

    protected volatile boolean created;

    public SimpleBeanFactory(Bean bean) {
        this.bean = bean;
        this.beanClass = null;
        this.beanType = BeanType.SINGLETON;
        this.beanProxyType = BeanProxyType.NO;
        this.beanNames = new HashSet<>();
        this.beanNames.add(this.bean.getClass().getName());
    }

    public SimpleBeanFactory(Bean bean, boolean created) {
        this.bean = bean;
        this.created = created;
        this.beanClass = null;
        this.beanType = BeanType.SINGLETON;
        this.beanProxyType = BeanProxyType.NO;
        this.beanNames = new HashSet<>();
        this.beanNames.add(this.bean.getClass().getName());
    }

    public SimpleBeanFactory(Bean bean, Class<I> beanClass, String... names) {
        this.bean = bean;
        this.beanClass = beanClass;
        this.beanType = BeanType.SINGLETON;
        this.beanProxyType = BeanProxyType.JDK;
        this.beanNames = new HashSet<>();
        if (names != null && names.length > 0) {
            this.beanNames.addAll(Arrays.asList(names));
        } else {
            this.beanNames.add(this.beanClass.getName());
        }
    }

    public SimpleBeanFactory(Bean bean, Class<I> beanClass, BeanProxyType beanProxyType, String... names) {
        this.bean = bean;
        this.beanClass = beanClass;
        this.beanType = BeanType.SINGLETON;
        this.beanProxyType = beanProxyType;
        this.beanNames = new HashSet<>();
        this.beanNames.addAll(Arrays.asList(names));
    }

    public SimpleBeanFactory(Bean bean, BeanType beanType, BeanProxyType beanProxyType, BeanCondition...conditions) {
        this.bean = bean;
        this.beanClass = null;
        this.beanType = beanType;
        this.beanProxyType = beanProxyType;
        this.beanNames = new HashSet<>();
        this.beanNames.add(this.bean.getClass().getName());
        if (conditions != null && conditions.length > 0) {
            Collections.addAll(this.conditions, conditions);
        }
    }

    public SimpleBeanFactory(Bean bean, BeanType beanType, BeanProxyType beanProxyType, String beanName, BeanCondition...conditions) {
        this.bean = bean;
        this.beanClass = null;
        this.beanType = beanType;
        this.beanProxyType = beanProxyType;
        this.beanNames = new HashSet<>();
        this.beanNames.add(beanName);
        if (conditions != null && conditions.length > 0) {
            Collections.addAll(this.conditions, conditions);
        }
    }

    public SimpleBeanFactory(Bean bean,
                          Class<I> beanClass, BeanType beanType, BeanProxyType beanProxyType, String beanName,
                          BeanCondition...conditions) {
        this.bean = bean;
        this.beanClass = beanClass;
        this.beanType = beanType;
        this.beanProxyType = beanProxyType;
        this.beanNames = new HashSet<>();
        this.beanNames.add(beanName);
        if (conditions != null && conditions.length > 0) {
            Collections.addAll(this.conditions, conditions);
        }
    }

    public SimpleBeanFactory(Bean bean,
                          Class<I> beanClass, BeanType beanType, BeanProxyType beanProxyType,
                          Set<String> beanNames, BeanCondition...conditions) {
        this.bean = bean;
        this.beanClass = beanClass;
        this.beanType = beanType;
        this.beanProxyType = beanProxyType;
        this.beanNames = beanNames;
        if (conditions != null && conditions.length > 0) {
            Collections.addAll(this.conditions, conditions);
        }
    }

    public SimpleBeanFactory(Class<I> beanClass, BeanType beanType, BeanProxyType beanProxyType,
                          String beanName, BeanCondition...conditions) {
        this.bean = null;
        this.beanClass = beanClass;
        this.beanType = beanType;
        this.beanProxyType = beanProxyType;
        this.beanNames = new HashSet<>();
        this.beanNames.add(beanName);
        if (conditions != null && conditions.length > 0) {
            Collections.addAll(this.conditions, conditions);
        }
    }

    public SimpleBeanFactory(Class<I> beanClass, BeanType beanType, BeanProxyType beanProxyType,
                          Set<String> beanNames, BeanCondition...conditions) {
        this.bean = null;
        this.beanClass = beanClass;
        this.beanType = beanType;
        this.beanProxyType = beanProxyType;
        this.beanNames = beanNames;
        if (conditions != null && conditions.length > 0) {
            Collections.addAll(this.conditions, conditions);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public Bean factoryNamedBean(String name, ApplicationContext applicationContext) {
        if (!created) {
            Bean localBean = bean;
            Set<BeanLifecycle> beanLifecycles = applicationContext.getBeanLifecycle();
            for (BeanLifecycle beanLifecycle : beanLifecycles) {
                if (beanLifecycle.support(getBeanClass()) && beanLifecycle.support(this)) {
                    localBean = beanLifecycle.construct(localBean);
                    beanLifecycle.postConstruct(localBean);
                    localBean = (Bean) beanLifecycle.doPreCreated(this, localBean, beanClass, BeanProxyType.JDK);
                    localBean = beanLifecycle.getCreatedBean(localBean);
                    bean = localBean;
                }
            }
            created = true;
        }
        return bean;
    }

    @Override
    public boolean isCreated() {
        return created;
    }

    @Override
    public Bean getCreatedBean() {
        if (created) {
            return bean;
        }
        return null;
    }

    @Override
    public Class<?> getBeanClass() {
        if (beanClass != null) {
            return beanClass;
        } else if (bean != null) {
            return bean.getClass();
        }
        return null;
    }

    @Override
    public boolean isLazyCreate() {
        return false;
    }

    @Override
    public BeanType getBeanType() {
        return beanType;
    }

    @Override
    public BeanProxyType getBeanProxyType() {
        return beanProxyType;
    }

    @Override
    public Set<String> getAllName() {
        return beanNames;
    }

    @Override
    public Set<BeanCondition> getConditions() {
        return conditions;
    }

    @Override
    public boolean isSingleton() {
        return BeanType.SINGLETON == beanType;
    }

    public void addProperty(String name, Object value) {
        properties.put(name, value);
    }

    public Object getProperty(String name) {
        return properties.get(name);
    }

    @Override
    public String toString() {
        return "\"SimpleBeanFactory\":{" +
                "\"bean\":" + bean + "," +
                "\"beanClass\":" + beanClass + "," +
                "\"beanType\":" + beanType + "," +
                "\"beanProxyType\":" + beanProxyType + "," +
                "\"beanNames\":" + beanNames + "}";
    }

    @Override
    @SuppressWarnings("unchecked")
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof SimpleBeanFactory)) {
            return false;
        }
        // if (!super.equals(o)) return false;
        SimpleBeanFactory<Bean, I> beanInfo = (SimpleBeanFactory<Bean, I>) o;
        Set<String> beanNames = getAllName();
        Set<String> oBeanNames = beanInfo.getAllName();
        boolean beanNameEmpty = beanNames == null || beanNames.isEmpty() || oBeanNames == null || oBeanNames.isEmpty();
        if (beanNameEmpty) {
            return true;
        }
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
            if (obj != null) {
                h += obj.hashCode();
            }
        }
        return h;
    }

    @Override
    public SimpleBeanFactory<Bean, I> copy() {
        if (beanType == BeanType.SINGLETON) {
            return this;
        }
        return new SimpleBeanFactory<>(beanClass, beanType, beanProxyType, new HashSet<>(beanNames));
    }

    @Override
    public void destruct(ApplicationContext applicationContext) {
        Set<BeanLifecycle> beanLifecycles = applicationContext.getBeanLifecycle();
        for (BeanLifecycle beanLifecycle : beanLifecycles) {
            if (beanLifecycle.support(getBeanClass()) && beanLifecycle.support(this)) {
                beanLifecycle.doBeforeDestruct(bean);
                beanLifecycle.destruct(bean);
            }
        }
    }

    @Override
    public Logger getLogger() {
        return LOGGER;
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(SimpleBeanFactory.class);
}
