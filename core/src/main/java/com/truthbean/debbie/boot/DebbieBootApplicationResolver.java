/**
 * Copyright (c) 2020 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *         http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.boot;

import com.truthbean.debbie.bean.*;
import com.truthbean.debbie.io.ResourceResolver;
import com.truthbean.debbie.reflection.ReflectionHelper;
import com.truthbean.Logger;
import com.truthbean.logger.LoggerFactory;

import java.util.Set;

/**
 * @author truthbean/Rogar·Q
 * @since 0.0.2
 */
class DebbieBootApplicationResolver {

    private final BeanFactoryContext applicationContext;

    DebbieBootApplicationResolver(BeanFactoryContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    void resolverClasses(BeanScanConfiguration configuration, ResourceResolver resourceResolver) {
        Set<Class<?>> targetClasses = configuration.getTargetClasses(resourceResolver);
        if (targetClasses.isEmpty()) {
            ClassLoader classLoader = this.applicationContext.getClassLoader();
            var allClass = ReflectionHelper.getAllClassByPackageName("**", classLoader, resourceResolver);
            allClass.forEach(configuration::addScanClasses);
        }

        DebbieConfigurationCenter.addConfiguration(configuration);
    }

    void resolverClasses(DebbieScan scan, BeanScanConfiguration configuration, ResourceResolver resourceResolver) {
        if (scan != null) {
            String[] basePackages = scan.basePackages();
            configuration.addScanBasePackages(basePackages);

            Class<?>[] classes = scan.classes();
            configuration.addScanClasses(classes);

            Class<?>[] excludeClasses = scan.excludeClasses();
            configuration.addScanExcludeClasses(excludeClasses);

            String[] excludePackages = scan.excludePackages();
            configuration.addScanExcludePackages(excludePackages);
        }

        resolverClasses(configuration, resourceResolver);
    }

    void resolverApplicationClass(Class<?> applicationClass, BeanScanConfiguration configuration,
                                  ResourceResolver resourceResolver) {
        LOGGER.debug(() -> "applicationClass: " + applicationClass);
        if (applicationClass == null) return;

        BeanInitialization beanInitialization = this.applicationContext.getBeanInitialization();
        beanInitialization.init(applicationClass);
        this.applicationContext.refreshBeans();
        DebbieBootApplication debbieBootApplication = applicationClass.getAnnotation(DebbieBootApplication.class);
        if (debbieBootApplication != null) {
            DebbieScan scan = debbieBootApplication.scan();
            String[] basePackages = scan.basePackages();
            configuration.addScanBasePackages(basePackages);

            Class<?>[] classes = scan.classes();
            configuration.addScanClasses(classes);

            Class<?>[] excludeClasses = scan.excludeClasses();
            configuration.addScanExcludeClasses(excludeClasses);

            String[] excludePackages = scan.excludePackages();
            configuration.addScanExcludePackages(excludePackages);

            Set<Class<?>> targetClasses = configuration.getTargetClasses(resourceResolver);
            if (targetClasses.isEmpty()) {
                configuration.addScanBasePackages(applicationClass.getPackageName());
            }

            DebbieConfigurationCenter.addConfiguration(configuration);
        } else {
            Set<Class<?>> targetClasses = configuration.getTargetClasses(resourceResolver);
            if (targetClasses.isEmpty()) {
                ClassLoader classLoader = this.applicationContext.getClassLoader();
                var allClass = ReflectionHelper.getAllClassByPackageName("**", classLoader, resourceResolver);
                if (!allClass.isEmpty()) {
                    boolean hasDebbieBootApplication = false;
                    for (Class<?> clazz : allClass) {
                        DebbieBootApplication bootApplication = clazz.getAnnotation(DebbieBootApplication.class);
                        if (bootApplication != null) {
                            hasDebbieBootApplication = true;
                            resolverApplicationClass(clazz, configuration, resourceResolver);
                            break;
                        }
                    }
                    if (!hasDebbieBootApplication) {
                        LOGGER.warn("No class annotated @DebbieBootApplication ");
                    }
                }
            }
        }
    }

    public static final Logger LOGGER = LoggerFactory.getLogger(DebbieBootApplicationResolver.class);
}
