package com.truthbean.debbie.bean;

import com.truthbean.debbie.proxy.BeanProxyType;

/**
 * bean lifecycle:  <br/>
 * 1. getBeanInfo (could ignore) <br/>
 * 2. construct (could ignore) <br /> eg. static method, constructor and so on
 * <p>
 * 2. postConstruct eg. invoke special method(eg. postConstruct, aware), field <br/>
 * 3. doPreCreated eg. do customize processor <br/>
 * 4. getCreatedBean <br/>
 * <p>
 * 5. doBeforeDestruct <br/>
 * 6. destruct <br/>
 *
 * @author TruthBean
 * @since 0.5.3
 * Created on 2021/12/03 21:44.
 */
public interface BeanLifecycle {

    default boolean support(Class<?> clazz) {
        return true;
    }

    default boolean support(BeanFactory<?> beanFactory) {
        return true;
    }

    <T> T construct(T preparedBean, Object... params);

    <T> T postConstruct(T bean, Object... params);

    default <T, K extends T> T doPreCreated(BeanInfo<K> beanInfo, K bean, Class<T> clazz, BeanProxyType proxyType) {
        return bean;
    }

    default <Bean> Bean getCreatedBean(Bean bean, Object... params) {
        return bean;
    }

    default void doBeforeDestruct(Object bean) {
    }

    default void destruct(Object bean) {
    }
}
