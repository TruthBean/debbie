/**
 * Copyright (c) 2020 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *         http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.boot;

import com.truthbean.debbie.bean.BeanComponentParser;
import com.truthbean.debbie.bean.BeanInitialization;
import com.truthbean.debbie.core.ApplicationContext;
import com.truthbean.debbie.properties.DebbieConfigurationCenter;

import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author TruthBean
 * @since 0.0.2
 */
public interface DebbieModuleStarter extends Comparable<DebbieModuleStarter> {

    int getOrder();

    default Map<Class<? extends Annotation>, BeanComponentParser> getComponentAnnotation() {
        return new HashMap<>();
    }

    default void registerBean(ApplicationContext applicationContext, BeanInitialization beanInitialization) {
        // do nothing
    }

    default void configure(DebbieConfigurationCenter configurationFactory, ApplicationContext applicationContext) {
        // do nothing
    }

    default void starter(DebbieConfigurationCenter configurationFactory, ApplicationContext applicationContext) {
        // do nothing
    }

    default void postStarter(ApplicationContext applicationContext) {
        // do nothing
    }

    default void release(DebbieConfigurationCenter configurationFactory, ApplicationContext applicationContext) {
        // do nothing
    }

    @Override
    default int compareTo(DebbieModuleStarter o) {
        if (o != null) {
            int x = getOrder();
            int y = o.getOrder();
            return Integer.compare(x, y);
        }
        return -1;
    }

    default String toStr() {
        return getClass().getName() + "@" + Integer.toHexString(hashCode()) + " | order : " + getOrder();
    }
}
