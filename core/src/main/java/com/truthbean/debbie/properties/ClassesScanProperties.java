/**
 * Copyright (c) 2020 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *         http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.properties;

import com.truthbean.debbie.bean.DebbieApplicationContext;
import com.truthbean.debbie.bean.BeanScanConfiguration;

import java.lang.annotation.Annotation;
import java.util.List;
import java.util.Set;

/**
 * @author TruthBean
 * @since 0.0.1
 * Created on 2019/3/5 21:58.
 */
@SuppressWarnings({"unchecked"})
public class ClassesScanProperties extends BaseProperties implements DebbieProperties<BeanScanConfiguration> {
    private static final BeanScanConfiguration configuration = new BeanScanConfiguration();

    //===========================================================================
    public static final String SCAN_CLASSES_KEY = "debbie.core.scan.classes";
    public static final String SCAN_BASE_PACKAGES_KEY = "debbie.core.scan.base-packages";
    public static final String SCAN_EXCLUDE_PACKAGES_KEY = "debbie.core.scan.exclude-packages";
    public static final String SCAN_EXCLUDE_CLASSES_KEY = "debbie.core.scan.exclude-classes";

    public static final String CUSTOM_INJECT_KEY = "debbie.core.scan.inject-classes";
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

        Set<Class<?>> injectClasses = properties.getClassSetValue(CUSTOM_INJECT_KEY, ",");
        if (injectClasses != null) {
            for (Class<?> injectClass : injectClasses) {
                if (Annotation.class.isAssignableFrom(injectClass)) {
                    configuration.addCustomInjectType((Class<? extends Annotation>) injectClass);
                }
            }
        }
    }

    public static BeanScanConfiguration toConfiguration(ClassLoader classLoader) {
        configuration.setClassLoader(classLoader);
        return configuration;
    }

    @Override
    public BeanScanConfiguration toConfiguration(DebbieApplicationContext applicationContext) {
        ClassLoader classLoader = applicationContext.getClassLoader();
        return toConfiguration(classLoader);
    }
}
