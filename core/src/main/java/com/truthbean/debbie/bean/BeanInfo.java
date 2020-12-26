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

import com.truthbean.Logger;
import com.truthbean.debbie.util.StringUtils;
import com.truthbean.logger.LoggerFactory;

import java.io.Closeable;
import java.io.IOException;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * @author TruthBean/Rogar·Q
 * @since 0.1.0
 * Created on 2020-09-28 16:09
 */
public interface BeanInfo<Bean> {

    default boolean isLazyCreate() {
        return true;
    }

    default BeanFactory<Bean> getBeanFactory() {
        return null;
    }

    default boolean hasBeanFactory() {
        return getBeanFactory() != null;
    }

    default boolean isSingleton() {
        var beanType = getBeanType();
        return beanType == BeanType.SINGLETON;
    }

    Class<Bean> getBeanClass();

    BeanType getBeanType();

    default String getServiceName() {
        Set<String> beanNames = getBeanNames();
        String name = beanNames.isEmpty() ? null : beanNames.iterator().next();
        if (name == null || name.isBlank()) {
            name = getBeanClass().getSimpleName();
            name = StringUtils.toFirstCharLowerCase(name);
            beanNames.add(name);
        }
        return name;
    }

    default boolean containName(String name) {
        Set<String> beanNames = getBeanNames();
        return beanNames.contains(name);
    }

    default Set<String> getBeanNames() {
        return new HashSet<>();
    }

    Bean getBean();

    default Optional<Bean> optional() {
        return Optional.ofNullable(getBean());
    }

    default boolean isEmpty() {
        return getBean() == null;
    }

    default  boolean isPresent() {
        return getBean() != null;
    }

    default Supplier<Bean> getBeanSupplier() {
        return this::getBean;
    }

    default void consumer(Consumer<Bean> consumer) {
        consumer.accept(getBean());
    }

    default BeanInfo<Bean> copy() {
        return this;
    }

    default void release() {
        BeanFactory<Bean> beanFactory = getBeanFactory();
        if (beanFactory != null) {
            beanFactory.destroy();
        } else {
            Class<Bean> beanClass = getBeanClass();
            if (!DebbieBeanFactory.class.isAssignableFrom(beanClass) && getBean() != null) {
                var bean = getBean();
                if (BeanClosure.class.isAssignableFrom(beanClass)) {
                    ((BeanClosure) bean).destroy();
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
    }

    static final Logger LOGGER = LoggerFactory.getLogger(BeanInfo.class);
}