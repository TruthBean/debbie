package com.truthbean.debbie.properties;

import com.truthbean.debbie.reflection.ClassLoaderUtils;
import com.truthbean.debbie.util.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author TruthBean
 * @since 0.0.1
 * Created on 2018-03-19 12:27.
 */
public class BaseProperties {

    /**
     * slf4j logger
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(BaseProperties.class);

    /**
     * properties变量名称正则
     */
    private static Pattern VARIABLE_PATTERN = Pattern.compile("\\$\\s*\\{?\\s*([._0-9a-zA-Z]+)\\s*}?");

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
        var classLoader = ClassLoaderUtils.getClassLoader(BaseProperties.class);
        var applicationUrl = System.getProperty("debbie.application.properties", Constants.APPLICATION_PROPERTIES);
        if (applicationUrl == null) {
            LOGGER.error("debbie.application.properties value cannot be null.");
        } else {
            LOGGER.debug("application.properties: " + applicationUrl);
            var url = classLoader.getResource(applicationUrl);
            if (url == null) {
                if (applicationUrl.equals(Constants.APPLICATION_PROPERTIES))
                    LOGGER.warn(Constants.APPLICATION_PROPERTIES + " not found in classpath.");
                else {
                    LOGGER.warn(applicationUrl + " not found in classpath.");
                }
                // read via file
                File file = new File(applicationUrl);
                if (file.exists()) {
                    LOGGER.debug("application.properties url: " + file.getAbsolutePath());
                    InputStream inputStream;
                    try {
                        inputStream = new FileInputStream(file);
                        var reader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
                        PROPERTIES.load(reader);
                    } catch (IOException e) {
                        LOGGER.error("load properties error", e);
                    }
                } else {
                    // read via network
                    InputStream inputStream;
                    try {
                        if (!applicationUrl.equals(Constants.APPLICATION_PROPERTIES)) {
                            url = new URL(applicationUrl);
                            LOGGER.debug("application.properties url: " + url);
                            inputStream = url.openStream();
                            var reader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
                            PROPERTIES.load(reader);
                        }
                    } catch (IOException e) {
                        LOGGER.error("load properties error", e);
                    }
                }
            } else {
                LOGGER.debug("application.properties url: " + url);
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
    }

    public static boolean isPropertiesEmpty() {
        return PROPERTIES.isEmpty();
    }

    public Properties getProperties() {
        Properties result = new Properties();
        // OS environment variable
        var env = System.getenv();
        result.putAll(env);
        // jvm properties
        var systemProperties = System.getProperties();
        result.putAll(systemProperties);
        // project properties
        var properties = BaseProperties.PROPERTIES;
        if (properties.isEmpty()) {
            BaseProperties.readPropertiesFile();
            properties = BaseProperties.PROPERTIES;
        }
        // custom properties will cover system properties
        result.putAll(properties);

        return result;
    }

    /**
     * replace property
     *
     * @param expression
     * @param params
     * @return
     */
    private static String replaceProperty(String expression, Properties params) {
        if (expression == null || expression.length() == 0 || !expression.contains("$")) {
            return expression;
        }
        Matcher matcher = VARIABLE_PATTERN.matcher(expression);
        StringBuffer sb = new StringBuffer();
        // 逐个匹配
        while (matcher.find()) {
            String key = matcher.group(1);
            LOGGER.debug("env key:" + key);
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
            LOGGER.debug("env value:" + value);
            matcher.appendReplacement(sb, Matcher.quoteReplacement(value));
        }
        matcher.appendTail(sb);
        return sb.toString();
    }

    public Map<String, String> getMatchedKey(String keyPrefix) {
        if (keyPrefix == null || keyPrefix.isBlank()) {
            throw new RuntimeException("illegal keyPrefix");
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
            throw new RuntimeException("illegal key");
        }
        var properties = getProperties();
        String value = null;
        if (properties.containsKey(key)) {
            value = replaceProperty(properties.getProperty(key, null), properties);
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
                LOGGER.error(value + " to char error", e);
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
                LOGGER.error(value + " to bool error", e);
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
                LOGGER.error(value + " to int error", e);
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
                LOGGER.error(value + " to int error", e);
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
                LOGGER.error(value + " to long error", e);
                result = defaultValue;
            }
        }
        return result;
    }

    public String[] getStringArrayValue(String key, String split) {
        var value = getValue(key);
        if (split == null || split.isBlank()) {
            throw new RuntimeException("illegal split");
        }
        if (value != null) {
            return value.split(split);
        }
        return null;
    }

    public Map<String, String> getMapValue(String key, String keyValueSplit, String split) {
        var value = getValue(key);
        if (split == null || split.isBlank()) {
            throw new RuntimeException("illegal split");
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
                        LOGGER.error("class (" + className + ") not found", e);
                    }
                }
            }
        }
        return result;
    }

    @SuppressWarnings("rawtypes")
    public Class<?> getClassValue(String key, String defaultClass) {
        var className = getStringValue(key, defaultClass);
        var classLoader = ClassLoaderUtils.getDefaultClassLoader();
        Class<?> result = null;
        if (className != null) {
            try {
                result = classLoader.loadClass(className);
            } catch (ClassNotFoundException e) {
                LOGGER.error("class (" + className + ") not found", e);
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

    public void reset() {
        BaseProperties.PROPERTIES.clear();
    }

}