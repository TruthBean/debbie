/**
 * Copyright (c) 2023 TruthBean(Rogar·Q)
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
import com.truthbean.debbie.environment.*;
import com.truthbean.debbie.reflection.ClassLoaderUtils;
import com.truthbean.debbie.util.Constants;
import com.truthbean.logger.SystemOutLogger;

import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * @author TruthBean
 * @since 0.0.1
 * Created on 2018-03-19 12:27.
 */
public class PropertiesResourceEnvironment implements ResourceEnvironment {

    /**
     * logger
     */
    private volatile Logger logger;

    /**
     * properties
     */
    private final Properties allProperties = new Properties();

    private final Map<String, ProfiledEnvironment> environmentMap = new HashMap<>();

    private final String profileKey = "debbie.profile.name";

    public PropertiesResourceEnvironment() {
    }

    @Override
    public int priority() {
        return 1999;
    }

    @Override
    public String profile() {
        return EnvironmentDepository.PROPERTIES;
    }

    public Environment getEnvironment(String profile) {
        if (this.profile().equals(profile)) {
            return this;
        }
        return environmentMap.get(profile);
    }

    public boolean hashProfile(String profile) {
        if (this.profile().equals(profile)) {
            return true;
        }
        return environmentMap.containsKey(profile);
    }

    public List<Environment> getEnvironments() {
        return new ArrayList<>(environmentMap.values());
    }

    public Set<String> getProfiles() {
        return new HashSet<>(environmentMap.keySet());
    }

    public Map<String, Environment> getProfileEnvironments() {
        return new HashMap<>(environmentMap);
    }

    @Override
    public PropertiesResourceEnvironment setLogger(Logger logger) {
        this.logger = logger;
        return this;
    }

    @Override
    public Logger getLogger() {
        if (this.logger == null) {
            this.logger = SystemOutLogger.getLogger(PropertiesResourceEnvironment.class);
        }
        return logger;
    }

    /**
     * read from properties file
     */
    @Override
    public Properties load(String resourceUri) {
        if (resourceUri == null) {
            getLogger().warn(() -> "debbie.application.properties VALUE is null.");
        } else {
            getLogger().debug(() -> Constants.APPLICATION_PROPERTIES + ": " + resourceUri);
            var classLoader = ClassLoaderUtils.getClassLoader(PropertiesResourceEnvironment.class);
            var url = classLoader.getResource(resourceUri);
            if (url == null) {
                if (resourceUri.equals(Constants.APPLICATION_PROPERTIES)) {
                    getLogger().warn(Constants.APPLICATION_PROPERTIES + " not found in classpath.");
                } else {
                    getLogger().warn(resourceUri + " not found in classpath.");
                }
                // read via file
                File file = new File(resourceUri);
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
                        if (!resourceUri.equals(Constants.APPLICATION_PROPERTIES)) {
                            URL url1 = new URL(resourceUri);
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

    @Override
    public Map<String, ProfiledEnvironment> loadResources() {
        Properties result = new Properties();
        var applicationUrl = System.getProperty(Constants.DEBBIE_APPLICATION_PROPERTIES, Constants.APPLICATION_PROPERTIES);
        if (applicationUrl.contains(";")) {
            String[] applicationUrls = applicationUrl.split(";");
            for (int i = 0; i < applicationUrls.length; i++) {
                String url = applicationUrls[i];
                Properties properties = load(url);
                if (properties != null) {
                    result.putAll(properties);
                    String profile = EnvironmentDepository.PROPERTIES + ":" + url;
                    // 优先使用profileKey自定义的名称，自定义的名称不能为系统已经存在的名称
                    profile = properties.getProperty(profileKey, profile);
                    environmentMap.put(profile, new EnvironmentWrapper(properties, profile, priority() - 1 - i, logger));
                }
            }
        } else {
            Properties properties = load(applicationUrl);
            if (properties != null) {
                result.putAll(properties);
                String profile = EnvironmentDepositoryHolder.ORIGIN_PROFILE;
                // 优先使用profileKey自定义的名称，自定义的名称不能为系统已经存在的名称
                profile = properties.getProperty(profileKey, profile);
                environmentMap.put(profile, new EnvironmentWrapper(properties, profile, priority() - 1, logger));
            }
        }
        /*String profilesStr = System.getProperty(Constants.PROFILES);
        if (StringUtils.hasText(profilesStr)) {
            String[] profiles = profilesStr.trim().split(",");
            for (int i = 0; i < profiles.length; i++) {
                String profile = profiles[i];
                String name = Constants.APPLICATION + "-" + profile + Constants.PROPERTIES;
                applicationUrl = applicationUrl.replace(Constants.APPLICATION_PROPERTIES, name);
                Properties profileProperties = load(applicationUrl);
                if (profileProperties != null) {
                    result.putAll(profileProperties);
                    environmentMap.put(profile, new EnvironmentWrapper(profileProperties, profile, getPriority() - i - 2));
                }
            }
        }*/
        allProperties.putAll(result);
        return environmentMap;
    }

    @Override
    public Properties properties() {
        if (allProperties.isEmpty()) {
            loadResources();
        }
        return allProperties;
    }

    private Properties loadProperties(InputStreamReader reader) {
        Properties result = new Properties();
        // OS environment variable
        // var env = System.getenv();
        // result.putAll(env);
        // project properties
        Properties properties = new Properties();
        try {
            properties.load(reader);
        } catch (IOException e) {
            getLogger().error("load properties error", e);
        }
        // customize properties will cover system properties
        result.putAll(properties);
        // jvm properties
        // var systemProperties = System.getProperties();
        // result.putAll(systemProperties);
        return result;
    }

    @Override
    public void clear() {
        allProperties.clear();
        environmentMap.forEach((s, properties) -> properties.clear());
        environmentMap.clear();
    }
}