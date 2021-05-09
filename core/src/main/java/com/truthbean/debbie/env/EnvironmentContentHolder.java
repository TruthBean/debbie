/**
 * Copyright (c) 2021 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 * http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.env;

import com.truthbean.Logger;
import com.truthbean.debbie.properties.BaseProperties;
import com.truthbean.debbie.spi.SpiLoader;

import java.util.Properties;

/**
 * @author TruthBean/Rogar·Q
 * @since 0.5.0
 * Created on 2021-02-19 21:25
 */
public class EnvironmentContentHolder implements EnvironmentContent {
    private static final EnvironmentContent ENV_CONTENT;

    private static final Properties CACHE = new Properties();

    static {
        EnvironmentContent environmentContent;
        try {
            environmentContent = SpiLoader.loadProvider(EnvironmentContent.class);
        } catch (Exception e) {
            System.getLogger(EnvironmentContentHolder.class.getName())
                    .log(System.Logger.Level.ERROR, "load com.truthbean.debbie.env.EnvironmentContent error.", e);
            environmentContent = new BaseProperties();
        }
        ENV_CONTENT = environmentContent;
    }

    public EnvironmentContentHolder() {
    }

    public static EnvironmentContent getEnvironmentContent() {
        return ENV_CONTENT;
    }

    @Override
    public EnvironmentContentHolder setLogger(Logger logger) {
        ENV_CONTENT.setLogger(logger);
        return this;
    }

    @Override
    public Logger getLogger() {
        return ENV_CONTENT.getLogger();
    }

    @Override
    public void addProperty(String name, String value) {
        CACHE.put(name, value);
    }

    @Override
    public boolean containKey(String key) {
        return CACHE.containsKey(key);
    }

    public static boolean isPropertiesEmpty() {
        return CACHE.isEmpty();
    }

    @Override
    public Properties getProperties() {
        if (CACHE.isEmpty()) {
            Properties result = new Properties();
            // OS environment variable
            var env = System.getenv();
            result.putAll(env);
            // project properties
            Properties properties = ENV_CONTENT.getProperties();
            if (properties != null && !properties.isEmpty()) {
                // custom properties will cover system properties
                result.putAll(properties);
            }
            // jvm properties
            var systemProperties = System.getProperties();
            result.putAll(systemProperties);
            CACHE.putAll(result);
        }
        return CACHE;
    }

    public static void clear() {
        CACHE.clear();
        ENV_CONTENT.reset();
    }
}
