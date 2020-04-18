package com.truthbean.debbie.boot;

import com.truthbean.debbie.bean.BeanFactoryHandler;
import com.truthbean.debbie.bean.BeanInitialization;
import com.truthbean.debbie.properties.DebbieConfigurationFactory;

/**
 * @author TruthBean
 * @since 0.0.2
 */
public interface DebbieModuleStarter extends Comparable<DebbieModuleStarter> {

    default void registerBean(BeanFactoryHandler beanFactoryHandler, BeanInitialization beanInitialization) {
        // do nothing
    }

    default void configure(DebbieConfigurationFactory configurationFactory, BeanFactoryHandler beanFactoryHandler) {
        // do nothing
    }

    default void starter(DebbieConfigurationFactory configurationFactory, BeanFactoryHandler beanFactoryHandler) {
        // do nothing
    }

    int getOrder();

    default void release(DebbieConfigurationFactory configurationFactory, BeanFactoryHandler beanFactoryHandler) {
        // do nothing
    }

    @Override
    default int compareTo(DebbieModuleStarter o) {
        if (o != null) {
            int x = getOrder();
            int y = o.getOrder();
            return Integer.compare(x, y);
        }
        return -1;
    }

    default String toStr() {
        return getClass().getName() + "@" + Integer.toHexString(hashCode()) + " | order : " + getOrder();
    }
}
