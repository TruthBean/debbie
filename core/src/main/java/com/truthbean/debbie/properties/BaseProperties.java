/**
 * Copyright (c) 2020 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 * http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.properties;

import com.truthbean.Logger;
import com.truthbean.debbie.data.serialize.JacksonYamlUtils;
import com.truthbean.debbie.reflection.ClassLoaderUtils;
import com.truthbean.debbie.util.Constants;
import com.truthbean.logger.LoggerFactory;

import java.io.*;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * @author TruthBean
 * @since 0.0.1
 * Created on 2018-03-19 12:27.
 */
public class BaseProperties {

    /**
     * logger
     */
    private final Logger logger;
    
    public BaseProperties() {
        this.logger = LoggerFactory.getLogger(BaseProperties.class);
    }

    public BaseProperties(Logger logger) {
        this.logger = logger;
    }

    /**
     * properties
     */
    private static final Properties PROPERTIES = new Properties();

    /**
     * read from properties file
     */
    private Properties readPropertiesFile() {
        var classLoader = ClassLoaderUtils.getClassLoader(BaseProperties.class);
        var applicationUrl = System.getProperty("debbie.application.properties", Constants.APPLICATION_PROPERTIES);
        if (applicationUrl == null) {
            logger.warn(() -> "debbie.application.properties value is null.");
        } else {
            logger.debug(() -> Constants.APPLICATION_PROPERTIES + ": " + applicationUrl);
            var url = classLoader.getResource(applicationUrl);
            if (url == null) {
                if (applicationUrl.equals(Constants.APPLICATION_PROPERTIES))
                    logger.warn(Constants.APPLICATION_PROPERTIES + " not found in classpath.");
                else {
                    logger.warn(applicationUrl + " not found in classpath.");
                }
                // read via file
                File file = new File(applicationUrl);
                if (file.exists()) {
                    logger.debug(() -> Constants.APPLICATION_PROPERTIES + ": " + file.getAbsolutePath());
                    try (InputStream inputStream = new FileInputStream(file)) {
                        var reader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
                        return loadProperties(reader);
                    } catch (IOException e) {
                        logger.error("load properties error", e);
                    }
                } else {
                    // read via network
                    try {
                        if (!applicationUrl.equals(Constants.APPLICATION_PROPERTIES)) {
                            URL url1 = new URL(applicationUrl);
                            if (logger.isDebugEnabled())
                                logger.debug(Constants.APPLICATION_PROPERTIES + " url: " + url1);
                            try (InputStream inputStream = url1.openStream()) {
                                var reader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
                                return loadProperties(reader);
                            }
                        }
                    } catch (IOException e) {
                        logger.error("load properties error", e);
                    }
                }
            } else {
                if (logger.isDebugEnabled())
                    logger.debug(Constants.APPLICATION_PROPERTIES + " url: " + url);
                InputStream inputStream;
                try {
                    inputStream = url.openStream();
                    var reader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
                    return loadProperties(reader);
                } catch (IOException e) {
                    logger.error("load properties error", e);
                }
            }
        }
        return null;
    }

    /**
     * read from yaml file
     */
    private Properties readYamlFile() {
        var classLoader = ClassLoaderUtils.getClassLoader(BaseProperties.class);
        var applicationUrl = System.getProperty("debbie.application.yaml", Constants.APPLICATION_YAML);
        if (applicationUrl == null) {
            logger.warn(() -> "debbie.application.yaml value is null.");
        } else {
            logger.debug(() -> Constants.APPLICATION_YAML + ": " + applicationUrl);
            var url = classLoader.getResource(applicationUrl);
            if (url == null) {
                if (applicationUrl.equals(Constants.APPLICATION_YAML))
                    logger.warn(Constants.APPLICATION_YAML + " not found in classpath.");
                else {
                    logger.warn(applicationUrl + " not found in classpath.");
                }
                // read via file
                File file = new File(applicationUrl);
                if (file.exists()) {
                    logger.debug(() -> Constants.APPLICATION_YAML + ": " + file.getAbsolutePath());
                    try (InputStream inputStream = new FileInputStream(file)) {
                        return loadYaml(inputStream);
                    } catch (IOException e) {
                        logger.error("load yaml error", e);
                    }
                } else {
                    // read via network
                    try {
                        if (!applicationUrl.equals(Constants.APPLICATION_YAML)) {
                            url = new URL(applicationUrl);
                            if (logger.isDebugEnabled())
                                logger.debug(Constants.APPLICATION_YAML + " url: " + url);
                            try (InputStream inputStream = url.openStream()) {
                                return loadYaml(inputStream);
                            }
                        }
                    } catch (IOException e) {
                        logger.error("load yaml error", e);
                    }
                }
            } else {
                if (logger.isDebugEnabled())
                    logger.debug(Constants.APPLICATION_YAML + " url: " + url);
                InputStream inputStream;
                try {
                    inputStream = url.openStream();
                    return loadYaml(inputStream);
                } catch (IOException e) {
                    logger.error("load yaml error", e);
                }
            }
        }
        return null;
    }

    /**
     * read from yal file
     */
    private Properties readYmlFile() {
        var classLoader = ClassLoaderUtils.getClassLoader(BaseProperties.class);
        var applicationUrl = System.getProperty("debbie.application.yml", Constants.APPLICATION_YML);
        if (applicationUrl == null) {
            logger.warn(() -> "debbie.application.yml value is null.");
        } else {
            logger.debug(() -> Constants.APPLICATION_YML + ": " + applicationUrl);
            var url = classLoader.getResource(applicationUrl);
            if (url == null) {
                if (applicationUrl.equals(Constants.APPLICATION_YML))
                    logger.warn(Constants.APPLICATION_YML + " not found in classpath.");
                else {
                    logger.warn(applicationUrl + " not found in classpath.");
                }
                // read via file
                File file = new File(applicationUrl);
                if (file.exists()) {
                    logger.debug(() -> Constants.APPLICATION_YML + ": " + file.getAbsolutePath());
                    try (InputStream inputStream = new FileInputStream(file)) {
                        return loadYaml(inputStream);
                    } catch (IOException e) {
                        logger.error("load yml error", e);
                    }
                } else {
                    // read via network
                    try {
                        if (!applicationUrl.equals(Constants.APPLICATION_YML)) {
                            url = new URL(applicationUrl);
                            if (logger.isDebugEnabled())
                                logger.debug(Constants.APPLICATION_YML + " url: " + url);
                            try (InputStream inputStream = url.openStream()) {
                                return loadYaml(inputStream);
                            }
                        }
                    } catch (IOException e) {
                        logger.error("load yml error", e);
                    }
                }
            } else {
                if (logger.isDebugEnabled())
                    logger.debug(Constants.APPLICATION_YML + " url: " + url);
                InputStream inputStream;
                try {
                    inputStream = url.openStream();
                    return loadYaml(inputStream);
                } catch (IOException e) {
                    logger.error("load yml error", e);
                }
            }
        }
        return null;
    }

    public void addProperty(String name, String value) {
        PROPERTIES.put(name, value);
    }

    public static boolean isPropertiesEmpty() {
        return PROPERTIES.isEmpty();
    }

    public Properties getProperties() {
        if (PROPERTIES.isEmpty()) {
            Properties result = new Properties();
            // OS environment variable
            var env = System.getenv();
            result.putAll(env);
            // project properties
            Properties properties = readPropertiesFile();
            if (properties == null) {
                properties = readYamlFile();
            }
            if (properties == null) {
                properties = readYmlFile();
            }
            if (properties != null) {
                // custom properties will cover system properties
                result.putAll(properties);
            }
            // jvm properties
            var systemProperties = System.getProperties();
            result.putAll(systemProperties);
            PROPERTIES.putAll(result);
        }
        return PROPERTIES;
    }

    private Properties loadProperties(InputStreamReader reader) {
        Properties result = new Properties();
        // OS environment variable
        var env = System.getenv();
        result.putAll(env);
        // project properties
        Properties properties = new Properties();
        try {
            properties.load(reader);
        } catch (IOException e) {
            logger.error("load properties error", e);
        }
        // custom properties will cover system properties
        result.putAll(properties);
        // jvm properties
        var systemProperties = System.getProperties();
        result.putAll(systemProperties);
        return result;
    }

    private Properties loadYaml(InputStream inputStream) {
        Properties result = new Properties();
        // OS environment variable
        var env = System.getenv();
        result.putAll(env);
        // project properties
        Properties properties = new Properties();
        Map map = JacksonYamlUtils.yml2Properties(inputStream);
        properties.putAll(map);
        // custom properties will cover system properties
        result.putAll(properties);
        // jvm properties
        var systemProperties = System.getProperties();
        result.putAll(systemProperties);
        return result;
    }

    public Map<String, String> getMatchedKey(String keyPrefix) {
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

    public String getValue(String key) {
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

    public String getStringValue(String key, String defaultValue) {
        var value = getValue(key);
        if (value == null) {
            value = defaultValue;
        }
        return value;
    }

    public char getCharacterValue(String key, char defaultValue) {
        var value = getValue(key);
        char result;
        if (value == null) {
            result = defaultValue;
        } else {
            try {
                result = value.charAt(0);
            } catch (Exception e) {
                logger.error(value + " to char error", e);
                result = defaultValue;
            }
        }
        return result;
    }

    public boolean getBooleanValue(String key, boolean defaultValue) {
        var value = getValue(key);
        boolean result;
        if (value == null) {
            result = defaultValue;
        } else {
            try {
                result = Boolean.parseBoolean(value);
            } catch (Exception e) {
                logger.error(value + " to bool error", e);
                result = defaultValue;
            }
        }
        return result;
    }

    public int getIntegerValue(String key, int defaultValue) {
        var value = getValue(key);
        int result;
        if (value == null) {
            result = defaultValue;
        } else {
            try {
                result = Integer.parseInt(value);
            } catch (Exception e) {
                logger.error(value + " to int error", e);
                result = defaultValue;
            }
        }
        return result;
    }

    public double getDoubleValue(String key, double defaultValue) {
        var value = getValue(key);
        double result;
        if (value == null) {
            result = defaultValue;
        } else {
            try {
                result = Double.parseDouble(value);
            } catch (Exception e) {
                logger.error(value + " to double error", e);
                result = defaultValue;
            }
        }
        return result;
    }

    public long getLongValue(String key, long defaultValue) {
        var value = getValue(key);
        long result;
        if (value == null) {
            result = defaultValue;
        } else {
            try {
                result = Long.parseLong(value);
            } catch (Exception e) {
                logger.error(value + " to long error", e);
                result = defaultValue;
            }
        }
        return result;
    }

    public String[] getStringArrayValue(String key, String split) {
        var value = getValue(key);
        if (split == null || split.isBlank()) {
            throw new PropertiesException("illegal split");
        }
        if (value != null) {
            return value.split(split);
        }
        return null;
    }

    public Charset getCharsetValue(String key, Charset defaultCharset) {
        var value = getValue(key);
        Charset result;
        if (value == null) {
            result = defaultCharset;
        } else {
            try {
                result = Charset.forName(value);
            } catch (Exception e) {
                logger.error(value + " to Charset error", e);
                result = defaultCharset;
            }
        }
        return result;
    }

    public Map<String, String> getMapValue(String key, String keyValueSplit, String split) {
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

    public List<String> getStringListValue(String key, String split) {
        var value = getStringArrayValue(key, split);
        List<String> result = null;
        if (value != null) {
            result = Arrays.asList(value);
        }
        return result;
    }

    @SuppressWarnings("rawtypes")
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
                        logger.error("class (" + className + ") not found", e);
                    }
                }
            }
        }
        return result;
    }

    public Class<?> getClassValue(String key, String defaultClass) {
        var className = getStringValue(key, defaultClass);
        var classLoader = ClassLoaderUtils.getDefaultClassLoader();
        Class<?> result = null;
        if (className != null) {
            try {
                result = classLoader.loadClass(className);
            } catch (ClassNotFoundException e) {
                logger.error("class (" + className + ") not found", e);
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
                        logger.error("class (" + className + ") not found", e);
                    }
                }
            }
        }
        return result;
    }

    public static void reset() {
        BaseProperties.PROPERTIES.clear();
    }

}