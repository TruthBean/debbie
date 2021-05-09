/**
 * Copyright (c) 2021 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 * http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.properties;

import com.truthbean.Logger;
import com.truthbean.LoggerFactory;
import com.truthbean.debbie.data.serialize.JacksonYamlUtils;
import com.truthbean.debbie.env.EnvironmentContent;
import com.truthbean.debbie.reflection.ClassLoaderUtils;
import com.truthbean.debbie.util.Constants;

import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * @author TruthBean
 * @since 0.0.1
 * Created on 2018-03-19 12:27.
 */
public class BaseProperties implements EnvironmentContent {

    /**
     * logger
     */
    private volatile Logger logger;

    public BaseProperties() {
    }

    @Override
    public BaseProperties setLogger(Logger logger) {
        this.logger = logger;
        return this;
    }

    @Override
    public Logger getLogger() {
        if (this.logger == null) {
            this.logger = LoggerFactory.getLogger(BaseProperties.class);
        }
        return logger;
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
            getLogger().warn(() -> "debbie.application.properties value is null.");
        } else {
            getLogger().debug(() -> Constants.APPLICATION_PROPERTIES + ": " + applicationUrl);
            var url = classLoader.getResource(applicationUrl);
            if (url == null) {
                if (applicationUrl.equals(Constants.APPLICATION_PROPERTIES))
                    getLogger().warn(Constants.APPLICATION_PROPERTIES + " not found in classpath.");
                else {
                    getLogger().warn(applicationUrl + " not found in classpath.");
                }
                // read via file
                File file = new File(applicationUrl);
                if (file.exists()) {
                    getLogger().info(() -> Constants.APPLICATION_PROPERTIES + ": " + file.getAbsolutePath());
                    try (InputStream inputStream = new FileInputStream(file)) {
                        var reader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
                        return loadProperties(reader);
                    } catch (IOException e) {
                        getLogger().error("load properties error", e);
                    }
                } else {
                    // read via network
                    try {
                        if (!applicationUrl.equals(Constants.APPLICATION_PROPERTIES)) {
                            URL url1 = new URL(applicationUrl);
                            getLogger().info(Constants.APPLICATION_PROPERTIES + " url: " + url1);
                            try (InputStream inputStream = url1.openStream()) {
                                var reader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
                                return loadProperties(reader);
                            }
                        }
                    } catch (IOException e) {
                        getLogger().error("load properties error", e);
                    }
                }
            } else {
                getLogger().info(Constants.APPLICATION_PROPERTIES + " url: " + url);
                InputStream inputStream;
                try {
                    inputStream = url.openStream();
                    var reader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
                    return loadProperties(reader);
                } catch (IOException e) {
                    getLogger().error("load properties error", e);
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
            getLogger().warn(() -> "debbie.application.yaml value is null.");
        } else {
            getLogger().debug(() -> Constants.APPLICATION_YAML + ": " + applicationUrl);
            var url = classLoader.getResource(applicationUrl);
            if (url == null) {
                if (applicationUrl.equals(Constants.APPLICATION_YAML))
                    getLogger().warn(Constants.APPLICATION_YAML + " not found in classpath.");
                else {
                    getLogger().warn(applicationUrl + " not found in classpath.");
                }
                // read via file
                File file = new File(applicationUrl);
                if (file.exists()) {
                    getLogger().debug(() -> Constants.APPLICATION_YAML + ": " + file.getAbsolutePath());
                    try (InputStream inputStream = new FileInputStream(file)) {
                        return loadYaml(inputStream);
                    } catch (IOException e) {
                        getLogger().error("load yaml error", e);
                    }
                } else {
                    // read via network
                    try {
                        if (!applicationUrl.equals(Constants.APPLICATION_YAML)) {
                            url = new URL(applicationUrl);
                            if (getLogger().isDebugEnabled())
                                getLogger().debug(Constants.APPLICATION_YAML + " url: " + url);
                            try (InputStream inputStream = url.openStream()) {
                                return loadYaml(inputStream);
                            }
                        }
                    } catch (IOException e) {
                        getLogger().error("load yaml error", e);
                    }
                }
            } else {
                if (getLogger().isDebugEnabled())
                    getLogger().debug(Constants.APPLICATION_YAML + " url: " + url);
                InputStream inputStream;
                try {
                    inputStream = url.openStream();
                    return loadYaml(inputStream);
                } catch (IOException e) {
                    getLogger().error("load yaml error", e);
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
            getLogger().warn(() -> "debbie.application.yml value is null.");
        } else {
            getLogger().debug(() -> Constants.APPLICATION_YML + ": " + applicationUrl);
            var url = classLoader.getResource(applicationUrl);
            if (url == null) {
                if (applicationUrl.equals(Constants.APPLICATION_YML))
                    getLogger().warn(Constants.APPLICATION_YML + " not found in classpath.");
                else {
                    getLogger().warn(applicationUrl + " not found in classpath.");
                }
                // read via file
                File file = new File(applicationUrl);
                if (file.exists()) {
                    getLogger().debug(() -> Constants.APPLICATION_YML + ": " + file.getAbsolutePath());
                    try (InputStream inputStream = new FileInputStream(file)) {
                        return loadYaml(inputStream);
                    } catch (IOException e) {
                        getLogger().error("load yml error", e);
                    }
                } else {
                    // read via network
                    try {
                        if (!applicationUrl.equals(Constants.APPLICATION_YML)) {
                            url = new URL(applicationUrl);
                            if (getLogger().isDebugEnabled())
                                getLogger().debug(Constants.APPLICATION_YML + " url: " + url);
                            try (InputStream inputStream = url.openStream()) {
                                return loadYaml(inputStream);
                            }
                        }
                    } catch (IOException e) {
                        getLogger().error("load yml error", e);
                    }
                }
            } else {
                if (getLogger().isDebugEnabled())
                    getLogger().debug(Constants.APPLICATION_YML + " url: " + url);
                InputStream inputStream;
                try {
                    inputStream = url.openStream();
                    return loadYaml(inputStream);
                } catch (IOException e) {
                    getLogger().error("load yml error", e);
                }
            }
        }
        return null;
    }

    @Override
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
            getLogger().error("load properties error", e);
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

}