package com.truthbean.debbie.boot;

import com.truthbean.debbie.bean.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author truthbean/Rogar·Q
 * @since 0.0.2
 */
class DebbieBootApplicationResolver {

    private final BeanFactoryHandler beanFactoryHandler;

    DebbieBootApplicationResolver(BeanFactoryHandler beanFactoryHandler) {
        this.beanFactoryHandler = beanFactoryHandler;
    }

    void resolverApplicationClass(Class<?> applicationClass, BeanScanConfiguration configuration) {
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

            if (configuration.getTargetClasses().isEmpty()) {
                configuration.addScanBasePackages(applicationClass.getPackageName());
            }

            DebbieConfigurationCenter.addConfiguration(configuration);
        }
    }

    public static final Logger LOGGER = LoggerFactory.getLogger(DebbieBootApplicationResolver.class);
}
