/**
 * Copyright (c) 2021 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 * http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.bean;

import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

/**
 * @author TruthBean/Rogar·Q
 * @since 0.1.0
 * Created on 2020-07-01 11:32.
 */
public interface GlobalBeanFactory {

    <T> T factory(String serviceName);

    <T> T factory(Class<T> type);

    <T> T factoryIfPresent(Class<T> type);

    <T> Optional<T> factoryIfPresent(String beanName);

    <T> Supplier<T> supply(String beanName);

    <T> Supplier<T> supply(Class<T> type);

    <T> void factoryByRawBean(T rawBean);

    <T> T factoryByNoBean(Class<T> noBeanType);

    <T, K extends T> T factory(BeanInfo<K> beanInfo);

    <T> T factoryBeanByDependenceProcessor(BeanInfo<T> beanInfo, boolean skipFactory);

    <T> T factoryBeanByDependenceProcessor(BeanInfo<T> beanInfo, boolean skipFactory, Object firstParamValue);

    <T> BeanInfo<T> getBeanInfoWithBean(Class<T> type);

    <T, K extends T> List<K> getBeanList(Class<T> superType);

    <T, K extends T> List<K> getBeanList(Class<T> superType, boolean withoutProxy);

    <T> boolean containsBean(Class<T> beanType);

    boolean containsBean(String beanName);

    <T> T getBeanByFactory(BeanInfo<T> beanInfo);

    <T> T getBeanByFactory(Class<T> beanClass, BeanFactory<T> beanFactory);
}
