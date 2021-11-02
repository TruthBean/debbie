package com.truthbean.debbie.env;

import com.truthbean.Logger;
import com.truthbean.debbie.boot.DebbieApplication;
import com.truthbean.debbie.spi.SpiLoader;
import com.truthbean.logger.LoggerConfig;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author TruthBean
 * @since 0.5.3
 */
public class SimpleEnvironmentContentProfile implements EnvironmentContentProfile {

    private final Properties allProperties = new Properties();

    private static final Map<String, EnvironmentContent> environmentContents = new HashMap<>();

    private static volatile String highestPriority;

    public SimpleEnvironmentContentProfile() {
    }

    public static void reload() {
        environmentContents.put(SYSTEM, new SystemEnvironmentContent());
        environmentContents.put(JVM, new JvmEnvironmentContent());

        JvmEnvironmentContent jvmEnvironmentContent = new JvmEnvironmentContent();
        EnvironmentContent highestPriorityEnv = jvmEnvironmentContent;
        environmentContents.put(SYSTEM, new SystemEnvironmentContent());
        environmentContents.put(JVM, jvmEnvironmentContent);
        Set<EnvironmentContent> environmentContents;
        try {
            System.setProperty(LoggerConfig.STD_OUT, "true");
            environmentContents = SpiLoader.loadProviderSet(EnvironmentContent.class);
        } catch (Throwable e) {
            System.getLogger(EnvironmentContentHolder.class.getName())
                    .log(System.Logger.Level.ERROR, "load com.truthbean.debbie.env.EnvironmentContent error.", e);
            environmentContents = new HashSet<>();
        }

        environmentContents = environmentContents.stream()
                .sorted(Comparator.comparingInt(EnvironmentContent::getPriority))
                .collect(Collectors.toCollection(LinkedHashSet::new));
        for (
                EnvironmentContent environmentContent : environmentContents) {
            SimpleEnvironmentContentProfile.environmentContents.put(environmentContent.getProfile(), environmentContent);
            highestPriorityEnv = environmentContent;
        }

        highestPriority = highestPriorityEnv.getProfile();
    }

    public void setAllEnvLogger(Logger logger) {
        environmentContents.forEach((profile, env) -> {
            if (env instanceof EnvironmentContentLoggerSetter) {
                ((EnvironmentContentLoggerSetter) env).setLogger(logger);
            }
        });
    }

    public Properties getAllProperties() {
        if (allProperties.isEmpty()) {
            environmentContents.forEach((profile, env) -> {
                /*if (environmentContents instanceof ResourceEnvironmentContent) {
                    ((ResourceEnvironmentContent) environmentContents).loadResource();
                }*/
                Properties properties = env.getProperties();
                allProperties.putAll(properties);
                properties.forEach((k, v) -> allProperties.put(properties + ":" + k, v));
            });
        }
        return allProperties;
    }

    public String getHighestPriority() {
        return highestPriority;
    }

    @Override
    public EnvironmentContent getEnvContent(String profile) {
        return environmentContents.get(profile);
    }

    public EnvironmentContent getSystemEnvContent() {
        return environmentContents.get(SYSTEM);
    }

    public EnvironmentContent getJvmEnvContent() {
        return environmentContents.get(JVM);
    }

    @Override
    public List<EnvironmentContent> getEnvContents() {
        return new ArrayList<>(environmentContents.values());
    }

    public void addEnvContent(String profile, MutableEnvironmentContent envContent) {
        environmentContents.put(profile, envContent);
    }

    public void clear() {
        highestPriority = null;
        environmentContents.clear();
        allProperties.clear();
    }
}
