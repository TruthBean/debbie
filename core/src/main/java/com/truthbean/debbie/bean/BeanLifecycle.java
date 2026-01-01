package com.truthbean.debbie.bean;

import com.truthbean.debbie.proxy.BeanProxyType;

/**
 * bean lifecycle:  <br/>
 * 1. construct (could ignore) <br /> eg. static method, constructor and so on
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

    /**
     * check whether the bean lifecycle support the bean class
     * @param clazz bean class
     * @return true if support
     */
    default boolean support(Class<?> clazz) {
        return true;
    }

    /**
     * check whether the bean lifecycle support the bean info
     * @param beanInfo bean info
     * @return true if support
     */
    default boolean support(BeanInfo<?> beanInfo) {
        return true;
    }

    /**
     * create bean instance by params for the prepared bean
     * if the preparedBean is null, then create a new instance by params
     * @param preparedBean prepared bean instance
     * @param params needed params
     * @return bean instance
     * @param <T> bean type
     */
    <T> T construct(T preparedBean, Object... params);

    /**
     * invoke after the bean instance created
     * @param bean bean instance
     * @param params needed params
     * @return bean instance
     * @param <T> bean type
     */
    <T> T postConstruct(T bean, Object... params);

    /**
     * invoke before the bean instance created
     * @param beanInfo bean info
     * @param bean bean instance
     * @param clazz bean class
     * @param proxyType bean proxy type
     * @return bean instance
     * @param <T> bean type
     * @param <K> bean type extends T
     */
    default <T, K extends T> T doPreCreated(BeanInfo<K> beanInfo, K bean, Class<T> clazz, BeanProxyType proxyType) {
        return bean;
    }

    /**
     * get the created bean instance
     * @param bean bean instance
     * @param params needed params
     * @return bean instance
     * @param <Bean> bean type
     */
    default <Bean> Bean getCreatedBean(Bean bean, Object... params) {
        return bean;
    }

    /**
     * invoke before the bean instance destroyed
     * @param bean bean instance
     */
    default void doBeforeDestruct(Object bean) {
    }

     /**
      * destroy the bean instance
      * @param bean bean instance
      */
    default void destruct(Object bean) {
    }
}
