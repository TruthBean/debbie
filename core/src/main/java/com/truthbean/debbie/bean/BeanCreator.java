package com.truthbean.debbie.bean;

import com.truthbean.Logger;
import com.truthbean.LoggerFactory;
import com.truthbean.debbie.core.ApplicationContext;
import com.truthbean.debbie.core.ApplicationContextAware;
import com.truthbean.debbie.env.EnvContentAware;
import com.truthbean.debbie.event.DebbieEventPublisherAware;

/**
 *
 * @author TruthBean
 * @since 0.5.3
 * Created on 2021/12/03 21:13.
 */
public interface BeanCreator {

    default void doConstructPost(Object bean) {
        if (bean instanceof ConstructPost) {
            ((ConstructPost) bean).postConstruct();
        }
    }

    default void resolveAwareValue(ApplicationContext applicationContext, Object object) {
        if (applicationContext.isExiting()) {
            return;
        }
        if (object instanceof ClassLoaderAware) {
            ((ClassLoaderAware) object).setClassLoader(applicationContext.getClassLoader());
        } else if (object instanceof ApplicationContextAware) {
            ((ApplicationContextAware) object).setApplicationContext(applicationContext);
        } else if (object instanceof GlobalBeanFactoryAware) {
            ((GlobalBeanFactoryAware) object).setGlobalBeanFactory(applicationContext.getGlobalBeanFactory());
        } else if (object instanceof DebbieEventPublisherAware) {
            ((DebbieEventPublisherAware) object).setEventPublisher(applicationContext);
        } else if (object instanceof EnvContentAware) {
            ((EnvContentAware) object).setEnvContent(applicationContext.getEnvContent());
        }
    }

    default Logger getLogger() {
        return LoggerFactory.getLogger(BeanCreator.class);
    }
}
