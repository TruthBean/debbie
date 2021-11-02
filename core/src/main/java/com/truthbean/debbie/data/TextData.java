package com.truthbean.debbie.data;

import com.truthbean.Logger;
import com.truthbean.debbie.properties.PropertiesException;
import com.truthbean.debbie.reflection.ClassLoaderUtils;

import java.nio.charset.Charset;
import java.util.*;

/**
 * @author TruthBean
 * @since 0.5.3
 */
public interface TextData {

    default String getText(String rawData, String defaultData) {
        if (rawData == null) {
            return defaultData;
        }
        return rawData;
    }

    default char getCharacter(String rawData, char defaultData) {
        if (rawData == null) {
            return defaultData;
        }
        try {
            return rawData.charAt(0);
        } catch (Exception e) {
            getLogger().error(rawData + " to char error", e);
            return defaultData;
        }
    }

    default boolean getBoolean(String rawData, boolean defaultValue) {
        boolean result;
        if (rawData == null) {
            result = defaultValue;
        } else {
            try {
                result = Boolean.parseBoolean(rawData);
            } catch (Exception e) {
                getLogger().error(rawData + " to bool error", e);
                result = defaultValue;
            }
        }
        return result;
    }

    default int getInteger(String rawData, int defaultValue) {
        int result;
        if (rawData == null) {
            result = defaultValue;
        } else {
            try {
                result = Integer.parseInt(rawData);
            } catch (Exception e) {
                getLogger().error(rawData + " to int error", e);
                result = defaultValue;
            }
        }
        return result;
    }

    default double getDouble(String rawData, double defaultValue) {
        double result;
        if (rawData == null) {
            result = defaultValue;
        } else {
            try {
                result = Double.parseDouble(rawData);
            } catch (Exception e) {
                getLogger().error(rawData + " to double error", e);
                result = defaultValue;
            }
        }
        return result;
    }

    default long getLong(String rawData, long defaultValue) {
        long result;
        if (rawData == null) {
            result = defaultValue;
        } else {
            try {
                result = Long.parseLong(rawData);
            } catch (Exception e) {
                getLogger().error(rawData + " to long error", e);
                result = defaultValue;
            }
        }
        return result;
    }

    default String[] getStringArray(String rawData, String split) {
        if (split == null || split.isBlank()) {
            throw new PropertiesException("illegal split");
        }
        if (rawData != null) {
            return rawData.split(split);
        }
        return null;
    }

    default Charset getCharset(String rawData, Charset defaultCharset) {
        Charset result;
        if (rawData == null) {
            result = defaultCharset;
        } else {
            try {
                result = Charset.forName(rawData);
            } catch (Exception e) {
                getLogger().error(rawData + " to Charset error", e);
                result = defaultCharset;
            }
        }
        return result;
    }

    default Map<String, String> getMap(String rawData, String keyValueSplit, String split) {
        if (split == null || split.isBlank()) {
            throw new PropertiesException("illegal split");
        }
        if (rawData != null) {
            String[] splitValue = rawData.split(split);
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

    default List<String> getStringList(String rawData, String split) {
        var value = getStringArray(rawData, split);
        List<String> result = null;
        if (value != null) {
            result = Arrays.asList(value);
        }
        return result;
    }

    default List<Class<?>> getClassList(String rawData, String split) {
        var value = getStringArray(rawData, split);
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

    default Class<?> getClass(String rawData, String defaultClass) {
        var className = getText(rawData, defaultClass);
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

    default Set<Class<?>> getClassSet(String rawData, String split) {
        var value = getStringArray(rawData, split);
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

    Logger getLogger();
}
