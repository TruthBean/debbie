/**
 * Copyright (c) 2020 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *         http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.bean;

import com.truthbean.debbie.properties.DebbieConfiguration;

import java.util.HashMap;
import java.util.Map;

/**
 * @author truthbean/Rogar·Q
 * @since 0.0.2
 */
public class DebbieConfigurationCenter {
    private static final Map<Class<? extends DebbieConfiguration>, DebbieConfiguration> configurations = new HashMap<>();

    public static <C extends DebbieConfiguration> void addConfiguration(C configuration) {
        configurations.put(configuration.getClass(), configuration);
    }

    @SuppressWarnings("unchecked")
    public static <C extends DebbieConfiguration> C getConfiguration(Class<C> configurationClass) {
        return (C) configurations.get(configurationClass);
    }

    public static void clear() {
        configurations.clear();
    }
}
