/**
 * Copyright (c) 2023 TruthBean(Rogar·Q)
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

import java.lang.reflect.Proxy;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * @author TruthBean
 * @since 0.0.2
 * Created on 2019/06/02 16:50.
 */
public interface BeanFactory<Bean> extends RegistrableBeanInfo<Bean>, BeanClosure {

    Bean factoryBean(ApplicationContext applicationContext);

    // Bean factory(BeanInjection<Bean> beanInjection, ApplicationContext applicationContext);

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

    default Supplier<Bean> supply(final ApplicationContext applicationContext) {
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
