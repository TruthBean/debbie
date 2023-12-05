package com.truthbean.debbie.bean;

import com.truthbean.debbie.core.ApplicationContext;
import com.truthbean.debbie.proxy.BeanProxyType;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Supplier;

/**
 * @author TruthBean/Rogar·Q
 * @since 0.5.5
 */
public class SimpleMutableBeanInfo<Bean> implements MutableBeanInfo<Bean> {
    protected volatile Bean bean;

    private final Class<?> beanClass;
    protected BeanType beanType;
    protected BeanProxyType beanProxyType;
    protected final Set<String> beanNames;
    private final Set<BeanCondition> conditions = new HashSet<>();

    public SimpleMutableBeanInfo(Class<Bean> beanClass) {
        this.beanClass = beanClass;
        this.beanNames = new HashSet<>();
    }

    @Override
    public Class<?> getBeanClass() {
        return beanClass;
    }

    @Override
    public Set<String> getAllName() {
        return beanNames;
    }

    @Override
    public BeanInfo<Bean> copy() {
        return this;
    }

    @Override
    public Supplier<Bean> supply(ApplicationContext applicationContext) {
        return null;
    }

    @Override
    public void setBeanProxyType(BeanProxyType beanProxyType) {
        this.beanProxyType = beanProxyType;
    }

    @Override
    public void setBeanType(BeanType beanType) {
        this.beanType = beanType;
    }

    @Override
    public void addBeanName(String... beanNames) {

    }

    @Override
    public void addBeanName(Set<String> beanNames) {

    }

    @Override
    public void setBean(Bean bean) {

    }

    @Override
    public <T extends Bean> void setBean(Supplier<T> bean) {

    }

    @Override
    public void addProperty(String name, Object value) {

    }

    @Override
    public Object getProperty(String name) {
        return null;
    }
}
