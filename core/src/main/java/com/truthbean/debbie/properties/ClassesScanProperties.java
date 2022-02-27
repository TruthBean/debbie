/**
 * Copyright (c) 2022 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *         http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.properties;

import com.truthbean.common.mini.util.StringUtils;
import com.truthbean.debbie.bean.BeanScanConfiguration;
import com.truthbean.debbie.core.ApplicationContext;
import com.truthbean.debbie.env.EnvironmentContentHolder;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author TruthBean
 * @since 0.0.1
 * Created on 2019/3/5 21:58.
 */
@SuppressWarnings({"unchecked"})
public class ClassesScanProperties extends EnvironmentContentHolder implements DebbieProperties<BeanScanConfiguration> {
    private static final Map<String, BeanScanConfiguration> configurationMap = new HashMap<>();

    //===========================================================================
    public static final String SCAN_CLASSES_KEY = "debbie.core.scan.classes";
    public static final String SCAN_BASE_PACKAGES_KEY = "debbie.core.scan.base-packages";
    public static final String SCAN_EXCLUDE_PACKAGES_KEY = "debbie.core.scan.exclude-packages";
    public static final String SCAN_EXCLUDE_CLASSES_KEY = "debbie.core.scan.exclude-classes";

    public static final String CUSTOM_INJECT_KEY = "debbie.core.scan.inject-classes";
    //===========================================================================
    public static final String JDK_PROXY_ENABLE_KEY = "debbie.jdk-proxy.enable";
    public static final String RESOURCE_SCAN_ENABLE_KEY = "debbie.resource.scan.enable";

    static {
        BeanScanConfiguration configuration = new BeanScanConfiguration();

        ClassesScanProperties properties = new ClassesScanProperties();
        Set<Class<?>> classes = properties.getClassSetValue(SCAN_CLASSES_KEY, ",");
        if (classes != null) {
            configuration.addScanClasses(classes);
        }

        boolean scan = properties.getBooleanValue(RESOURCE_SCAN_ENABLE_KEY, true);
        configuration.setEnableScanResources(scan);

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

        configurationMap.put(DEFAULT_PROFILE, configuration);
    }

    public static BeanScanConfiguration toConfiguration(ClassLoader classLoader) {
        BeanScanConfiguration configuration = configurationMap.get(DEFAULT_PROFILE);
        configuration.setClassLoader(classLoader);
        return configuration;
    }

    @Override
    public Set<String> getProfiles() {
        return configurationMap.keySet();
    }

    @Override
    public BeanScanConfiguration getConfiguration(String name, ApplicationContext applicationContext) {
        if (StringUtils.hasText(name)) {
            BeanScanConfiguration configuration = configurationMap.get(name);
            if (configuration.getClassLoader() != null) {
                configuration.setClassLoader(applicationContext.getClassLoader());
            }
            return configuration;
        } else {
            return getConfiguration(applicationContext);
        }
    }

    @Override
    public BeanScanConfiguration getConfiguration(ApplicationContext applicationContext) {
        ClassLoader classLoader = applicationContext.getClassLoader();
        return toConfiguration(classLoader);
    }

    @Override
    public void close() throws IOException {
        configurationMap.clear();
    }
}
