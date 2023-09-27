/**
 * Copyright (c) 2023 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *         http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 *//*

package com.truthbean.debbie.controversial;

import com.truthbean.debbie.bean.BeanInfoManager;
import com.truthbean.debbie.bean.BeanType;
import com.truthbean.debbie.bean.MutableBeanFactory;
import com.truthbean.debbie.bean.SimpleBeanFactory;
import com.truthbean.debbie.core.ApplicationContext;
import com.truthbean.debbie.proxy.BeanProxyType;

import java.util.Set;

*/
/**
 * @author TruthBean
 * @since 0.0.2
 *//*

public class SingletonBeanRegister {
    private final BeanInfoManager beanInfoManager;

    public SingletonBeanRegister(ApplicationContext applicationContext) {
        beanInfoManager = applicationContext.getBeanInfoManager();
    }

    public <T extends I, I> void registerSingletonBean(T bean, Class<I> beanClass, String... beanName) {
        var beanFactory = new SimpleBeanFactory<>(bean, beanClass, BeanProxyType.JDK, beanName);
        beanInfoManager.register(beanFactory);
        beanInfoManager.refreshBeans();
    }

    public <T extends I, I> void registerSingletonBean(T bean, Class<I> beanClass, Set<String> beanNames) {
        SimpleBeanFactory<T, I> beanFactory = new SimpleBeanFactory<>(bean, beanClass, BeanType.SINGLETON, BeanProxyType.JDK, beanNames);
        beanInfoManager.register(beanFactory);
        beanInfoManager.refreshBeans();
    }

    public <T extends I, I> void registerSingletonBean(MutableFactoryBeanInfo<T> beanInfo) {
        beanInfo.setBeanType(BeanType.SINGLETON);
        beanInfoManager.refresh(beanInfo);
        beanInfoManager.refreshBeans();
    }

    public <T extends I, I> void registerSingletonBean(MutableBeanFactory<T> beanInfo) {
        beanInfo.setBeanType(BeanType.SINGLETON);
        beanInfoManager.refresh(beanInfo);
        beanInfoManager.refreshBeans();
    }
}
*/
