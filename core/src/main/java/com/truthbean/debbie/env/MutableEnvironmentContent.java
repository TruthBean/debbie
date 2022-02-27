/**
 * Copyright (c) 2022 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 * http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.env;

import com.truthbean.Logger;

import java.util.Properties;

/**
 * @author TruthBean/Rogar·Q
 * @since 0.5.0
 */
public interface MutableEnvironmentContent extends EnvironmentContent, EnvironmentContentLoggerSetter {

    default void addProperty(String name, String value) {
        Properties properties = getProperties();
        properties.put(name, value);
    }

    default void reset() {
        getProperties().clear();
    }
}
