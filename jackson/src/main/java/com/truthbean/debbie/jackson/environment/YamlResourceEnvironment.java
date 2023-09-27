package com.truthbean.debbie.jackson.environment;

import com.truthbean.Logger;
import com.truthbean.debbie.environment.Environment;
import com.truthbean.debbie.environment.ProfiledEnvironment;
import com.truthbean.debbie.environment.ResourceEnvironment;
import com.truthbean.debbie.properties.PropertiesResourceEnvironment;
import com.truthbean.debbie.reflection.ClassLoaderUtils;
import com.truthbean.debbie.util.Constants;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.*;

/**
 * @author TruthBean
 * @since 0.5.5
 * Created on 2022/04/08 11:33.
 */
public class YamlResourceEnvironment implements ResourceEnvironment {
    @Override
    public int priority() {
        return 2999;
    }

    @Override
    public String profile() {
        return null;
    }

    @Override
    public Properties properties() {
        return null;
    }

    @Override
    public Logger getLogger() {
        return null;
    }

    @Override
    public Environment setLogger(Logger logger) {
        return null;
    }

    @Override
    public Properties load(String resourceUri) {
        return null;
    }

    @Override
    public Map<String, ProfiledEnvironment> loadResources() {
        return new HashMap<>();
    }

    @Override
    public void clear() {

    }


    /**
     * read from yaml file
     */
    private Properties readYamlFile() {
        var classLoader = ClassLoaderUtils.getClassLoader(PropertiesResourceEnvironment.class);
        var applicationUrl = System.getProperty("debbie.application.yaml", Constants.APPLICATION_YAML);
        if (applicationUrl == null) {
            getLogger().warn(() -> "debbie.application.yaml VALUE is null.");
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
        var classLoader = ClassLoaderUtils.getClassLoader(PropertiesResourceEnvironment.class);
        var applicationUrl = System.getProperty("debbie.application.yml", Constants.APPLICATION_YML);
        if (applicationUrl == null) {
            getLogger().warn(() -> "debbie.application.yml VALUE is null.");
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

    private Properties loadYaml(InputStream inputStream) {
        Properties result = new Properties();
        // OS environment variable
        var env = System.getenv();
        result.putAll(env);
        // project properties
        Properties properties = new Properties();
        // Map map = JacksonYamlUtils.yml2Properties(inputStream);
        // properties.putAll(map);
        // customize properties will cover system properties
        result.putAll(properties);
        // jvm properties
        var systemProperties = System.getProperties();
        result.putAll(systemProperties);
        return result;
    }


    public Environment getEnvironment(String profile) {
        return null;
    }

    public boolean hashProfile(String profile) {
        return false;
    }

    public List<Environment> getEnvironments() {
        return null;
    }

    public Set<String> getProfiles() {
        return null;
    }

    public Map<String, Environment> getProfileEnvironments() {
        return null;
    }
}
