package com.truthbean.debbie.jdbc.datasource;

import com.truthbean.debbie.core.bean.BeanFactoryHandler;
import com.truthbean.debbie.core.bean.BeanInitialization;
import com.truthbean.debbie.core.bean.DebbieBeanInfo;

public class DataSourceFactoryBeanRegister {

    public static void register(BeanFactoryHandler beanFactoryHandler, BeanInitialization initialization) {
        DebbieBeanInfo beanInfo = new DebbieBeanInfo<>(DataSourceFactory.class);
        DataSourceFactory factory = DataSourceFactory.factory();
        beanInfo.setBean(factory);
        initialization.init(beanInfo);

        beanFactoryHandler.refreshBeans();
    }
}
