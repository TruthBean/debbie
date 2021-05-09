/**
 * Copyright (c) 2021 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *         http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.bean;

import com.truthbean.debbie.core.ApplicationContext;

/**
 * @author TruthBean
 * @since 0.0.2
 */
public class SingletonBeanRegister {
    private final BeanInfoFactory beanInfoFactory;
    private final BeanInitialization initialization;

    public SingletonBeanRegister(ApplicationContext applicationContext) {
        this.beanInfoFactory = applicationContext.getBeanInfoFactory();
        initialization = applicationContext.getBeanInitialization();
    }

    public <T extends I, I> void registerSingletonBean(T bean, Class<I> beanClass, String beanName) {
        MutableBeanInfo<I> beanInfo = new DebbieBeanInfo<>(beanClass);
        beanInfo.addBeanName(beanName);
        beanInfo.setBean(bean);
        initialization.initSingletonBean(beanInfo);
        beanInfoFactory.refreshBeans();
    }

    public <T extends I, I> void registerSingletonBean(MutableBeanInfo<T> beanInfo) {
        initialization.refreshSingletonBean(beanInfo);
        beanInfoFactory.refreshBeans();
    }
}
