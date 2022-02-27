/**
 * Copyright (c) 2022 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 * http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.bean;

import java.util.Optional;
import java.util.Set;
import java.util.function.Supplier;

/**
 * @author TruthBean/Rogar·Q
 * @since 0.1.0
 * Created on 2020-07-01 11:32.
 */
public interface GlobalBeanFactory {

    <T> T factory(String beanName);

    <T> T factory(Class<T> type);

    <T> T factoryWithoutProxy(Class<T> type);

    <T> T factory(String serviceName, Class<T> type, boolean required);

    <T> T factoryIfPresent(Class<T> type);

    <T> T factoryIfPresentOrElse(Class<T> type, Supplier<T> otherFactory);

    <T> Optional<T> factoryIfPresent(String beanName);

    <T> Supplier<T> supply(String beanName);

    <T> Supplier<T> supply(Class<T> type);

    <T> void factoryByRawBean(T rawBean);

    <T> T factoryByNoBean(Class<T> noBeanType);

    // <T, K extends T> K factory(BeanInfo<T> beanInfo);

    // <T> T factoryBeanByDependenceProcessor(FactoryBeanInfo<T> beanInfo, boolean skipFactory);

    // <T> T factoryBeanByDependenceProcessor(FactoryBeanInfo<T> beanInfo, boolean skipFactory, Object firstParamValue);

    <Bean> Set<Bean> getBeanList(Class<Bean> superType);

    // <Bean> List<Bean> getBeanList(Class<Bean> superType, boolean withoutProxy);

    <T> boolean containsBean(Class<T> beanType);

    boolean containsBean(String beanName);

    // <T> T getBeanByFactory(FactoryBeanInfo<T> beanInfo);

    // <T> T getBeanByFactory(Class<T> beanClass, BeanFactory<T> beanFactory);
}
