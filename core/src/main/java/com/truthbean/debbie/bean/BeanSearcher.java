package com.truthbean.debbie.bean;

import com.truthbean.debbie.properties.PropertiesConfigurationBeanFactory;
import com.truthbean.debbie.proxy.BeanProxyType;

import java.util.HashMap;
import java.util.Map;

/**
 * @author TruthBean/Rogar·Q
 * @since 0.5.5
 */
public class BeanSearcher {
    private String beanName;

    private final Class<?> beanClass;

    private boolean require;

    private boolean proxy;

    private boolean throwException = true;

    private BeanType beanType;

    private BeanProxyType proxyType;

    private final Map<String, Object> resources = new HashMap<>();

    public BeanSearcher(Class<?> beanClass) {
        this.beanClass = beanClass;
    }

    public BeanSearcher(Class<?> beanClass, BeanInject beanInject) {
        this.beanClass = beanClass;
        if (beanInject == null) {
            return;
        }
        String name = beanInject.name();
        if (name.isBlank()) {
            name = beanInject.value();
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

    public Class getBeanClass() {
        return beanClass;
    }

    public boolean isRequire() {
        return require;
    }

    public void setRequire(boolean require) {
        this.require = require;
    }

    public boolean isProxy() {
        return proxy;
    }

    public void setProxy(boolean proxy) {
        this.proxy = proxy;
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
