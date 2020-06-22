/**
 * Copyright (c) 2020 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *         http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.properties;

import com.truthbean.debbie.reflection.ClassLoaderUtils;
import com.truthbean.debbie.util.OsUtils;
import com.truthbean.Logger;
import com.truthbean.logger.LoggerFactory;

import java.io.*;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author TruthBean
 * @since 2018-01-05 14:21
 */
public final class PropertiesHelper {
    private PropertiesHelper() {
    }

    /**
     * logger
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(PropertiesHelper.class);

    /**
     * raw properties cache
     */
    private static final Map<String, Properties> RAW_PROPERTIES_CACHE = new ConcurrentHashMap<>();

    /**
     * properties with value replaced by real value
     */
    private static final Map<String, Properties> PROPERTIES_CACHE = new ConcurrentHashMap<>();

    public static Properties getPropertiesWithRealValue(String filename) {
        Properties properties = loadProperties(filename, false, false, StandardCharsets.UTF_8);
        for (Object key : properties.keySet()) {
            String realValue = replaceProperty(properties.getProperty((String) key), properties);
            System.setProperty((String) key, realValue);
            properties.put(key, realValue);
        }
        PROPERTIES_CACHE.put(filename, properties);
        return properties;
    }

    /**
     * get property value
     *
     * @param filename properties file name
     * @param key key
     * @return string
     */
    public static String getProperty(String filename, String key) {
        return getProperty(filename, key, null);
    }

    /**
     * get raw property value
     *
     * @param filename properties file name
     * @param key key
     * @return string
     */
    public static String getRawProperty(String filename, String key) {
        return getRawProperty(filename, key, null);
    }

    /**
     * get property int value
     *
     * @param key key
     * @param filename filename
     * @return int
     */
    public static int getIntProperty(String filename, String key) throws NumberFormatException {
        String valueStr = getProperty(filename, key, null);
        if (valueStr != null) {
            return Integer.parseInt(valueStr);
        }
        return 0;
    }

    public static long getLongProperty(String filename, String key) throws NumberFormatException {
        String valueStr = getProperty(filename, key, null);
        if (valueStr != null) {
            return Long.parseLong(valueStr);
        }
        return 0L;
    }

    /**
     * get property bool value
     *
     * @param filename properties file name
     * @param key property key
     * @return property bool value
     */
    public static boolean getBoolProperty(String filename, String key) {
        String valueStr = getProperty(filename, key, null);
        return Boolean.parseBoolean(valueStr);
    }

    public static String getProperty(String filename, String key, String defaultValue) {
        if (PROPERTIES_CACHE.containsKey(filename)) {
            Properties properties = PROPERTIES_CACHE.get(filename);
            if (properties.containsKey(key)) {
                return properties.getProperty(key);
            } else {
                return defaultValue;
            }
        } else {
            Properties properties = getPropertiesWithRealValue(filename);
            return properties.getProperty(key);
        }
    }

    public static String getRawProperty(String filename, String key, String defaultValue) {
        String value = System.getProperty(key);
        if (value != null && value.length() > 0) {
            return value;
        }
        Properties properties = loadProperties(filename, false, false, StandardCharsets.UTF_8);
        return replaceProperty(properties.getProperty(key, defaultValue), properties);
    }

    /**
     * properties变量名称正则
     */
    private static Pattern VARIABLE_PATTERN = Pattern.compile("\\$\\s*\\{?\\s*([._0-9a-zA-Z]+)\\s*}?");

    /**
     * replace property
     *
     * @param expression properties value contains, like a.b.c=a-${user.dir}
     * @param params the properties
     * @return the real property value
     */
    public static String replaceProperty(String expression, Properties params) {
        if (expression == null || expression.length() == 0 || !expression.contains("$")) {
            return expression;
        }
        Matcher matcher = VARIABLE_PATTERN.matcher(expression);
        StringBuffer sb = new StringBuffer();
        // 逐个匹配
        while (matcher.find()) {
            String key = matcher.group(1);
            LOGGER.debug(() -> "env key:" + key);
            String value = System.getProperty(key);
            if (value == null && params != null) {
                value = params.getProperty(key);
            }
            if (value == null) {
                value = System.getenv(key);
                if (value == null) {
                    value = "";
                } else {
                    System.setProperty(key, value);
                }
            }
            if (LOGGER.isDebugEnabled())
                LOGGER.debug("env value:" + value);
            matcher.appendReplacement(sb, Matcher.quoteReplacement(value));
        }
        matcher.appendTail(sb);
        return sb.toString();
    }

    /**
     * Load properties file to {@link Properties} from class path.
     *
     * @param fileName       properties file name.
     * @param allowMultiFile if <code>false</code>, throw {@link IllegalStateException} when found multi file on the class path.
     * @param optional       is optional. if <code>false</code>, log warn when properties config file not found!s
     * @return loaded {@link Properties} content. <ul>
     * <li>return empty Properties if no file found.
     * <li>merge multi properties file if found multi file
     * </ul>
     * @throws IllegalStateException not allow multi-file, but multi-file exsit on class path.
     */
    private static Properties loadProperties(String fileName, boolean allowMultiFile, boolean optional, Charset charset) {

        if (RAW_PROPERTIES_CACHE.containsKey(fileName)) {
            return RAW_PROPERTIES_CACHE.get(fileName);
        }

        // read from file
        Properties properties = new Properties();
        if (fileName.startsWith("/")) {
            try {
                try (FileInputStream input = new FileInputStream(fileName)) {
                    var reader = new InputStreamReader(input, charset);
                    properties.load(reader);
                }
            } catch (Throwable e) {
                LOGGER.warn(() -> "Failed to load " + fileName + " file from " + fileName + "(ingore this file): " + e.getMessage(), e);
            }
            RAW_PROPERTIES_CACHE.put(fileName, properties);
            return properties;
        } else {
            final String path = getResourcePath(fileName);
            LOGGER.debug(() -> "properties file path: " + path);
            if (path != null) {
                File file = new File(path);
                if (file.exists()) {
                    try {
                        try (FileInputStream input = new FileInputStream(file)) {
                            var reader = new InputStreamReader(input, charset);
                            properties.load(reader);
                        }
                    } catch (IOException e) {
                        LOGGER.warn("Failed to load " + fileName + " file from " + fileName + "(ingore this file): " + e.getMessage(), e);
                    }
                    RAW_PROPERTIES_CACHE.put(fileName, properties);
                    return properties;
                }
            }

        }

        List<URL> list = new ArrayList<>();
        try {
            Enumeration<URL> urls = ClassLoaderUtils.getDefaultClassLoader().getResources(fileName);
            list = new ArrayList<>();
            while (urls.hasMoreElements()) {
                list.add(urls.nextElement());
            }
        } catch (Throwable t) {
            LOGGER.warn("Fail to load " + fileName + " file: " + t.getMessage(), t);
        }

        if (list.size() == 0) {
            if (!optional) {
                LOGGER.warn("No " + fileName + " found on the class path.");
            }
            RAW_PROPERTIES_CACHE.put(fileName, properties);
            return properties;
        }

        if (!allowMultiFile) {
            // fall back to use method getResourceAsStream
            try {
                ClassLoader classLoader = ClassLoaderUtils.getClassLoader(PropertiesHelper.class);
                URL url = new URL(fileName);
                InputStream inputStream = url.openStream();
                var reader = new InputStreamReader(inputStream, charset);
                properties.load(reader);
            } catch (Throwable e) {
                LOGGER.warn("Failed to load " + fileName + " file from " + fileName + "(ingore this file): " + e.getMessage(), e);
            }
            RAW_PROPERTIES_CACHE.put(fileName, properties);
            return properties;
        }

        LOGGER.info("load " + fileName + " properties file from " + list);

        for (java.net.URL url : list) {
            try {
                Properties p = new Properties();
                InputStream input = url.openStream();
                if (input != null) {
                    try (input) {
                        p.load(input);
                        properties.putAll(p);
                    }
                }
            } catch (Throwable e) {
                LOGGER.warn("Fail to load " + fileName + " file from " + url + "(ingore this file): " + e.getMessage(), e);
            }
        }

        RAW_PROPERTIES_CACHE.put(fileName, properties);
        return properties;
    }

    public static String getResourcePath(String resourceName) {
        URL classPath = ClassLoaderUtils.getClassLoader(PropertiesHelper.class).getResource(resourceName);
        if (classPath != null) {
            String path = classPath.getPath();
            if (OsUtils.isWinOs()) {
                return path.replaceFirst("/", "");
            }
            return path;
        }
        return null;
    }

}
