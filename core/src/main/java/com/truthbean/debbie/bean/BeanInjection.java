package com.truthbean.debbie.bean;

import com.truthbean.core.util.StringUtils;
import com.truthbean.debbie.properties.PropertiesConfigurationBeanFactory;
import com.truthbean.debbie.proxy.BeanProxyType;

import java.util.HashMap;
import java.util.Map;

/**
 * @author TruthBean/Rogar·Q
 * @since 0.5.5
 */
public class BeanInjection<T> {
    private String beanName;

    private final Class<T> beanClass;

    private boolean require;

    private boolean throwException = true;

    private BeanType beanType;

    private BeanProxyType proxyType;

    private final Map<String, Object> resources = new HashMap<>();

    public BeanInjection(Class<T> beanClass) {
        this.beanClass = beanClass;
    }

    public BeanInjection(Class<T> beanClass, BeanInject beanInject, String beanName) {
        this.beanClass = beanClass;
        if (beanInject == null) {
            return;
        }
        String name = beanInject.name();
        if (StringUtils.hasText(name)) {
            name = beanInject.value();
        }
        if (StringUtils.hasText(name)) {
            name = beanName;
        }
        this.beanName = name;
        this.require = beanInject.require();
        this.beanType = beanInject.type();
        this.proxyType = beanInject.proxy();
        addResource(PropertiesConfigurationBeanFactory.profileResourceKey, beanInject.profile());
        addResource(PropertiesConfigurationBeanFactory.categoryResourceKey, beanInject.category());
    }

    public String getBeanName() {
        return beanName;
    }

    public void setBeanName(String beanName) {
        this.beanName = beanName;
    }

    public Class<T> getBeanClass() {
        return beanClass;
    }

    public boolean isRequire() {
        return require;
    }

    public void setRequire(boolean require) {
        this.require = require;
    }

    public boolean isThrowException() {
        return throwException;
    }

    public void setThrowException(boolean throwException) {
        this.throwException = throwException;
    }

    public BeanType getBeanType() {
        return beanType;
    }

    public void setBeanType(BeanType beanType) {
        this.beanType = beanType;
    }

    public BeanProxyType getProxyType() {
        return proxyType;
    }

    public void setProxyType(BeanProxyType proxyType) {
        this.proxyType = proxyType;
    }

    public Map<String, Object> getResources() {
        return resources;
    }

    public Object getResource(String key) {
        return resources.get(key);
    }

    public Object getResource(String key, Object defaultResource) {
        return resources.getOrDefault(key, defaultResource);
    }

    public void addResource(String key, Object resource) {
        this.resources.put(key, resource);
    }

    public void addResources(Map<String, Object> resources) {
        this.resources.putAll(resources);
    }
}
