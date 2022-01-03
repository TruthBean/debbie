package com.truthbean.debbie.proxy;

import com.truthbean.debbie.bean.BeanInfo;

/**
 * @author TruthBean
 * @since 0.5.3
 * Created on 2021/11/14 22:12.
 */
public interface BeanProxyHandler {

    <T, K extends T> T proxyCreatedBean(BeanInfo<K> beanInfo, K bean, BeanProxyType proxyType);

    default <T, K extends T> T proxyCreatedBean(K bean, Class<T> clazz, BeanProxyType proxyType) {
        return bean;
    }
}
