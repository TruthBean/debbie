/**
 * Copyright (c) 2020 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 * http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.core;

import com.truthbean.debbie.bean.BeanInfoFactory;
import com.truthbean.debbie.bean.BeanInitialization;
import com.truthbean.debbie.bean.GlobalBeanFactory;
import com.truthbean.debbie.bean.InjectedBeanFactory;
import com.truthbean.debbie.io.ResourceResolver;
import com.truthbean.debbie.properties.DebbieConfigurationCenter;

/**
 * @author TruthBean
 * @since 0.1.0
 */
public interface ApplicationContext {

    ClassLoader getClassLoader();

    ResourceResolver getResourceResolver();

    BeanInitialization getBeanInitialization();

    BeanInfoFactory getBeanInfoFactory();

    DebbieConfigurationCenter getConfigurationCenter();

    InjectedBeanFactory getInjectedBeanFactory();

    GlobalBeanFactory getGlobalBeanFactory();

    void refreshBeans();

    <O, T> T transform(final O origin, final Class<T> target);

    <T> T factory(String beanName);

    <T> T factory(Class<T> beanType);

    void release(String... args);
}
