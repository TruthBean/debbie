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

import com.truthbean.debbie.bean.DebbieApplicationContext;
import com.truthbean.debbie.bean.BeanInitialization;
import com.truthbean.debbie.properties.DebbieConfigurationCenter;

/**
 * @author TruthBean
 * @since 0.0.2
 */
public interface DebbieModuleStarter extends Comparable<DebbieModuleStarter> {

    default void registerBean(DebbieApplicationContext applicationContext, BeanInitialization beanInitialization) {
        // do nothing
    }

    default void configure(DebbieConfigurationCenter configurationFactory, DebbieApplicationContext applicationContext) {
        // do nothing
    }

    default void starter(DebbieConfigurationCenter configurationFactory, DebbieApplicationContext applicationContext) {
        // do nothing
    }

    int getOrder();

    default void release(DebbieConfigurationCenter configurationFactory, DebbieApplicationContext applicationContext) {
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
