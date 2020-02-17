package com.truthbean.debbie.boot;

import com.truthbean.debbie.bean.BeanFactoryHandler;
import com.truthbean.debbie.bean.BeanInitialization;
import com.truthbean.debbie.properties.DebbieConfigurationFactory;

/**
 * @author TruthBean
 * @since 0.0.2
 */
public interface DebbieModuleStarter extends Comparable<DebbieModuleStarter> {

    void registerBean(BeanFactoryHandler beanFactoryHandler);

    void starter(DebbieConfigurationFactory configurationFactory, BeanFactoryHandler beanFactoryHandler);

    int getOrder();

    void release();

    @Override
    default int compareTo(DebbieModuleStarter o) {
        if (o != null) {
            int x = getOrder();
            int y = o.getOrder();
            return Integer.compare(x, y);
        }
        return -1;
    }
}
