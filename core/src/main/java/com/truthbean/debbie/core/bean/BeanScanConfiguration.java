package com.truthbean.debbie.core.bean;

import com.truthbean.debbie.core.properties.DebbieConfiguration;
import com.truthbean.debbie.core.reflection.ReflectionHelper;

import java.util.*;

/**
 * @author TruthBean
 * @since 0.0.1
 * Created on 2019/3/5 23:16.
 */
public class BeanScanConfiguration implements DebbieConfiguration {
    private final Set<Class<?>> scanClasses = new HashSet<>();

    private final Set<String> scanBasePackages = new HashSet<>();
    private final Set<String> scanExcludePackages = new HashSet<>();
    private final Set<Class<?>> scanExcludeClasses = new HashSet<>();

    public Set<Class<?>> getScanClasses() {
        return scanClasses;
    }

    public void addScanClasses(Set<Class<?>> scanClasses) {
        if (scanClasses != null) {
            this.scanClasses.addAll(scanClasses);
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

    public Set<String> getScanExcludePackages() {
        return scanExcludePackages;
    }

    public void addScanExcludePackages(Collection<String> scanExcludePackages) {
        this.scanExcludePackages.addAll(scanExcludePackages);
    }

    public Set<Class<?>> getScanExcludeClasses() {
        return scanExcludeClasses;
    }

    public void addScanExcludeClasses(Collection<Class<?>> scanExcludeClasses) {
        this.scanExcludeClasses.addAll(scanExcludeClasses);
    }

    public Set<Class<?>> getTargetClasses() {
        Set<Class<?>> classes = new HashSet<>();
        if (!scanClasses.isEmpty()) {
            classes.addAll(scanClasses);
        }
        if (!scanBasePackages.isEmpty()) {
            for (String packageName: scanBasePackages) {
                List<Class<?>> classList = ReflectionHelper.getAllClassByPackageName(packageName);
                classes.addAll(classList);
            }
        }
        if (!scanExcludeClasses.isEmpty()) {
            classes.removeAll(scanExcludeClasses);
        }
        if (!scanExcludePackages.isEmpty()) {
            // TODO: 后期优化
            for (String packageName: scanBasePackages) {
                List<Class<?>> classList = ReflectionHelper.getAllClassByPackageName(packageName);
                classes.removeAll(classList);
            }
        }
        return Collections.unmodifiableSet(classes);
    }
}
