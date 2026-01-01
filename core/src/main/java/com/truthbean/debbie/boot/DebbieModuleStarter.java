/**
 * Copyright (c) 2025 TruthBean(Rogar·Q)
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
        return !environment.getBooleanValue(DebbieApplication.DISABLE_DEBBIE, false);
    }

    /**
     * get the order of the module, the lower the order, the higher the priority
     * @return the order of the module
     */
    int getOrder();

    /**
     * custom the component annotation, such as @Mapper, etc.
     * @return the component annotation of the module
     */
    default Map<Class<? extends Annotation>, BeanComponentParser> getComponentAnnotation() {
        return new HashMap<>();
    }

    /**
     * register the bean by the module
     * @param applicationContext the application context
     * @param beanInfoManager the bean info manager
     */
    default void registerBean(ApplicationContext applicationContext, BeanInfoManager beanInfoManager) {
        // do nothing
    }

     /**
     * configure by module
     * @param applicationContext the application context
     */
    default void configure(ApplicationContext applicationContext) {
        // do nothing
    }

    /**
     * the module starter, start module
     * @param applicationContext the application context
     */
    default void starter(ApplicationContext applicationContext) {
        // do nothing
    }

    /**
     * post starter, after module starter
     * @param applicationContext the application context
     */
    default void postStarter(ApplicationContext applicationContext) {
        // do nothing
    }

    /**
     * release the module resources before application exit
     * @param applicationContext the application context
     */
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
