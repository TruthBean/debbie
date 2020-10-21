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

/**
 * @author TruthBean/Rogar·Q
 * @since 0.1.0
 * Created on 2020-10-07 22:35
 */
public class SimpleBeanFactory<Bean> implements BeanFactory<Bean> {

    private final Bean bean;
    private final Class<Bean> beanClass;
    private final boolean singleton;

    public SimpleBeanFactory(Bean bean) {
        this.bean = bean;
        this.beanClass = null;
        this.singleton = false;
    }

    public SimpleBeanFactory(Bean bean, boolean singleton) {
        this.bean = bean;
        this.beanClass = null;
        this.singleton = singleton;
    }

    public SimpleBeanFactory(Bean bean, Class<Bean> beanClass, boolean singleton) {
        this.bean = bean;
        this.beanClass = beanClass;
        this.singleton = singleton;
    }

    @Override
    public Bean getBean() {
        return bean;
    }

    @Override
    public Bean factoryBean() {
        return bean;
    }

    @Override
    public Class<?> getBeanType() {
        if (beanClass != null) {
            return beanClass;
        } else if (bean != null) {
            return bean.getClass();
        }
        return null;
    }

    @Override
    public boolean isSingleton() {
        return singleton;
    }

    @Override
    public void destroy() {

    }

    @Override
    public void setGlobalBeanFactory(GlobalBeanFactory globalBeanFactory) {

    }
}
