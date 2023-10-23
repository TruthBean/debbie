package com.truthbean.debbie.environment;

import com.truthbean.Logger;

import java.util.Objects;
import java.util.Properties;

/**
 * @author TruthBean
 * @since 0.5.5
 * Created on 2022/04/08 14:32.
 */
public final class EnvironmentWrapper implements ProfiledEnvironment {

    private final Logger logger;
    private final Properties properties;
    private final String profile;
    private final int priority;

    /**
     *
     */
    public EnvironmentWrapper(Properties properties, String profile, int priority, Logger logger) {
        this.properties = properties;
        this.profile = profile;
        this.priority = priority;
        this.logger = logger;
    }

    public Environment getEnvironment() {
        return this;
    }

    @Override
    public Logger getLogger() {
        return logger;
    }

    @Override
    public void clear() {
        properties.clear();
    }

    @Override
    public Properties properties() {
        return properties;
    }

    @Override
    public String profile() {
        return profile;
    }

    @Override
    public int priority() {
        return priority;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (EnvironmentWrapper) obj;
        return Objects.equals(this.properties, that.properties) &&
                Objects.equals(this.profile, that.profile) &&
                this.priority == that.priority;
    }

    @Override
    public int hashCode() {
        return Objects.hash(properties, profile, priority);
    }

    @Override
    public String toString() {
        return "EnvironmentWrapper[" +
                "properties=" + properties + ", " +
                "profile=" + profile + ", " +
                "priority=" + priority + ']';
    }
}
