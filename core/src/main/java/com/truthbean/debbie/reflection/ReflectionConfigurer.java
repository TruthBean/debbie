/**
 * Copyright (c) 2024 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 * http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.reflection;

import com.truthbean.core.util.OsUtils;
import com.truthbean.debbie.environment.Environment;

/**
 * @author TruthBean/Rogar·Q
 * @since 0.5.0
 */
public class ReflectionConfigurer {
    private static Environment environment;

    public static final String ENABLE_KEY = "debbie.reflect.enable";

    public ReflectionConfigurer(Environment environment) {
        ReflectionConfigurer.environment = environment;
    }

    public static boolean enable() {
        return environment.getBooleanValue(ENABLE_KEY, true);
    }

    public static boolean isReflectEnable(Environment environment) {
        return environment.getBooleanValue(ENABLE_KEY, true);
    }

    public static boolean isReflectEnable(Environment environment, Environment defaultEnvironment) {
        if (environment != null && environment.containKey(ENABLE_KEY)) {
            return environment.getBooleanValue(ENABLE_KEY, true);
        }
        if (defaultEnvironment != null) {
            return defaultEnvironment.getBooleanValue(ENABLE_KEY, true);
        } else {
            return false;
        }
    }

    public boolean isReflectEnable() {
        return !OsUtils.nonJvm() && environment.getBooleanValue(ENABLE_KEY, true);
    }
}
