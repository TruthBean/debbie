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

import com.truthbean.debbie.core.ApplicationContext;

import java.lang.ref.SoftReference;
import java.util.HashSet;
import java.util.Set;

/**
 * @author TruthBean/Rogar·Q
 * @since 0.5.0
 * Created on 2021-02-24 22:32
 */
public class SoftReferenceBeanFactory<T> implements BeanFactory<T> {

    private SoftReference<T> bean;
    private final Class<T> beanType;
    private final Set<String> names = new HashSet<>();

    public SoftReferenceBeanFactory(Class<T> beanClass) {
        this.beanType = beanClass;
        names.add(beanClass.getName());
    }

    @Override
    public Set<String> getAllName() {
        return names;
    }

    @Override
    public void destruct(ApplicationContext applicationContext) {
        bean.clear();
    }

    @Override
    public T factoryBean(ApplicationContext applicationContext) {
        bean = new SoftReference<>(applicationContext.getGlobalBeanFactory().factory(beanType));
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
        return beanType;
    }

    @Override
    public boolean isSingleton() {
        return false;
    }
}
