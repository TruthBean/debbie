/**
 * Copyright (c) 2024 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 * http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.bean;

import com.truthbean.debbie.core.ApplicationContext;

import java.lang.ref.WeakReference;
import java.util.HashSet;
import java.util.Set;

/**
 * @author TruthBean/Rogar·Q
 * @since 0.5.0
 * Created on 2021-02-24 22:57
 */
public class WeakReferenceBeanFactory<T> implements BeanFactory<T> {

    private WeakReference<T> bean;
    private final Class<T> beanClass;
    private final Set<String> beanNames;

    public WeakReferenceBeanFactory(Class<T> beanClass) {
        this.beanClass = beanClass;
        this.beanNames = new HashSet<>();
    }

    @Override
    public Set<String> getAllName() {
        return beanNames;
    }

    @Override
    public void destruct(ApplicationContext applicationContext) {
        bean.clear();
    }

    @Override
    public T factoryBean(ApplicationContext applicationContext) {
        this.bean = new WeakReference<>(applicationContext.getGlobalBeanFactory().factory(this.beanClass));
        return bean.get();
    }

    @Override
    public boolean isCreated() {
        return false;
    }

    @Override
    public T getCreatedBean() {
        if (bean != null) {
            return bean.get();
        }
        return null;
    }

    @Override
    public Class<?> getBeanClass() {
        return beanClass;
    }

    @Override
    public boolean isSingleton() {
        return false;
    }
}
