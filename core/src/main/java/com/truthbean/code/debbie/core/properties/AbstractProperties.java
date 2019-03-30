package com.truthbean.code.debbie.core.properties;

import com.truthbean.code.debbie.core.bean.BeanScanConfiguration;
import com.truthbean.code.debbie.core.reflection.ClassLoaderUtils;
import com.truthbean.code.debbie.core.util.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * @author TruthBean
 * @since 0.0.1
 * Created on 2018-03-19 12:27.
 */
public class AbstractProperties {

    /**
     * slf4j logger
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractProperties.class);

    /**
     * properties
     */
    private static final Properties PROPERTIES = new Properties();

    static {
        readPropertiesFile();
    }

    /**
     * read from properties file
     */
    private static void readPropertiesFile() {
        var classLoader = ClassLoaderUtils.getClassLoader(AbstractProperties.class);
        var url = classLoader.getResource(Constants.APPLICATION_PROPERTIES);
        if (url == null) {
            LOGGER.warn(Constants.APPLICATION_PROPERTIES + " not found，You SHOULD not use with properties function.");
        } else {
            InputStream inputStream;
            try {
                inputStream = url.openStream();
                var reader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
                PROPERTIES.load(reader);
            } catch (IOException e) {
                LOGGER.error("load properties error", e);
            }
        }
    }

    public static <C extends BeanScanConfiguration> C loadProperties() {
        throw new UnsupportedOperationException();
    }

    public static boolean isPropertiesEmpty() {
        return PROPERTIES.isEmpty();
    }

    public Properties getProperties() {
        var properties = AbstractProperties.PROPERTIES;
        if (properties.isEmpty()) {
            AbstractProperties.readPropertiesFile();
        }
        return properties;
    }

    public String getValue(String key) {
        if (key == null || "".equals(key.trim())) {
            throw new RuntimeException("illegal key");
        }
        var properties = getProperties();
        String value = null;
        if (properties.containsKey(key)) {
            value = properties.getProperty(key);
        }
        return value;
    }

    public String getStringValue(String key, String defaultValue) {
        var value = getValue(key);
        if (value == null) {
            value = defaultValue;
        }
        return value;
    }

    public Boolean getBooleanValue(String key, boolean defaultValue) {
        var value = getValue(key);
        Boolean result;
        if (value == null) {
            result = defaultValue;
        } else {
            result = Boolean.valueOf(value);
        }
        return result;
    }

    public Integer getIntegerValue(String key, int defaultValue) {
        var value = getValue(key);
        Integer result = null;
        if (value == null) {
            result = defaultValue;
        } else {
            try {
                result = Integer.valueOf(value);
            } catch (Exception e) {
                LOGGER.error(value + " to int error", e);
            }
        }
        return result;
    }

    public Long getLongValue(String key, long defaultValue) {
        var value = getValue(key);
        Long result = null;
        if (value == null) {
            result = defaultValue;
        } else {
            try {
                result = Long.valueOf(value);
            } catch (Exception e) {
                LOGGER.error(value + " to long error", e);
            }
        }
        return result;
    }

    public String[] getStringArrayValue(String key, String split) {
        var value = getValue(key);
        if (split == null || "".equals(split)) {
            throw new RuntimeException("illegal split");
        }
        if (value != null) {
            return value.split(split);
        }
        return null;
    }

    public List<String> getStringListValue(String key, String split) {
        var value = getStringArrayValue(key, split);
        List<String> result = null;
        if (value != null) {
            result = Arrays.asList(value);
        }
        return result;
    }

    public List<Class> getClassListValue(String key, String split) {
        var value = getStringArrayValue(key, split);
        var classLoader = ClassLoaderUtils.getDefaultClassLoader();
        List<Class> result = null;
        if (value != null) {
            result = new ArrayList<>();
            for (var className : value) {
                if (className != null) {
                    try {
                        result.add(classLoader.loadClass(className));
                    } catch (ClassNotFoundException e) {
                        LOGGER.error("class (" + className + ") not found", e);
                    }
                }
            }
        }
        return result;
    }

    public Set<Class<?>> getClassSetValue(String key, String split) {
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
                        LOGGER.error("class (" + className + ") not found", e);
                    }
                }
            }
        }
        return result;
    }

}