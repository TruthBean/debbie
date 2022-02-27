/**
 * Copyright (c) 2022 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 * http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.reflection;

import com.truthbean.common.mini.util.OsUtils;
import com.truthbean.debbie.env.EnvironmentContent;

/**
 * @author TruthBean/Rogar·Q
 * @since 0.5.0
 */
public class ReflectionConfigurer {
    private static EnvironmentContent envContent;

    public static final String ENABLE_KEY = "debbie.reflect.enable";

    public ReflectionConfigurer(EnvironmentContent envContent) {
        ReflectionConfigurer.envContent = envContent;
    }

    public static boolean enable() {
        return envContent.getBooleanValue(ENABLE_KEY, true);
    }

    public static boolean isReflectEnable(EnvironmentContent envContent) {
        return envContent.getBooleanValue(ENABLE_KEY, true);
    }

    public boolean isReflectEnable() {
        return !OsUtils.nonJvm() && envContent.getBooleanValue(ENABLE_KEY, true);
    }
}
