/**
 * Copyright (c) 2022 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 * http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 *//*

package com.truthbean.debbie.controversial;

import com.truthbean.debbie.core.ApplicationContext;

import java.util.Map;
import java.util.function.Supplier;

*/
/**
 * @author TruthBean/Rogar·Q
 * @since 0.1.0
 * Created on 2020-06-24 19:59.
 *//*

interface ReflectionBeanCreator<Bean> extends BaseBeanCreator {

    FactoryBeanInfo<Bean> getBeanInfo();

    void createPreparation(Map<FactoryBeanInfo<?>, ReflectionBeanCreator<?>> singletonBeanCreatorMap, Object firstParamValue);

    void createPreparationByDependence(ApplicationContext applicationContext);

    void postConstructor();

    void postPreparation(Map<FactoryBeanInfo<?>, ReflectionBeanCreator<?>> singletonBeanCreatorMap);

    void postCreated();

    boolean isCreated();

    void create(Bean bean);

    void create(Supplier<Bean> bean);

    Bean getCreatedBean();
}
*/
