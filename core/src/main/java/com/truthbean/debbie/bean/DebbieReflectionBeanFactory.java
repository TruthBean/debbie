package com.truthbean.debbie.bean;

import com.truthbean.Logger;
import com.truthbean.LoggerFactory;
import com.truthbean.debbie.core.ApplicationContext;
import com.truthbean.debbie.proxy.BeanProxyType;
import com.truthbean.debbie.reflection.ReflectionConfigurer;

import java.lang.annotation.Annotation;
import java.lang.reflect.Proxy;
import java.util.*;
import java.util.function.Supplier;

/**
 * @author TruthBean
 * @since 0.5.3
 * Created on 2021/12/04 23:40.
 */
public class DebbieReflectionBeanFactory<Bean> extends ClassBeanInfo<Bean> implements BeanFactory<Bean> {

    private volatile Bean preparedBean;
    private Bean bean;

    private volatile boolean created;

    public DebbieReflectionBeanFactory(Class<Bean> beanClass) {
        super(beanClass);
    }

    public DebbieReflectionBeanFactory(ClassBeanInfo<Bean> beanFactory) {
        super(beanFactory);
    }

    public DebbieReflectionBeanFactory(DebbieReflectionBeanFactory<Bean> beanFactory) {
        super(beanFactory);
        this.preparedBean = beanFactory.preparedBean;
        this.bean = beanFactory.bean;
        this.created = beanFactory.created;
    }

    public DebbieReflectionBeanFactory(Class<Bean> beanClass, Bean preparedBean) {
        super(beanClass, preparedBean);
        this.preparedBean = preparedBean;
    }

    public DebbieReflectionBeanFactory(Class<Bean> beanClass, Map<Class<? extends Annotation>, BeanComponentParser> componentAnnotationTypes) {
        super(beanClass, componentAnnotationTypes);
    }

    public Bean getCreatedBean(ApplicationContext applicationContext) {
        if (created) {
            return bean;
        }
        return factoryBean(applicationContext);
    }

    @Override
    public Bean factoryNamedBean(String name, ApplicationContext applicationContext) {
        if (!created) {
            createBean(applicationContext);
        } else if (!isSingleton()) {
            createBean(applicationContext);
        }
        final Bean localBean = preparedBean;
        if (isSingleton()) {
            bean = localBean;
        } else {
            bean = null;
        }
        return localBean;
    }

    @Override
    @SuppressWarnings({"unchecked", "rawtypes"})
    public Bean factoryProxiedBean(String name, Class beanInterface, ApplicationContext applicationContext) {
        Bean needBean;
        if (!isCreated()) {
            needBean = factoryNamedBean(name, applicationContext);
        } else {
            needBean = getCreatedBean();
        }
        if ((isCreated() || (!isSingleton() && isPreparationCreated())) && beanInterface != null && beanInterface.isInterface()) {
            if (isCreated()) {
                needBean = getCreatedBean();
            } else if (isPreparationCreated()) {
                needBean = getPreparedBean();
            }
            if (!(needBean instanceof Proxy)) {
                Set<BeanLifecycle> beanLifecycles = applicationContext.getBeanLifecycle();
                for (BeanLifecycle beanLifecycle : beanLifecycles) {
                    if (beanLifecycle.support(getBeanClass()) && beanLifecycle.support(this)) {
                        Bean proxy = (Bean) beanLifecycle.doPreCreated(this, needBean, beanInterface, BeanProxyType.JDK);
                        if (proxy instanceof Proxy) {
                            return proxy;
                        }
                    }
                }
            }
            return needBean;
        }
        return needBean;
    }

    @Override
    public Boolean isProxiedBean() {
        if (created) {
            return bean instanceof Proxy;
        }
        return null;
    }

    private void createBean(ApplicationContext applicationContext) {
        Bean localBean = null;
        Set<BeanLifecycle> beanLifecycles = applicationContext.getBeanLifecycle();
        for (BeanLifecycle beanLifecycle : beanLifecycles) {
            if (beanLifecycle.support(getBeanClass()) && beanLifecycle.support(this)) {
                if (preparedBean == null) {
                    if (ReflectionConfigurer.isReflectEnable(applicationContext.getEnvContent())) {
                        localBean = beanLifecycle.construct(null, this);
                    }
                } else {
                    localBean = preparedBean;
                }
                if (localBean != null) {
                    localBean = beanLifecycle.postConstruct(localBean, this);
                    localBean = beanLifecycle.doPreCreated(this, localBean, getClazz(), BeanProxyType.JDK);
                    localBean = beanLifecycle.getCreatedBean(localBean);
                }
                if (localBean != null) {
                    preparedBean = localBean;
                }
            }
        }
        if (preparedBean != null && isSingleton()) {
            created = true;
        }
    }

    @Override
    public void setBean(Bean bean) {
        super.setBean(bean);
        this.bean = bean;
        this.preparedBean = bean;
        this.created = true;
    }

    @Override
    public <T extends Bean> void setBean(Supplier<T> bean) {
        this.setBean(bean.get());
    }

    @Override
    public Bean getBean() {
        return bean;
    }

    @Override
    public boolean isCreated() {
        return created;
    }

    @Override
    public Bean getCreatedBean() {
        if (created) {
            return bean;
        }
        return null;
    }

    @Override
    public Logger getLogger() {
        return LOGGER;
    }

    @Override
    public void destruct(ApplicationContext applicationContext) {
        Set<BeanLifecycle> beanLifecycles = applicationContext.getBeanLifecycle();
        for (BeanLifecycle beanLifecycle : beanLifecycles) {
            if (beanLifecycle.support(getBeanClass()) && beanLifecycle.support(this)) {
                beanLifecycle.doBeforeDestruct(bean);
                beanLifecycle.destruct(bean);
            }
        }
        bean = null;
        super.destruct(applicationContext);
    }

    @Override
    public DebbieReflectionBeanFactory<Bean> copy() {
        DebbieReflectionBeanFactory<Bean> factory = new DebbieReflectionBeanFactory<>(getClazz());
        factory.copyFrom(factory);
        return factory;
    }

    @Override
    public void copyFrom(ClassBeanInfo<Bean> beanInfo) {
        super.copyFrom(beanInfo);
        this.bean = beanInfo.getBean();
        this.preparedBean = beanInfo.getPreparedBean();
        this.created = beanInfo.isCreated();
    }

    @Override
    public boolean equals(Object o) {
        return isEquals(o);
    }

    @Override
    public int hashCode() {
        return getHashCode(super.hashCode());
    }

    @Override
    public String toString() {
        return "\"DebbieReflectionBeanFactory\":" + super.toString();
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(DebbieReflectionBeanFactory.class);
}
