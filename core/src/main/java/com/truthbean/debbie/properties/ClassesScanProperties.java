package com.truthbean.debbie.properties;

import com.truthbean.debbie.bean.BeanFactoryHandler;
import com.truthbean.debbie.bean.BeanScanConfiguration;

import java.util.List;
import java.util.Set;

/**
 * @author TruthBean
 * @since 0.0.1
 * Created on 2019/3/5 21:58.
 */
public class ClassesScanProperties extends BaseProperties implements DebbieProperties<BeanScanConfiguration> {
    private static final BeanScanConfiguration configuration = new BeanScanConfiguration();

    //===========================================================================
    public static final String SCAN_CLASSES_KEY = "debbie.core.scan.classes";
    public static final String SCAN_BASE_PACKAGES_KEY = "debbie.core.scan.base-packages";
    public static final String SCAN_EXCLUDE_PACKAGES_KEY = "debbie.core.scan.exclude-packages";
    public static final String SCAN_EXCLUDE_CLASSES_KEY = "debbie.core.scan.exclude-classes";
    //===========================================================================

    static {
        ClassesScanProperties properties = new ClassesScanProperties();
        Set<Class<?>> classes = properties.getClassSetValue(SCAN_CLASSES_KEY, ",");
        if (classes != null) {
            configuration.addScanClasses(classes);
        }

        List<String> packages = properties.getStringListValue(SCAN_BASE_PACKAGES_KEY, ",");
        if (packages != null) {
            configuration.addScanBasePackages(packages);
        }

        List<String> excludePackages = properties.getStringListValue(SCAN_EXCLUDE_PACKAGES_KEY, ",");
        if (excludePackages != null) {
            configuration.addScanExcludePackages(excludePackages);
        }

        Set<Class<?>> excludeClasses = properties.getClassSetValue(SCAN_EXCLUDE_CLASSES_KEY, ",");
        if (excludeClasses != null) {
            configuration.addScanExcludeClasses(excludeClasses);
        }
    }

    public static BeanScanConfiguration toConfiguration(ClassLoader classLoader) {
        configuration.setClassLoader(classLoader);
        return configuration;
    }

    @Override
    public BeanScanConfiguration toConfiguration(BeanFactoryHandler beanFactoryHandler) {
        ClassLoader classLoader = beanFactoryHandler.getClassLoader();
        return toConfiguration(classLoader);
    }
}
