package com.truthbean.debbie.mvc;

import com.truthbean.debbie.bean.BeanFactoryHandler;
import com.truthbean.debbie.bean.BeanInitialization;
import com.truthbean.debbie.boot.DebbieModuleStarter;
import com.truthbean.debbie.mvc.filter.FilterRegister;
import com.truthbean.debbie.properties.DebbieConfigurationFactory;

/**
 * @author truthbean
 * @since 0.0.2
 */
public class MvcModuleStarter implements DebbieModuleStarter {
    @Override
    public void registerBean(BeanFactoryHandler beanFactoryHandler, BeanInitialization beanInitialization) {
        FilterRegister filterRegister = new FilterRegister();
        filterRegister.setInitialization(beanInitialization);
        beanInitialization.addAnnotationRegister(filterRegister);
    }

    @Override
    public void configure(DebbieConfigurationFactory configurationFactory, BeanFactoryHandler beanFactoryHandler) {
        configurationFactory.register(MvcProperties.class, MvcConfiguration.class);
    }

    @Override
    public int getOrder() {
        return 11;
    }
}
