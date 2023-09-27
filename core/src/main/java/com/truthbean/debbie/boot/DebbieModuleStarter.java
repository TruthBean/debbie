/**
 * Copyright (c) 2023 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *         http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.boot;

import com.truthbean.debbie.bean.BeanComponentParser;
import com.truthbean.debbie.bean.BeanInfoManager;
import com.truthbean.debbie.core.ApplicationContext;
import com.truthbean.debbie.environment.Environment;

import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.Map;

/**
 * @author TruthBean
 * @since 0.0.2
 */
public interface DebbieModuleStarter extends Comparable<DebbieModuleStarter> {

    /**
     * dynamic enable the module
     * @param environment environment content
     * @return is enabled using the module
     */
    default boolean enable(Environment environment) {
        return !environment.getBoolean(DebbieApplication.DISABLE_DEBBIE, false);
    }

    int getOrder();

    default Map<Class<? extends Annotation>, BeanComponentParser> getComponentAnnotation() {
        return new HashMap<>();
    }

    default void registerBean(ApplicationContext applicationContext, BeanInfoManager beanInfoManager) {
        // do nothing
    }

    default void configure(ApplicationContext applicationContext) {
        // do nothing
    }

    default void starter(ApplicationContext applicationContext) {
        // do nothing
    }

    default void postStarter(ApplicationContext applicationContext) {
        // do nothing
    }

    default void release(ApplicationContext applicationContext) {
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
