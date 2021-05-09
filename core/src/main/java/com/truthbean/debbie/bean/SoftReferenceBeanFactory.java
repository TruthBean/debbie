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

import java.lang.ref.SoftReference;

/**
 * @author TruthBean/Rogar·Q
 * @since 0.5.0
 * Created on 2021-02-24 22:32
 */
public class SoftReferenceBeanFactory<T> implements SkipCreatedBeanFactory<T> {

    private SoftReference<T> bean;
    private final Class<T> beanType;

    public SoftReferenceBeanFactory(Class<T> beanClass) {
        this.beanType = beanClass;
    }

    @Override
    public void destroy() {
        bean.clear();
    }

    @Override
    public T getBean() {
        return bean.get();
    }

    @Override
    public Class<?> getBeanType() {
        return beanType;
    }

    @Override
    public boolean isSingleton() {
        return false;
    }

    @Override
    public void setGlobalBeanFactory(GlobalBeanFactory globalBeanFactory) {
        bean = new SoftReference<>(globalBeanFactory.factory(beanType));
    }
}
