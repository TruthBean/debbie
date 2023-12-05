package com.truthbean.debbie.bean;

import com.truthbean.debbie.core.ApplicationContext;
import com.truthbean.debbie.proxy.BeanProxyType;

/**
 * @author TruthBean/Rogar·Q
 * @since 0.5.5
 */
public interface NamedBeanFactory<Bean> extends BeanFactory<Bean> {

    Bean factoryNamedBean(String name, ApplicationContext applicationContext);

    @SuppressWarnings({"rawtypes"})
    default Bean factory(String name, Class beanClass, BeanType type, BeanProxyType proxyType, ApplicationContext applicationContext) {
        return null;
    }

    @SuppressWarnings({"rawtypes"})
    default Bean factory(String profile, String category, String name, Class beanClass, BeanType type, BeanProxyType proxyType, ApplicationContext applicationContext) {
        return null;
    }
}
