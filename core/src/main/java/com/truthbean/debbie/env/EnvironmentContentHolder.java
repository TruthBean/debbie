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

import java.util.Properties;

/**
 * @author TruthBean/Rogar·Q
 * @since 0.5.0
 * Created on 2021-02-19 21:25
 */
public class EnvironmentContentHolder implements EnvironmentContent {

    static {
        SimpleEnvironmentContentProfile.reload();
    }

    private static final Properties CACHE = new Properties();
    private static final SimpleEnvironmentContentProfile PROFILE = new SimpleEnvironmentContentProfile();

    private Logger logger;

    @Override
    public int getPriority() {
        return -1;
    }

    @Override
    public String getProfile() {
        return "all";
    }

    public EnvironmentContentHolder setLogger(Logger logger) {
        this.logger = logger;
        PROFILE.setAllEnvLogger(logger);
        return this;
    }

    @Override
    public Logger getLogger() {
        return logger;
    }

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
        return PROFILE.getAllProperties();
    }

    public static void clear() {
        CACHE.clear();
        PROFILE.clear();
    }
}
