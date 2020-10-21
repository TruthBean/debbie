/**
 * Copyright (c) 2020 TruthBean(Rogar·Q)
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

    public SimpleBeanInfo(Bean bean,
                          Class<Bean> beanClass, BeanType beanType, String beanName) {
        this.beanFactory = null;
        this.bean = bean;
        this.beanClass = beanClass;
        this.beanType = beanType;
        this.beanNames = new HashSet<>();
        this.beanNames.add(beanName);
    }

    public SimpleBeanInfo(Bean bean,
                          Class<Bean> beanClass, BeanType beanType, Set<String> beanNames) {
        this.beanFactory = null;
        this.bean = bean;
        this.beanClass = beanClass;
        this.beanType = beanType;
        this.beanNames = beanNames;
    }

    public SimpleBeanInfo(BeanFactory<Bean> beanFactory,
                          Class<Bean> beanClass, BeanType beanType, String beanName) {
        this.beanFactory = beanFactory;
        this.bean = null;
        this.beanClass = beanClass;
        this.beanType = beanType;
        this.beanNames = new HashSet<>();
        this.beanNames.add(beanName);
    }

    public SimpleBeanInfo(BeanFactory<Bean> beanFactory,
                          Class<Bean> beanClass, BeanType beanType, Set<String> beanNames) {
        this.beanFactory = beanFactory;
        this.bean = null;
        this.beanClass = beanClass;
        this.beanType = beanType;
        this.beanNames = beanNames;
    }

    public SimpleBeanInfo(BeanFactory<Bean> beanFactory, Bean bean,
                          Class<Bean> beanClass, BeanType beanType, String beanName) {
        this.beanFactory = beanFactory;
        this.bean = bean;
        this.beanClass = beanClass;
        this.beanType = beanType;
        this.beanNames = new HashSet<>();
        this.beanNames.add(beanName);
    }

    public SimpleBeanInfo(BeanFactory<Bean> beanFactory, Bean bean,
                          Class<Bean> beanClass, BeanType beanType, Set<String> beanNames) {
        this.beanFactory = beanFactory;
        this.bean = bean;
        this.beanClass = beanClass;
        this.beanType = beanType;
        this.beanNames = beanNames;
    }

    @Override
    public BeanFactory<Bean> getBeanFactory() {
        return beanFactory;
    }

    @Override
    public Class<Bean> getClazz() {
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
}
