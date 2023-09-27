/**
 * Copyright (c) 2023 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 * http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 *//*

package com.truthbean.debbie.controversial;

import com.truthbean.Logger;
import com.truthbean.LoggerFactory;
import com.truthbean.debbie.bean.BeanClosure;
import com.truthbean.debbie.bean.BeanFactory;
import com.truthbean.debbie.bean.BeanInfo;
import com.truthbean.debbie.core.ApplicationContext;

import java.io.Closeable;
import java.io.IOException;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;

*/
/**
 * @author TruthBean/Rogar·Q
 * @since 0.1.0
 * Created on 2020-09-28 16:09
 *//*

interface FactoryBeanInfo<Bean> extends BeanInfo<Bean> {

    default BeanFactory<Bean> getBeanFactory() {
        return null;
    }

    default boolean hasBeanFactory() {
        return getBeanFactory() != null;
    }

    default boolean hasSkipCreatedBeanFactory() {
        return hasBeanFactory() && SkipCreatedBeanFactory.class.isAssignableFrom(getBeanFactory().getClass());
    }

    @Override
    Class<Bean> getBeanClass();

    Bean getBean();

    default Optional<Bean> optional() {
        return Optional.ofNullable(getBean());
    }

    default boolean isEmpty() {
        return getBean() == null;
    }

    default boolean isPresent() {
        return getBean() != null;
    }

    default Supplier<Bean> getBeanSupplier() {
        return this::getBean;
    }

    default void consumer(Consumer<Bean> consumer) {
        consumer.accept(getBean());
    }

    default Bean create(ApplicationContext applicationContext) {
        if (!isPresent() && hasBeanFactory()) {
            return getBeanFactory().factoryBean(applicationContext);
        }
        return getBean();
    }

    @Override
    default FactoryBeanInfo<Bean> copy() {
        return this;
    }

    default void close(ApplicationContext applicationContext) {
        Class<Bean> beanClass = getBeanClass();
        if (!BeanFactory.class.isAssignableFrom(beanClass) && getBean() != null) {
            var bean = getBean();
            if (BeanClosure.class.isAssignableFrom(beanClass)) {
                ((BeanClosure) bean).destruct(applicationContext);
            }
            if (Closeable.class.isAssignableFrom(beanClass)) {
                try {
                    ((Closeable) bean).close();
                } catch (IOException e) {
                    LOGGER.error("", e);
                }
            }
            if (AutoCloseable.class.isAssignableFrom(beanClass)) {
                try {
                    ((AutoCloseable) bean).close();
                } catch (Exception e) {
                    LOGGER.error("", e);
                }
            }
        }
    }

    default void destruct(ApplicationContext applicationContext) {
        BeanFactory<Bean> beanFactory = getBeanFactory();
        if (beanFactory != null) {
            beanFactory.destruct(applicationContext);
        } else {
            close(applicationContext);
        }
    }

    Logger LOGGER = LoggerFactory.getLogger(FactoryBeanInfo.class);
}*/
