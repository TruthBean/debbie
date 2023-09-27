package com.truthbean.debbie.bean;

import com.truthbean.debbie.core.ApplicationContext;

/**
 * @author TruthBean
 * @since 0.5.3
 * Created on 2021/11/26 19:50.
 */
public interface ReflectionBeanFactory<Bean> extends MutableBeanFactory<Bean> {

    @Override
    boolean isCreated();

    Bean createPreparedBean(ApplicationContext applicationContext);
}
