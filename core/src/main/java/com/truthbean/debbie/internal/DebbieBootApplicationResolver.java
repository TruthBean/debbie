/**
 * Copyright (c) 2023 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *         http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.internal;

import com.truthbean.debbie.bean.*;
import com.truthbean.debbie.boot.DebbieBootApplication;
import com.truthbean.debbie.boot.exception.DebbieApplicationException;
import com.truthbean.debbie.core.ApplicationContext;
import com.truthbean.debbie.io.ResourceResolver;
import com.truthbean.debbie.properties.ClassesScanProperties;
import com.truthbean.debbie.properties.PropertiesConfigurationBeanFactory;
import com.truthbean.debbie.reflection.ReflectionHelper;
import com.truthbean.Logger;
import com.truthbean.LoggerFactory;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Set;

/**
 * @author truthbean/Rogar·Q
 * @since 0.0.2
 */
class DebbieBootApplicationResolver {

    private final ApplicationContext applicationContext;

    DebbieBootApplicationResolver(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    void resolverClasses(BeanScanConfiguration configuration, ResourceResolver resourceResolver) {
        Set<Class<?>> targetClasses = configuration.getTargetClasses(resourceResolver);
        if (targetClasses.isEmpty()) {
            ClassLoader classLoader = this.applicationContext.getClassLoader();
            var allClass = ReflectionHelper.getAllClassByPackageName("**", classLoader, resourceResolver);
            allClass.forEach(configuration::addScanClasses);
        }

        var beanFactory = new PropertiesConfigurationBeanFactory<>(new ClassesScanProperties(), BeanScanConfiguration.class);
        BeanInfoManager beanInfoManager = applicationContext.getBeanInfoManager();
        beanInfoManager.registerBeanInfo(beanFactory);
        var bean = beanFactory.factoryBean(applicationContext);
        configuration.copyFrom(bean);
    }

    @SuppressWarnings("unused")
    void resolverClasses(DebbieScan scan, BeanScanConfiguration configuration, ResourceResolver resourceResolver) {
        if (scan != null) {
            resolveDebbieScan(configuration, scan);
        }

        resolverClasses(configuration, resourceResolver);
    }

    void resolverApplicationClass(Class<?> applicationClass, BeanScanConfiguration configuration,
                                  ResourceResolver resourceResolver) {
        LOGGER.info(() -> "application entry class: " + applicationClass);
        if (notSupport(applicationClass)) {
            LOGGER.warn("application class(" + applicationClass + ") not support");
            return;
        }

        var beanInfoManager = this.applicationContext.getBeanInfoManager();
        DebbieReflectionBeanFactory<?> applicationBeanFactory = new DebbieReflectionBeanFactory<>(applicationClass);
        resolveApplicationBean(configuration, resourceResolver, applicationClass, beanInfoManager, applicationBeanFactory);
    }

    @SuppressWarnings("unchecked")
    void resolverApplicationClass(Object application, BeanScanConfiguration configuration,
                                  ResourceResolver resourceResolver) {
        if (application == null) {
            return;
        }
        Class<?> applicationClass = application.getClass();
        LOGGER.info(() -> "application entry class: " + applicationClass);
        if (notSupport(applicationClass)) {
            LOGGER.warn("application class(" + applicationClass + ") not support");
            return;
        }

        var beanInfoManager = this.applicationContext.getBeanInfoManager();
        DebbieReflectionBeanFactory applicationBeanFactory = new DebbieReflectionBeanFactory(applicationClass, application);
        resolveApplicationBean(configuration, resourceResolver, applicationClass, beanInfoManager, applicationBeanFactory);
    }

    private void resolveApplicationBean(BeanScanConfiguration configuration, ResourceResolver resourceResolver,
                                        Class<?> applicationClass, BeanInfoManager beanInfoManager,
                                        DebbieReflectionBeanFactory<?> applicationBeanFactory) {
        applicationBeanFactory.setBeanType(BeanType.SINGLETON);
        beanInfoManager.registerBeanInfo(applicationBeanFactory);

        Annotation annotation = applicationBeanFactory.getAnnotatedClassAnnotation(DebbieBootApplication.class);
        if (annotation != null) {
            DebbieBootApplication debbieBootApplication = applicationBeanFactory.getClassAnnotation(DebbieBootApplication.class);
            resolveDebbieBootApplicationAnnotation(annotation, debbieBootApplication, configuration);
            Set<String> packages = configuration.getScanBasePackages();
            String packageName = applicationClass.getPackageName();
            boolean flag = true;
            for (String s : packages) {
                if (packageName.startsWith(s)) {
                    flag = false;
                    break;
                }
            }
            if (flag) {
                configuration.addScanBasePackages(packageName);
            }

            var beanFactory = new PropertiesConfigurationBeanFactory<>(new ClassesScanProperties(), BeanScanConfiguration.class);
            beanInfoManager.registerBeanInfo(beanFactory);
            var bean = beanFactory.factoryBean(applicationContext);
            configuration.copyFrom(bean);
            configuration.setApplicationClass(applicationClass);
            return;
        }

        DebbieBootApplication debbieBootApplication = applicationClass.getAnnotation(DebbieBootApplication.class);
        if (debbieBootApplication != null) {
            DebbieScan scan = debbieBootApplication.scan();
            resolveDebbieScan(configuration, scan);

            Set<String> packages = configuration.getScanBasePackages();
            String packageName = applicationClass.getPackageName();
            boolean flag = true;
            for (String s : packages) {
                if (packageName.startsWith(s)) {
                    flag = false;
                    break;
                }
            }
            if (flag) {
                configuration.addScanBasePackages(packageName);
            }

            Class<? extends Annotation>[] injectTypes = debbieBootApplication.customInjectType();
            configuration.addCustomInjectType(injectTypes);

            configuration.setApplicationClass(applicationClass);
            var beanFactory = new PropertiesConfigurationBeanFactory<>(new ClassesScanProperties(), BeanScanConfiguration.class);
            beanInfoManager.registerBeanInfo(beanFactory);
            var bean = beanFactory.factoryBean(applicationContext);
            configuration.copyFrom(bean);
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
                        throw new DebbieApplicationException(applicationClass + " has no @DebbieBootApplication or no annotated " +
                                "by Annotation annotated By @DebbieBootApplication");
                    }
                }
            }
        }
    }

    private void resolveDebbieScan(BeanScanConfiguration configuration, DebbieScan scan) {
        String[] basePackages = scan.basePackages();
        configuration.addScanBasePackages(basePackages);

        Class<?>[] classes = scan.classes();
        configuration.addScanClasses(classes);

        Class<?>[] excludeClasses = scan.excludeClasses();
        configuration.addScanExcludeClasses(excludeClasses);

        String[] excludePackages = scan.excludePackages();
        configuration.addScanExcludePackages(excludePackages);
    }

    @SuppressWarnings("unchecked")
    private void resolveDebbieBootApplicationAnnotation(Annotation annotation,
                                                        DebbieBootApplication debbieBootApplication,
                                                        BeanScanConfiguration configuration) {
        try {
            Method customInjectType = ReflectionHelper.getMethod(annotation.annotationType(), "customInjectType", new Class[0]);
            if (customInjectType != null) {
                var injectTypes = (Class<? extends Annotation>[]) ReflectionHelper.invokeMethod(annotation, customInjectType);
                if (injectTypes != null && injectTypes.length > 0) {
                    configuration.addCustomInjectType(injectTypes);
                }
            } else {
                configuration.addCustomInjectType(debbieBootApplication.customInjectType());
            }
        } catch (Exception ignored) {
        }
        try {
            Method scanMethod = ReflectionHelper.getMethod(annotation.annotationType(), "scan", new Class[0]);
            if (scanMethod != null) {
                var scan = (DebbieScan) ReflectionHelper.invokeMethod(annotation, scanMethod);
                if (scan != null) {
                    resolveDebbieScan(configuration, scan);
                }
            } else {
                resolveDebbieScan(configuration, debbieBootApplication.scan());
            }
        } catch (Exception ignored) {
        }
    }

    private boolean notSupport(Class<?> applicationClass) {
        return applicationClass == null || applicationClass.isAnnotation()
                || applicationClass.isInterface()
                || Modifier.isAbstract(applicationClass.getModifiers());
    }

    public static final Logger LOGGER = LoggerFactory.getLogger(DebbieBootApplicationResolver.class);
}
