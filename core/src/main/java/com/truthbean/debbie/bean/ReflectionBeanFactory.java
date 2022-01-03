package com.truthbean.debbie.bean;

/**
 * @author TruthBean
 * @since 0.5.3
 * Created on 2021/11/26 19:50.
 */
public interface ReflectionBeanFactory<Bean> extends MutableBeanFactory<Bean> {

    @Override
    boolean isCreated();
}
