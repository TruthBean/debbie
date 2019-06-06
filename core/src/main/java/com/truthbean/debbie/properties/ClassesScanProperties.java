package com.truthbean.debbie.properties;

import com.truthbean.debbie.bean.BeanFactoryHandler;
import com.truthbean.debbie.bean.BeanScanConfiguration;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author TruthBean
 * @since 0.0.1
 * Created on 2019/3/5 21:58.
 */
public class ClassesScanProperties extends BaseProperties implements DebbieProperties<BeanScanConfiguration> {
    private static Set<Class<?>> scanClasses = new HashSet<>();

    private static Set<String> scanBasePackages = new HashSet<>();
    private static Set<String> scanExcludePackages = new HashSet<>();
    private static Set<Class<?>> scanExcludeClasses = new HashSet<>();

    //===========================================================================
    private static final String SCAN_CLASSES_KEY = "debbie.core.scan.classes";
    private static final String SCAN_BASE_PACKAGES_KEY = "debbie.core.scan.base-packages";
    private static final String SCAN_EXCLUDE_PACKAGES_KEY = "debbie.core.scan.exclude-packages";
    private static final String SCAN_EXCLUDE_CLASSES_KEY = "debbie.core.scan.exclude-classes";
    //===========================================================================

    static {
        ClassesScanProperties properties = new ClassesScanProperties();
        Set<Class<?>> classes = properties.getClassSetValue(SCAN_CLASSES_KEY, ",");
        if (classes != null) {
            scanClasses.addAll(classes);
        }
        scanClasses = Collections.unmodifiableSet(scanClasses);

        List<String> packages = properties.getStringListValue(SCAN_BASE_PACKAGES_KEY, ",");
        if (packages != null) {
            scanBasePackages.addAll(packages);
        }
        scanBasePackages = Collections.unmodifiableSet(scanBasePackages);

        List<String> excludePackages = properties.getStringListValue(SCAN_EXCLUDE_PACKAGES_KEY, ",");
        if (excludePackages != null) {
            scanExcludePackages.addAll(excludePackages);
        }
        scanExcludePackages = Collections.unmodifiableSet(scanExcludePackages);

        Set<Class<?>> excludeClasses = properties.getClassSetValue(SCAN_EXCLUDE_CLASSES_KEY, ",");
        if (excludeClasses != null) {
            scanExcludeClasses.addAll(excludeClasses);
        }
        scanExcludeClasses = Collections.unmodifiableSet(scanExcludeClasses);
    }

    public static Set<Class<?>> getScanClasses() {
        return scanClasses;
    }

    public static Set<String> getScanBasePackages() {
        return scanBasePackages;
    }

    public static Set<String> getScanExcludePackages() {
        return scanExcludePackages;
    }

    public static Set<Class<?>> getScanExcludeClasses() {
        return scanExcludeClasses;
    }

    private static BeanScanConfiguration configuration;

    public static BeanScanConfiguration toConfiguration() {
        if (configuration != null) {
            return configuration;
        }

        configuration = new BeanScanConfiguration();
        configuration.addScanBasePackages(scanBasePackages);
        configuration.addScanClasses(scanClasses);
        configuration.addScanExcludeClasses(scanExcludeClasses);
        configuration.addScanExcludePackages(scanExcludePackages);
        return configuration;
    }

    @Override
    public BeanScanConfiguration toConfiguration(BeanFactoryHandler beanFactoryHandler) {
        return toConfiguration();
    }
}
