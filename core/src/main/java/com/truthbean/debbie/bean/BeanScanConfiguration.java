package com.truthbean.debbie.bean;

import com.truthbean.debbie.io.ResourceResolver;
import com.truthbean.debbie.properties.DebbieConfiguration;
import com.truthbean.debbie.reflection.ReflectionHelper;
import com.truthbean.debbie.util.StringUtils;

import java.util.*;

/**
 * @author TruthBean
 * @since 0.0.1
 * Created on 2019/3/5 23:16.
 */
public class BeanScanConfiguration implements DebbieConfiguration {
    private final Set<Class<?>> scanClasses;

    private final Set<String> scanBasePackages;
    private final Set<String> scanExcludePackages;
    private final Set<Class<?>> scanExcludeClasses;

    public ClassLoader classLoader;

    public BeanScanConfiguration() {
        this.scanClasses = new HashSet<>();

        this.scanBasePackages = new HashSet<>();
        this.scanExcludePackages = new HashSet<>();
        this.scanExcludeClasses = new HashSet<>();
    }

    public BeanScanConfiguration(ClassLoader classLoader) {
        this.scanClasses = new HashSet<>();

        this.scanBasePackages = new HashSet<>();
        this.scanExcludePackages = new HashSet<>();
        this.scanExcludeClasses = new HashSet<>();

        this.classLoader = classLoader;
    }

    public void setClassLoader(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    public ClassLoader getClassLoader() {
        return classLoader;
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

    public Set<String> getScanBasePackages() {
        return scanBasePackages;
    }

    public String getScanBasePackage() {
        boolean flag = scanBasePackages.size() == 1 && scanExcludeClasses.isEmpty() && scanExcludePackages.isEmpty();
        if (flag) {
            return scanBasePackages.iterator().next();
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

    public Set<Class<?>> getTargetClasses(ResourceResolver resourceResolver) {
        Set<Class<?>> classes = new HashSet<>();
        if (!scanClasses.isEmpty()) {
            classes.addAll(scanClasses);
        }
        if (!scanBasePackages.isEmpty()) {
            scanBasePackages.forEach(packageName -> {
                List<Class<?>> classList;
                if (resourceResolver != null)
                    classList = ReflectionHelper.getAllClassByPackageName(packageName, classLoader, resourceResolver);
                else
                    classList = ReflectionHelper.getAllClassByPackageName(packageName, classLoader);
                classes.addAll(classList);
            });
        }
        if (!scanExcludeClasses.isEmpty()) {
            classes.removeAll(scanExcludeClasses);
        }
        if (!scanExcludePackages.isEmpty()) {
            // TODO: 后期优化
            scanBasePackages.forEach(packageName -> {
                List<Class<?>> classList;
                if (resourceResolver != null)
                    classList = ReflectionHelper.getAllClassByPackageName(packageName, classLoader, resourceResolver);
                else
                    classList = ReflectionHelper.getAllClassByPackageName(packageName, classLoader);
                classes.removeAll(classList);
            });
        }
        return Collections.unmodifiableSet(classes);
    }

    public void reset() {
        this.scanClasses.clear();
        this.scanBasePackages.clear();
        this.scanExcludePackages.clear();
        this.scanExcludeClasses.clear();
        this.classLoader = null;
    }
}
