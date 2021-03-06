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

import java.util.HashSet;
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
    private final Set<String> beanNames;
    private final Set<BeanCondition> conditions = new HashSet<>();

    @SuppressWarnings("unchecked")
    public SimpleBeanInfo(Bean bean, BeanType beanType, BeanCondition...conditions) {
        this.bean = bean;
        this.beanFactory = null;
        this.beanClass = (Class<Bean>) bean.getClass();
        this.beanType = beanType;
        this.beanNames = new HashSet<>();
        this.beanNames.add(this.beanClass.getName());
        if (conditions != null && conditions.length > 0) {
            for (BeanCondition condition : conditions) {
                this.conditions.add(condition);
            }
        }
    }

    @SuppressWarnings("unchecked")
    public SimpleBeanInfo(Bean bean, BeanType beanType, String beanName, BeanCondition...conditions) {
        this.bean = bean;
        this.beanFactory = null;
        this.beanClass = (Class<Bean>) bean.getClass();
        this.beanType = beanType;
        this.beanNames = new HashSet<>();
        this.beanNames.add(beanName);
        if (conditions != null && conditions.length > 0) {
            for (BeanCondition condition : conditions) {
                this.conditions.add(condition);
            }
        }
    }

    public SimpleBeanInfo(Bean bean,
                          Class<Bean> beanClass, BeanType beanType, String beanName, BeanCondition...conditions) {
        this.beanFactory = null;
        this.bean = bean;
        this.beanClass = beanClass;
        this.beanType = beanType;
        this.beanNames = new HashSet<>();
        this.beanNames.add(beanName);
        if (conditions != null && conditions.length > 0) {
            for (BeanCondition condition : conditions) {
                this.conditions.add(condition);
            }
        }
    }

    public SimpleBeanInfo(Bean bean,
                          Class<Bean> beanClass, BeanType beanType, Set<String> beanNames, BeanCondition...conditions) {
        this.beanFactory = null;
        this.bean = bean;
        this.beanClass = beanClass;
        this.beanType = beanType;
        this.beanNames = beanNames;
        if (conditions != null && conditions.length > 0) {
            for (BeanCondition condition : conditions) {
                this.conditions.add(condition);
            }
        }
    }

    public SimpleBeanInfo(BeanFactory<Bean> beanFactory,
                          Class<Bean> beanClass, BeanType beanType, String beanName, BeanCondition...conditions) {
        this.beanFactory = beanFactory;
        this.bean = null;
        this.beanClass = beanClass;
        this.beanType = beanType;
        this.beanNames = new HashSet<>();
        this.beanNames.add(beanName);
        if (conditions != null && conditions.length > 0) {
            for (BeanCondition condition : conditions) {
                this.conditions.add(condition);
            }
        }
    }

    public SimpleBeanInfo(BeanFactory<Bean> beanFactory,
                          Class<Bean> beanClass, BeanType beanType, Set<String> beanNames, BeanCondition...conditions) {
        this.beanFactory = beanFactory;
        this.bean = null;
        this.beanClass = beanClass;
        this.beanType = beanType;
        this.beanNames = beanNames;
        if (conditions != null && conditions.length > 0) {
            for (BeanCondition condition : conditions) {
                this.conditions.add(condition);
            }
        }
    }

    public SimpleBeanInfo(BeanFactory<Bean> beanFactory, Bean bean,
                          Class<Bean> beanClass, BeanType beanType, String beanName, BeanCondition...conditions) {
        this.beanFactory = beanFactory;
        this.bean = bean;
        this.beanClass = beanClass;
        this.beanType = beanType;
        this.beanNames = new HashSet<>();
        this.beanNames.add(beanName);
        if (conditions != null && conditions.length > 0) {
            for (BeanCondition condition : conditions) {
                this.conditions.add(condition);
            }
        }
    }

    public SimpleBeanInfo(BeanFactory<Bean> beanFactory, Bean bean,
                          Class<Bean> beanClass, BeanType beanType, Set<String> beanNames, BeanCondition...conditions) {
        this.beanFactory = beanFactory;
        this.bean = bean;
        this.beanClass = beanClass;
        this.beanType = beanType;
        this.beanNames = beanNames;
        if (conditions != null && conditions.length > 0) {
            for (BeanCondition condition : conditions) {
                this.conditions.add(condition);
            }
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
                "\"beanNames\":" + beanNames + "}";
    }

    @Override
    public SimpleBeanInfo<Bean> copy() {
        if (beanType == BeanType.SINGLETON) {
            return this;
        }
        return new SimpleBeanInfo<>(beanFactory, beanClass, beanType, new HashSet<>(beanNames));
    }
}
