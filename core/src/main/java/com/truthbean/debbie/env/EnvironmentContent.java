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
import com.truthbean.debbie.properties.PropertiesException;
import com.truthbean.debbie.properties.PropertiesHelper;
import com.truthbean.debbie.reflection.ClassLoaderUtils;

import java.nio.charset.Charset;
import java.util.*;

/**
 * @author TruthBean/Rogar·Q
 * @since 0.5.0
 */
public interface EnvironmentContent {

    Properties getProperties();

    EnvironmentContent setLogger(Logger logger);

    Logger getLogger();

    default void addProperty(String name, String value) {
        Properties properties = getProperties();
        properties.put(name, value);
    }

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
        char result;
        if (value == null) {
            result = defaultValue;
        } else {
            try {
                result = value.charAt(0);
            } catch (Exception e) {
                getLogger().error(value + " to char error", e);
                result = defaultValue;
            }
        }
        return result;
    }

    default boolean getBooleanValue(String key, boolean defaultValue) {
        var value = getValue(key);
        boolean result;
        if (value == null) {
            result = defaultValue;
        } else {
            try {
                result = Boolean.parseBoolean(value);
            } catch (Exception e) {
                getLogger().error(value + " to bool error", e);
                result = defaultValue;
            }
        }
        return result;
    }

    default int getIntegerValue(String key, int defaultValue) {
        var value = getValue(key);
        int result;
        if (value == null) {
            result = defaultValue;
        } else {
            try {
                result = Integer.parseInt(value);
            } catch (Exception e) {
                getLogger().error(value + " to int error", e);
                result = defaultValue;
            }
        }
        return result;
    }

    default double getDoubleValue(String key, double defaultValue) {
        var value = getValue(key);
        double result;
        if (value == null) {
            result = defaultValue;
        } else {
            try {
                result = Double.parseDouble(value);
            } catch (Exception e) {
                getLogger().error(value + " to double error", e);
                result = defaultValue;
            }
        }
        return result;
    }

    default long getLongValue(String key, long defaultValue) {
        var value = getValue(key);
        long result;
        if (value == null) {
            result = defaultValue;
        } else {
            try {
                result = Long.parseLong(value);
            } catch (Exception e) {
                getLogger().error(value + " to long error", e);
                result = defaultValue;
            }
        }
        return result;
    }

    default String[] getStringArrayValue(String key, String split) {
        var value = getValue(key);
        if (split == null || split.isBlank()) {
            throw new PropertiesException("illegal split");
        }
        if (value != null) {
            return value.split(split);
        }
        return null;
    }

    default Charset getCharsetValue(String key, Charset defaultCharset) {
        var value = getValue(key);
        Charset result;
        if (value == null) {
            result = defaultCharset;
        } else {
            try {
                result = Charset.forName(value);
            } catch (Exception e) {
                getLogger().error(value + " to Charset error", e);
                result = defaultCharset;
            }
        }
        return result;
    }

    default Map<String, String> getMapValue(String key, String keyValueSplit, String split) {
        var value = getValue(key);
        if (split == null || split.isBlank()) {
            throw new PropertiesException("illegal split");
        }
        if (value != null) {
            String[] splitValue = value.split(split);
            Map<String, String> result = new HashMap<>();
            for (String s : splitValue) {
                if (s.contains(keyValueSplit)) {
                    String[] keyValue = s.split(keyValueSplit);
                    if (keyValue.length == 2) {
                        result.put(keyValue[0], keyValue[1]);
                    } else {
                        throw new IllegalArgumentException("key and value must split by " + keyValueSplit);
                    }
                }
            }
            return result;
        }
        return null;
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
        var value = getStringArrayValue(key, split);
        var classLoader = ClassLoaderUtils.getDefaultClassLoader();
        List<Class<?>> result = null;
        if (value != null) {
            result = new ArrayList<>();
            for (var className : value) {
                if (className != null) {
                    try {
                        result.add(classLoader.loadClass(className));
                    } catch (ClassNotFoundException e) {
                        getLogger().error("class (" + className + ") not found", e);
                    }
                }
            }
        }
        return result;
    }

    default Class<?> getClassValue(String key, String defaultClass) {
        var className = getStringValue(key, defaultClass);
        var classLoader = ClassLoaderUtils.getDefaultClassLoader();
        Class<?> result = null;
        if (className != null) {
            try {
                result = classLoader.loadClass(className);
            } catch (ClassNotFoundException e) {
                getLogger().error("class (" + className + ") not found", e);
            }
        }
        return result;
    }

    default Set<Class<?>> getClassSetValue(String key, String split) {
        var value = getStringArrayValue(key, split);
        var classLoader = ClassLoaderUtils.getDefaultClassLoader();
        Set<Class<?>> result = null;
        if (value != null) {
            result = new HashSet<>();
            for (var className : value) {
                if (className != null) {
                    try {
                        result.add(classLoader.loadClass(className));
                    } catch (ClassNotFoundException e) {
                        getLogger().error("class (" + className + ") not found", e);
                    }
                }
            }
        }
        return result;
    }

    default void reset() {
        getProperties().clear();
    }
}
