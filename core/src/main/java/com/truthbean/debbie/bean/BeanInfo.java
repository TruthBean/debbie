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

import com.truthbean.debbie.util.StringUtils;

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

    @SuppressWarnings("unchecked")
    default Class<Bean> getClazz() {
        return (Class<Bean>) getBean().getClass();
    }

    BeanType getBeanType();

    default String getServiceName() {
        Set<String> beanNames = getBeanNames();
        String name = beanNames.isEmpty() ? null : beanNames.iterator().next();
        if (name == null || name.isBlank()) {
            name = getClazz().getSimpleName();
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
}