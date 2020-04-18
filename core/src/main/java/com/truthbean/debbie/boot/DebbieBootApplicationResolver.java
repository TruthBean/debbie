package com.truthbean.debbie.boot;

import com.truthbean.debbie.bean.*;
import com.truthbean.debbie.io.ResourceResolver;
import com.truthbean.debbie.reflection.ReflectionHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;

/**
 * @author truthbean/Rogar·Q
 * @since 0.0.2
 */
class DebbieBootApplicationResolver {

    private final BeanFactoryHandler beanFactoryHandler;

    DebbieBootApplicationResolver(BeanFactoryHandler beanFactoryHandler) {
        this.beanFactoryHandler = beanFactoryHandler;
    }

    void resolverApplicationClass(Class<?> applicationClass, BeanScanConfiguration configuration,
                                  ResourceResolver resourceResolver) {
        LOGGER.debug("applicationClass: " + applicationClass);
        if (applicationClass == null) return;

        BeanInitialization beanInitialization = this.beanFactoryHandler.getBeanInitialization();
        beanInitialization.init(applicationClass);
        this.beanFactoryHandler.refreshBeans();
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
                ClassLoader classLoader = this.beanFactoryHandler.getClassLoader();
                var allClass = ReflectionHelper.getAllClassByPackageName("**", classLoader, resourceResolver);
                if (allClass != null && !allClass.isEmpty()) {
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
