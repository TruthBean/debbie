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
import com.truthbean.debbie.data.TextData;
import com.truthbean.debbie.properties.PropertiesException;
import com.truthbean.debbie.properties.PropertiesHelper;

import java.nio.charset.Charset;
import java.util.*;

/**
 * @author TruthBean/Rogar·Q
 * @since 0.5.0
 */
public interface EnvironmentContent extends TextData {

    /**
     * 获取优先级，数值越大越优先获取，加载顺序反之
     */
    int getPriority();

    String getProfile();

    Properties getProperties();

    Logger getLogger();

    default boolean containKey(String key) {
        return getProperties().containsKey(key);
    }

    default Map<String, String> getMatchedKey(String keyPrefix) {
        if (keyPrefix == null || keyPrefix.isBlank()) {
            throw new PropertiesException("illegal keyPrefix");
        }
        var properties = getProperties();
        Map<String, String> result = new HashMap<>();
        properties.forEach((k, v) -> {
            if (k instanceof String && ((String) k).startsWith(keyPrefix)) {
                result.put((String) k, (String) v);
            }
        });
        return result;
    }

    default String getValue(String key) {
        if (key == null || key.isBlank()) {
            throw new PropertiesException("illegal key");
        }
        var properties = getProperties();
        String value = null;
        if (properties.containsKey(key)) {
            value = PropertiesHelper.replaceProperty(properties.getProperty(key, null), properties);
        }
        return value;
    }

    default String getStringValue(String key, String defaultValue) {
        var value = getValue(key);
        if (value == null) {
            value = defaultValue;
        }
        return value;
    }

    default char getCharacterValue(String key, char defaultValue) {
        var value = getValue(key);
        return getCharacter(value, defaultValue);
    }

    default boolean getBooleanValue(String key, boolean defaultValue) {
        var value = getValue(key);
        return getBoolean(value, defaultValue);
    }

    default int getIntegerValue(String key, int defaultValue) {
        var value = getValue(key);
        return getInteger(value, defaultValue);
    }

    default double getDoubleValue(String key, double defaultValue) {
        var value = getValue(key);
        return getDouble(value, defaultValue);
    }

    default long getLongValue(String key, long defaultValue) {
        var value = getValue(key);
        return getLong(value, defaultValue);
    }

    default String[] getStringArrayValue(String key, String split) {
        var value = getValue(key);
        return getStringArray(value, split);
    }

    default Charset getCharsetValue(String key, Charset defaultCharset) {
        var value = getValue(key);
        return getCharset(value, defaultCharset);
    }

    default Map<String, String> getMapValue(String key, String keyValueSplit, String split) {
        var value = getValue(key);
        return getMap(value, keyValueSplit, split);
    }

    default List<String> getStringListValue(String key, String split) {
        var value = getStringArrayValue(key, split);
        List<String> result = null;
        if (value != null) {
            result = Arrays.asList(value);
        }
        return result;
    }

    default List<Class<?>> getClassListValue(String key, String split) {
        var value = getValue(key);
        return getClassList(value, split);
    }

    default Class<?> getClassValue(String key, String defaultClass) {
        var value = getValue(key);
        return getClass(value, defaultClass);
    }

    default Set<Class<?>> getClassSetValue(String key, String split) {
        var value = getValue(key);
        return getClassSet(value, split);
    }
}
