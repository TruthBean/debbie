package com.truthbean.debbie.bean;

import com.truthbean.debbie.proxy.BeanProxyType;

import java.util.Set;
import java.util.function.Supplier;

/**
 * @author TruthBean
 * @since 0.5.3
 * Created on 2021/12/07 14:02.
 */
public interface MutableBeanInfo<Bean> extends MultiNameBeanInfo<Bean> {
    void setBeanProxyType(BeanProxyType beanProxyType);

    void setBeanType(BeanType beanType);

    void addBeanName(String... beanNames);

    void addBeanName(Set<String> beanNames);

    void setBean(Bean bean);

    <T extends Bean> void setBean(Supplier<T> bean);

    void addProperty(String name, Object value);

    Object getProperty(String name);
}
