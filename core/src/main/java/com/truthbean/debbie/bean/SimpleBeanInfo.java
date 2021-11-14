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

import com.truthbean.debbie.proxy.BeanProxyType;

import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * @author TruthBean/Rogar·Q
 * @since 0.1.0
 * Created on 2020-10-07 22:25
 */
public class SimpleBeanInfo<Bean> implements BeanInfo<Bean> {

    private final BeanFactory<Bean> beanFactory;
    private final Bean bean;
    private final Class<Bean> beanClass;
    private final BeanType beanType;
    private final BeanProxyType beanProxyType;
    private final Set<String> beanNames;
    private final Set<BeanCondition> conditions = new HashSet<>();

    @SuppressWarnings("unchecked")
    public SimpleBeanInfo(Bean bean, BeanType beanType, BeanProxyType beanProxyType, BeanCondition...conditions) {
        this.bean = bean;
        this.beanFactory = null;
        this.beanClass = (Class<Bean>) bean.getClass();
        this.beanType = beanType;
        this.beanProxyType = beanProxyType;
        this.beanNames = new HashSet<>();
        this.beanNames.add(this.beanClass.getName());
        if (conditions != null && conditions.length > 0) {
            Collections.addAll(this.conditions, conditions);
        }
    }

    @SuppressWarnings("unchecked")
    public SimpleBeanInfo(Bean bean, BeanType beanType, BeanProxyType beanProxyType, String beanName, BeanCondition...conditions) {
        this.bean = bean;
        this.beanFactory = null;
        this.beanClass = (Class<Bean>) bean.getClass();
        this.beanType = beanType;
        this.beanProxyType = beanProxyType;
        this.beanNames = new HashSet<>();
        this.beanNames.add(beanName);
        if (conditions != null && conditions.length > 0) {
            Collections.addAll(this.conditions, conditions);
        }
    }

    public SimpleBeanInfo(Bean bean,
                          Class<Bean> beanClass, BeanType beanType, BeanProxyType beanProxyType, String beanName,
                          BeanCondition...conditions) {
        this.beanFactory = null;
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

    public SimpleBeanInfo(Bean bean,
                          Class<Bean> beanClass, BeanType beanType, BeanProxyType beanProxyType,
                          Set<String> beanNames, BeanCondition...conditions) {
        this.beanFactory = null;
        this.bean = bean;
        this.beanClass = beanClass;
        this.beanType = beanType;
        this.beanProxyType = beanProxyType;
        this.beanNames = beanNames;
        if (conditions != null && conditions.length > 0) {
            Collections.addAll(this.conditions, conditions);
        }
    }

    public SimpleBeanInfo(BeanFactory<Bean> beanFactory,
                          Class<Bean> beanClass, BeanType beanType, BeanProxyType beanProxyType,
                          String beanName, BeanCondition...conditions) {
        this.beanFactory = beanFactory;
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

    public SimpleBeanInfo(BeanFactory<Bean> beanFactory,
                          Class<Bean> beanClass, BeanType beanType, BeanProxyType beanProxyType,
                          Set<String> beanNames, BeanCondition...conditions) {
        this.beanFactory = beanFactory;
        this.bean = null;
        this.beanClass = beanClass;
        this.beanType = beanType;
        this.beanProxyType = beanProxyType;
        this.beanNames = beanNames;
        if (conditions != null && conditions.length > 0) {
            Collections.addAll(this.conditions, conditions);
        }
    }

    public SimpleBeanInfo(BeanFactory<Bean> beanFactory, Bean bean,
                          Class<Bean> beanClass, BeanType beanType, BeanProxyType beanProxyType, String beanName,
                          BeanCondition...conditions) {
        this.beanFactory = beanFactory;
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

    public SimpleBeanInfo(BeanFactory<Bean> beanFactory, Bean bean,
                          Class<Bean> beanClass, BeanType beanType, BeanProxyType beanProxyType,
                          Set<String> beanNames, BeanCondition...conditions) {
        this.beanFactory = beanFactory;
        this.bean = bean;
        this.beanClass = beanClass;
        this.beanType = beanType;
        this.beanProxyType = beanProxyType;
        this.beanNames = beanNames;
        if (conditions != null && conditions.length > 0) {
            Collections.addAll(this.conditions, conditions);
        }
    }

    @Override
    public Set<BeanCondition> getConditions() {
        return conditions;
    }

    @Override
    public BeanFactory<Bean> getBeanFactory() {
        return beanFactory;
    }

    @Override
    public Class<Bean> getBeanClass() {
        return beanClass;
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
    public Bean getBean() {
        return bean;
    }

    @Override
    public Set<String> getBeanNames() {
        return beanNames;
    }

    @Override
    public String toString() {
        return "\"SimpleBeanInfo\":{" +
                "\"beanFactory\":" + beanFactory + "," +
                "\"bean\":" + bean + "," +
                "\"beanClass\":" + beanClass + "," +
                "\"beanType\":" + beanType + "," +
                "\"beanProxyType\":" + beanProxyType + "," +
                "\"beanNames\":" + beanNames + "}";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BeanInfo)) return false;
        // if (!super.equals(o)) return false;
        BeanInfo<?> beanInfo = (BeanInfo<?>) o;
        Set<String> beanNames = getBeanNames();
        Set<String> oBeanNames = beanInfo.getBeanNames();
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
    public SimpleBeanInfo<Bean> copy() {
        if (beanType == BeanType.SINGLETON) {
            return this;
        }
        return new SimpleBeanInfo<>(beanFactory, beanClass, beanType, beanProxyType, new HashSet<>(beanNames));
    }
}
