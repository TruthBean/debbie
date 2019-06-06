package com.truthbean.debbie.properties;

import com.truthbean.debbie.bean.BeanFactoryHandler;

/**
 * @author TruthBean
 * @since 0.0.2
 */
public interface DebbieProperties<C extends DebbieConfiguration> {

    C toConfiguration(BeanFactoryHandler beanFactoryHandler);
}
