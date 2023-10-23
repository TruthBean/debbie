package com.truthbean.debbie.environment;

import com.truthbean.Logger;
import com.truthbean.debbie.spi.SpiLoader;
import com.truthbean.logger.LoggerConfig;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 配置中心，环境仓库
 * @author TruthBean/Rogar·Q
 * @since 0.5.5
 */
public class EnvironmentDepository {

    public static final String ENV = "env";

    public static final String SYSTEM = "sys";

    public static final String JVM = "jvm";

    public static final String PROPERTIES = "props";

    private final Properties allProperties = new Properties();

    private static final Map<String, ProfiledEnvironment> environmentMap = new LinkedHashMap<>();

    private static volatile String highestPriority;

    public EnvironmentDepository() {
    }

    /**
     * 初始化，加载数据
     * 顺序：
     * 1. properties文件
     * 2. system.properties
     * 3. jvm的-D参数
     * 4. system.env
     * 5. 第三方扩展spi
     */
    static void init() {
        // 自定义读取properties/yml/json等文件
        Set<ProfiledEnvironment> propertiesEnvironments = new HashSet<>();
        try {
            System.setProperty(LoggerConfig.STD_OUT, "true");
            Set<ResourceEnvironment> set = SpiLoader.loadProviderSet(ResourceEnvironment.class);
            if (set != null && !set.isEmpty()) {
                for (ResourceEnvironment environment : set) {
                    Map<String, ProfiledEnvironment> map = environment.loadResources();
                    propertiesEnvironments.add(environment);
                    propertiesEnvironments.addAll(map.values());
                }
            }
        } catch (Throwable e) {
            System.getLogger(EnvironmentDepository.class.getName())
                    .log(System.Logger.Level.ERROR, "load com.truthbean.debbie.env.ResourceEnvironment error.", e);
        }
        propertiesEnvironments.stream()
                .sorted(Comparator.comparingInt(ProfiledEnvironment::priority))
                .collect(Collectors.toCollection(LinkedHashSet::new))
                .forEach(environment -> EnvironmentDepository.environmentMap.put(environment.profile(), environment));

        environmentMap.put(SYSTEM, new SystemEnvironment());
        environmentMap.put(JVM, new JvmEnvironment());
        environmentMap.put(ENV, new EnvEnvironment());

        Set<EnvironmentSpi> spiEnvironments = new HashSet<>();
        try {
            System.setProperty(LoggerConfig.STD_OUT, "true");
            Set<EnvironmentSpi> set = SpiLoader.loadProviderSet(EnvironmentSpi.class);
            if (set != null && !set.isEmpty()) {
                spiEnvironments.addAll(set);
            }
        } catch (Throwable e) {
            System.getLogger(EnvironmentDepository.class.getName())
                    .log(System.Logger.Level.ERROR, "load com.truthbean.debbie.env.Environment error.", e);
        }

        spiEnvironments.stream()
                .sorted(Comparator.comparingInt(EnvironmentSpi::priority))
                .collect(Collectors.toCollection(LinkedHashSet::new))
                .forEach(environment -> environmentMap.put(environment.profile(), environment));

        // 获取最后一个
        for (Map.Entry<String, ProfiledEnvironment> entry : environmentMap.entrySet()) {
            highestPriority = entry.getValue().profile();
        }
    }

    public void setAllEnvLogger(Logger logger) {
        environmentMap.forEach((profile, env) -> {
            if (env instanceof EnvironmentContentLoggerSetter) {
                ((EnvironmentContentLoggerSetter) env).setLogger(logger);
            }
        });
    }

    public Properties getAllProperties() {
        if (allProperties.isEmpty()) {
            environmentMap.forEach((profile, env) -> {
                Properties properties = env.properties();
                // customize properties will cover system properties
                allProperties.putAll(properties);
                properties.forEach((k, v) -> allProperties.put(profile + ":" + k, v));
            });
        }
        return allProperties;
    }

    public String getHighestPriority() {
        return highestPriority;
    }

    public Environment getEnvironment(String profile) {
        return environmentMap.get(profile);
    }

    public boolean hasProfile(String profile) {
        return environmentMap.containsKey(profile);
    }

    public Environment getSystemEnvironment() {
        return environmentMap.get(SYSTEM);
    }

    public Environment getJvmEnvironment() {
        return environmentMap.get(JVM);
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

    public void addEnvironment(String profile, ProfiledEnvironment environment) {
        environmentMap.put(profile, environment);
    }

    public void clear() {
        highestPriority = null;
        environmentMap.clear();
        allProperties.clear();
    }
}
