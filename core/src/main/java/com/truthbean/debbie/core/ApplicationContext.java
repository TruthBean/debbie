/**
 * Copyright (c) 2021 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 * http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.core;

import com.truthbean.debbie.bean.*;
import com.truthbean.debbie.boot.ApplicationArgs;
import com.truthbean.debbie.env.EnvironmentContent;
import com.truthbean.debbie.event.DebbieEventPublisher;
import com.truthbean.debbie.io.ResourceResolver;

import java.util.Set;

/**
 * @author TruthBean
 * @since 0.1.0
 */
public interface ApplicationContext extends DebbieEventPublisher {
    /**
     * after DebbieApplication#start call
     * @return application args
     */
    ApplicationArgs getApplicationArgs();

    ClassLoader getClassLoader();

    EnvironmentContent getEnvContent();

    ResourceResolver getResourceResolver();

    BeanInfoManager getBeanInfoManager();

    GlobalBeanFactory getGlobalBeanFactory();

    Set<BeanLifecycle> getBeanLifecycle();

    <T extends I, I> void registerSingleBean(Class<I> beanClass, T bean, String... names);

    // <E extends AbstractDebbieEvent, EL extends DebbieEventListener<E>> void registerEventListener(Class<E> eventClass, EL listener);

    void registerBeanLifecycle(BeanLifecycle beanLifecycle);

    // void refreshBeans();

    <O, T> T transform(final O origin, final Class<T> target);

    <T> T factory(String beanName);

    <T> T factory(Class<T> beanType);

    void release(String... args);

    boolean isExiting();
}
