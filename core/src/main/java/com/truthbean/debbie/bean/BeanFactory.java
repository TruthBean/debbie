/**
 * Copyright (c) 2022 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *         http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.bean;

import com.truthbean.Logger;
import com.truthbean.LoggerFactory;
import com.truthbean.debbie.core.ApplicationContext;
import com.truthbean.debbie.proxy.BeanProxyType;

import java.lang.reflect.Proxy;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * @author TruthBean
 * @since 0.0.2
 * Created on 2019/06/02 16:50.
 */
public interface BeanFactory<Bean> extends RegistrableBeanInfo<Bean>, BeanClosure {

    default Bean factoryBean(ApplicationContext applicationContext) {
        return factoryNamedBean(null, applicationContext);
    }

    Bean factoryNamedBean(String name, ApplicationContext applicationContext);

    /**
     * if isCreated() and isProxiedBean()
     *   return getCreatedBean();
     * else
     *   factory and proxy
     * @param name bean's name
     * @param beanInterface BEAN's interface
     * @param applicationContext debbie's applicationContext
     * @return BEAN's proxy
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    default Bean factoryProxiedBean(String name, Class beanInterface, ApplicationContext applicationContext) {
        Bean bean;
        if (!isCreated()) {
            bean = factoryNamedBean(name, applicationContext);
        } else {
            bean = getCreatedBean();
        }
        if (isCreated() && beanInterface != null && beanInterface.isInterface() && beanInterface.isInstance(bean)) {
            bean = getCreatedBean();
            if (!(bean instanceof Proxy)) {
                Set<BeanLifecycle> beanLifecycles = applicationContext.getBeanLifecycle();
                for (BeanLifecycle beanLifecycle : beanLifecycles) {
                    if (beanLifecycle.support(getBeanClass()) && beanLifecycle.support(this)) {
                        Bean proxy = (Bean) beanLifecycle.doPreCreated(this, bean, beanInterface, BeanProxyType.JDK);
                        if (proxy instanceof Proxy) {
                            return proxy;
                        }
                    }
                }
            }
            return bean;
        }
        return bean;
    }

    /**
     * is bean proxied
     *
     * @return boolean or null. if not created return null
     */
    default Boolean isProxiedBean() {
        if (isCreated()) {
            return getCreatedBean() instanceof Proxy;
        }
        return null;
    }

    @Override
    boolean isCreated();

    /**
     * @return if BeanFactory.isCreated() return bean
     * else return null
     */
    Bean getCreatedBean();

    default Optional<Bean> optional() {
        if (isCreated()) {
            return Optional.of(getCreatedBean());
        } else {
            return Optional.empty();
        }
    }

    /**
     * @param consumer acquire created bean
     */
    default void acquireBean(Consumer<Bean> consumer) {
        if (isCreated()) {
            consumer.accept(getCreatedBean());
        }
    }

    default Supplier<Bean> getBeanSupplier(final ApplicationContext applicationContext) {
        if (isCreated()) {
            return this::getCreatedBean;
        } else {
            return () -> factoryBean(applicationContext);
        }
    }

    @Override
    default BeanFactory<Bean> copy() {
        return this;
    }

    default Logger getLogger() {
        return LoggerFactory.getLogger(getClass());
    }
}
