package com.truthbean.debbie.environment;

import com.truthbean.Logger;

import java.util.Optional;
import java.util.Properties;
import java.util.Set;

/**
 * @author TruthBean
 * @since 0.5.5
 * Created on 2022/04/02 11:25.
 */
public class DebbieEnvironmentDepositoryHolder implements EnvironmentDepositoryHolder, ProfiledEnvironment {
    static {
        EnvironmentDepository.init();
    }

    private static final Properties CACHE = new Properties();
    private static final EnvironmentDepository PROFILE = new EnvironmentDepository();

    private String defaultProfile;

    private Logger logger;

    public DebbieEnvironmentDepositoryHolder() {
        if (!PROFILE.hasProfile(this.profile())) {
            PROFILE.addEnvironment(this.profile(), this);
        }
    }

    @Override
    public int priority() {
        return -1;
    }

    @Override
    public String profile() {
        return "all";
    }

    @Override
    public Set<String> getProfiles() {
        return PROFILE.getProfiles();
    }

    @Override
    public void setDefaultProfile(String profile) {
        this.defaultProfile = profile;
    }

    @Override
    public String getDefaultProfile() {
        return defaultProfile;
    }

    @Override
    public EnvironmentDepositoryHolder setLogger(Logger logger) {
        this.logger = logger;
        PROFILE.setAllEnvLogger(logger);
        return this;
    }

    @Override
    public Logger getLogger() {
        return logger;
    }

    @Override
    public void addProperty(String name, String value) {
        CACHE.put(name, value);
    }

    @Override
    public boolean containKey(String key) {
        return CACHE.containsKey(key);
    }

    @Override
    public boolean containKey(String profile, String key) {
        return CACHE.containsKey(profile + ":" + key);
    }

    @Override
    public boolean isPropertiesEmpty() {
        return CACHE.isEmpty();
    }

    @Override
    public Properties getAllProperties() {
        var properties = PROFILE.getAllProperties();
        CACHE.putAll(properties);
        return CACHE;
    }

    @Override
    public Properties properties() {
        var properties = PROFILE.getAllProperties();
        CACHE.putAll(properties);
        return CACHE;
    }

    @Override
    public Optional<Environment> getEnvironment(String profile) {
        if (profile == null || profile().equals(profile)) {
            return Optional.of(this);
        }
        return Optional.ofNullable(PROFILE.getEnvironment(profile));
    }

    @Override
    public Properties getAllProperties(String profile) {
        if (profile == null || profile().equals(profile)) {
            return this.getAllProperties();
        }
        Environment environment = PROFILE.getEnvironment(profile);
        if (environment == null) {
            return new Properties();
        } else {
            var properties = environment.properties();
            CACHE.putAll(properties);
            return properties;
        }
    }

    @Override
    public void clear() {
        CACHE.clear();
        PROFILE.clear();
    }
}
