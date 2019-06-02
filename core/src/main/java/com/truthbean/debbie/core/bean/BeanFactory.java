package com.truthbean.debbie.core.bean;

/**
 * @author TruthBean
 * @since 0.0.2
 * Created on 2019/06/02 16:50.
 */
public interface BeanFactory<Bean> {

    Bean getBean();

    Class<Bean> getBeanType();

    boolean isSingleton();
}
