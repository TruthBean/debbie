/**
 * Copyright (c) 2024 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *         http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.bean;

import com.truthbean.Logger;
import com.truthbean.debbie.io.ResourceResolver;
import com.truthbean.debbie.io.ResourcesHandler;
import com.truthbean.debbie.properties.DebbieConfiguration;
import com.truthbean.debbie.reflection.ReflectionHelper;
import com.truthbean.core.util.StringUtils;
import com.truthbean.LoggerFactory;

import java.lang.annotation.Annotation;
import java.util.*;

/**
 * @author TruthBean
 * @since 0.0.1
 * Created on 2019/3/5 23:16.
 */
public class BeanScanConfiguration implements DebbieConfiguration {
    private String profile;
    private String category;
    private boolean enable;
    private final Set<Class<?>> scanClasses;

    private boolean enableScanResources = true;
    private final Set<String> scanBasePackages;
    private final Set<String> scanExcludePackages;
    private final Set<Class<?>> scanExcludeClasses;

    private final Set<Class<?>> scannedClasses;
    private final Set<Class<? extends Annotation>> customInjectType;

    private volatile ClassLoader classLoader;
    private volatile Class<?> applicationClass;

    public BeanScanConfiguration() {
        this.scanClasses = new HashSet<>();

        this.scanBasePackages = new HashSet<>();
        this.scanExcludePackages = new HashSet<>();
        this.scanExcludeClasses = new HashSet<>();

        this.scannedClasses = new HashSet<>();
        this.customInjectType = new HashSet<>();
    }

    public BeanScanConfiguration(ClassLoader classLoader) {
        this.scanClasses = new HashSet<>();

        this.scanBasePackages = new HashSet<>();
        this.scanExcludePackages = new HashSet<>();
        this.scanExcludeClasses = new HashSet<>();

        this.scannedClasses = new HashSet<>();
        this.customInjectType = new HashSet<>();

        this.classLoader = classLoader;
    }

    @Override
    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    @Override
    public String getProfile() {
        return profile;
    }

    public void setProfile(String profile) {
        this.profile = profile;
    }

    @Override
    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setClassLoader(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    public ClassLoader getClassLoader() {
        return classLoader;
    }

    public void setApplicationClass(Class<?> applicationClass) {
        this.applicationClass = applicationClass;
    }

    public Class<?> getApplicationClass() {
        return applicationClass;
    }

    public Set<Class<? extends Annotation>> getCustomInjectType() {
        return customInjectType;
    }

    public void addCustomInjectType(Set<Class<? extends Annotation>> classes) {
        if (classes != null && !classes.isEmpty()) {
            this.customInjectType.addAll(classes);
        }
    }

    @SafeVarargs
    public final void addCustomInjectType(Class<? extends Annotation>... injectType) {
        if (injectType != null && injectType.length > 0) {
            this.customInjectType.addAll(Arrays.asList(injectType));
        }
    }

    public Set<Class<?>> getScanClasses() {
        return scanClasses;
    }

    public void addScanClasses(Set<Class<?>> scanClasses) {
        if (scanClasses != null) {
            this.scanClasses.addAll(scanClasses);
        }
    }

    public void addScanClasses(Class<?>... classes) {
        if (classes != null) {
            this.scanClasses.addAll(Arrays.asList(classes));
        }
    }

    public void copyFrom(BeanScanConfiguration configuration) {
        this.scanClasses.addAll(configuration.scanClasses);
        this.scanBasePackages.addAll(configuration.scanBasePackages);
        this.scanExcludePackages.addAll(configuration.scanExcludePackages);
        this.scanExcludeClasses.addAll(configuration.scanExcludeClasses);
    }

    public boolean isEnableScanResources() {
        return enableScanResources;
    }

    public void setEnableScanResources(boolean enableScanResources) {
        this.enableScanResources = enableScanResources;
    }

    public Set<String> getScanBasePackages() {
        return scanBasePackages;
    }

    public String getScanBasePackage() {
        boolean flag = scanBasePackages.size() == 1 && scanExcludeClasses.isEmpty() && scanExcludePackages.isEmpty();
        if (flag) {
            return scanBasePackages.iterator().next();
        } else if (scanBasePackages.isEmpty()) {
            return null;
        } else {
            throw new UnsupportedOperationException("scan rule is complex, you cannot do this");
        }
    }

    public void addScanBasePackages(Collection<String> scanBasePackages) {
        this.scanBasePackages.addAll(scanBasePackages);
    }

    public void addScanBasePackages(String... scanBasePackages) {
        if (scanBasePackages != null) {
            for (String scanBasePackage : scanBasePackages) {
                if (StringUtils.hasText(scanBasePackage)) {
                    this.scanBasePackages.add(scanBasePackage);
                }
            }
        }
    }

    public Set<String> getScanExcludePackages() {
        return scanExcludePackages;
    }

    public void addScanExcludePackages(Collection<String> scanExcludePackages) {
        this.scanExcludePackages.addAll(scanExcludePackages);
    }

    public void addScanExcludePackages(String... scanExcludePackages) {
        if (scanExcludePackages != null) {
            this.scanExcludePackages.addAll(Arrays.asList(scanExcludePackages));
        }
    }

    public Set<Class<?>> getScanExcludeClasses() {
        return scanExcludeClasses;
    }

    public void addScanExcludeClasses(Collection<Class<?>> scanExcludeClasses) {
        this.scanExcludeClasses.addAll(scanExcludeClasses);
    }

    public void addScanExcludeClasses(Class<?>... excludeClasses) {
        if (excludeClasses != null) {
            this.scanExcludeClasses.addAll(Arrays.asList(excludeClasses));
        }
    }

    public synchronized Set<Class<?>> getTargetClasses(ResourceResolver resourceResolver) {
        Set<Class<?>> classes = new HashSet<>();
        if (!scanClasses.isEmpty()) {
            classes.addAll(scanClasses);
        }
        if (!scanBasePackages.isEmpty()) {
            scanBasePackages.forEach(packageName -> {
                List<Class<?>> classList = scanClasses(resourceResolver, packageName);
                if (!scanExcludePackages.isEmpty()) {
                    outer: for (Class<?> aClass : classList) {
                        for (String s : scanExcludePackages) {
                            if (aClass.getPackageName().startsWith(s)) {
                                continue outer;
                            }
                        }
                        classes.add(aClass);
                    }
                } else {
                    classes.addAll(classList);
                }
            });
        }
        if (!scanExcludeClasses.isEmpty()) {
            classes.removeAll(scanExcludeClasses);
        }
        scannedClasses.addAll(classes);
        return Collections.unmodifiableSet(scannedClasses);
    }

    private synchronized List<Class<?>> scanClasses(final ResourceResolver resourceResolver, final String packageName) {
        if (classLoader == null) {
            LOGGER.error("classLoader is null!");
            return new ArrayList<>();
        }
        if (enableScanResources) {
            if (!scanExcludePackages.isEmpty()) {
                for (String s : scanExcludePackages) {
                    if (packageName.startsWith(s)) {
                        return new ArrayList<>();
                    }
                }
            }
            if (resourceResolver != null) {
                List<String> resources =
                        ResourcesHandler.getAllClassPathResources(packageName.replace(".", "/"), classLoader);
                resourceResolver.addResource(classLoader, resources);
                return ReflectionHelper.getAllClassByPackageName(packageName, classLoader, resourceResolver);
            } else {
                return ReflectionHelper.getAllClassByPackageName(packageName, classLoader);
            }
        }
        return new ArrayList<>();
    }

    @SuppressWarnings("unused")
    public Set<Class<?>> getScannedClasses() {
        return Collections.unmodifiableSet(this.scannedClasses);
    }

    @Override
    public BeanScanConfiguration copy() {
        BeanScanConfiguration configuration = new BeanScanConfiguration();
        configuration.profile = profile;
        configuration.category = category;
        configuration.enable = enable;
        configuration.scanClasses.addAll(scanClasses);
        configuration.customInjectType.addAll(customInjectType);
        configuration.classLoader = classLoader;
        configuration.applicationClass = applicationClass;
        return configuration;
    }

    @Override
    public void close() {
        synchronized (BeanScanConfiguration.class) {
            this.profile = null;
            this.category = null;
            this.scanClasses.clear();
            this.scanBasePackages.clear();
            this.scanExcludePackages.clear();
            this.scanExcludeClasses.clear();
            this.scannedClasses.clear();
            this.customInjectType.clear();
            this.classLoader = null;
        }
    }

    public static final Logger LOGGER = LoggerFactory.getLogger(BeanScanConfiguration.class);
}
