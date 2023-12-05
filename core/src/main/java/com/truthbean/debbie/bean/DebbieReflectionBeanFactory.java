package com.truthbean.debbie.bean;

import com.truthbean.Logger;
import com.truthbean.LoggerFactory;
import com.truthbean.debbie.core.ApplicationContext;
import com.truthbean.debbie.environment.Environment;
import com.truthbean.debbie.environment.EnvironmentDepositoryHolder;
import com.truthbean.debbie.proxy.BeanProxyType;
import com.truthbean.debbie.reflection.ReflectionConfigurer;
import com.truthbean.debbie.reflection.ReflectionHelper;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;

/**
 * @author TruthBean
 * @since 0.5.3
 * Created on 2021/12/04 23:40.
 */
public class DebbieReflectionBeanFactory<Bean> extends ClassBeanInfo<Bean> implements ReflectionBeanFactory<Bean> {

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

    @Override
    public Bean getCreatedBean(ApplicationContext applicationContext) {
        if (created) {
            return bean;
        }
        return factoryBean(applicationContext);
    }

    @Override
    public Bean factoryBean(ApplicationContext applicationContext) {
        if (created && isSingleton()) {
            return bean;
        }
        if (!created || !isSingleton()) {
            createBean(null, null, applicationContext);
        }
        final Bean localBean = preparedBean;
        if (isSingleton()) {
            bean = localBean;
        } else {
            bean = null;
        }
        return localBean;
    }

    /*@Override
    public Bean factoryNamedBean(String name, ApplicationContext applicationContext) {
        // 这里的name没有用到！！！
        if (!created || !isSingleton()) {
            createBean(null, null, applicationContext);
        }
        final Bean localBean = preparedBean;
        if (isSingleton()) {
            bean = localBean;
        } else {
            bean = null;
        }
        return localBean;
    }*/

    /*@Override
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
    public Bean factory(String profile, String category, String name, Class beanClass,
                        BeanType type, BeanProxyType proxyType, ApplicationContext applicationContext) {
        boolean create = false;
        if (type != null && (type == BeanType.NO_LIMIT || type != getBeanType())) {
            create = true;
        }
        if (proxyType != null && proxyType != this.getBeanProxyType()) {
            create = true;
        }
        if (!created || !isSingleton()) {
            create = true;
        }
        if (create) {
            createBean(profile, category, applicationContext);
        }
        final Bean localBean = preparedBean;
        if (isSingleton()) {
            bean = localBean;
        } else {
            bean = null;
        }
        return localBean;
    }*/

    @Override
    public Boolean isProxiedBean() {
        if (created) {
            return bean instanceof Proxy;
        }
        return null;
    }

    private void createBean(String profile, String category, ApplicationContext applicationContext) {
        Bean localBean = null;
        Set<BeanLifecycle> beanLifecycles = applicationContext.getBeanLifecycle();
        boolean hasVirtualConstruct = false;
        boolean hasVirtualFields = false;
        EnvironmentDepositoryHolder environmentHolder = applicationContext.getEnvironmentHolder();
        Environment environment = environmentHolder.getEnvironmentIfPresent(profile);
        Environment defaultEnvironment = applicationContext.getDefaultEnvironment();
        for (BeanLifecycle beanLifecycle : beanLifecycles) {
            if (beanLifecycle.support(getBeanClass()) && beanLifecycle.support(this)) {
                if (preparedBean == null) {
                    if (ReflectionConfigurer.isReflectEnable(environment, defaultEnvironment)) {
                        localBean = beanLifecycle.construct(null, this);
                    }
                    if (isConstructorBeanDependentHasVirtualValue()) {
                        hasVirtualConstruct = true;
                    }
                } else {
                    localBean = preparedBean;
                }
                if (localBean != null) {
                    localBean = beanLifecycle.postConstruct(localBean, this, true);
                }
                if (!hasVirtualConstruct && localBean != null) {
                    if (isFieldBeanDependencyHasVirtualValue()) {
                        hasVirtualFields = true;
                    } else {
                        localBean = beanLifecycle.postConstruct(localBean, this, false, profile, category);
                        localBean = beanLifecycle.doPreCreated(this, localBean, getClazz(), getBeanProxyType());
                        localBean = beanLifecycle.getCreatedBean(localBean, this);
                    }
                }
                if (localBean != null) {
                    preparedBean = localBean;
                }
            }
        }
        if (hasVirtualConstruct || hasVirtualFields) {
            // resolve dependencies virtual value
            for (BeanLifecycle beanLifecycle : beanLifecycles) {
                if (beanLifecycle.support(getBeanClass()) && beanLifecycle.support(this)
                        && beanLifecycle instanceof ReflectionBeanLifecycle) {
                    if (hasVirtualConstruct && ReflectionConfigurer.isReflectEnable(environment, defaultEnvironment)) {
                        localBean = beanLifecycle.construct(preparedBean, this);
                    }
                    if (hasVirtualFields && localBean != null) {
                        localBean = beanLifecycle.postConstruct(localBean, this, false, profile, category);
                        localBean = beanLifecycle.doPreCreated(this, localBean, getClazz(), getBeanProxyType());
                        localBean = beanLifecycle.getCreatedBean(localBean, this);
                    }
                    if (localBean != null) {
                        preparedBean = localBean;
                    }
                }
            }
        }
        if (preparedBean != null && isSingleton()) {
            created = true;
            setVirtualValue(false);
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
    public Bean createPreparedBean(ApplicationContext applicationContext) {
        if (created) {
            return bean;
        }
        if (preparedBean != null) {
            return preparedBean;
        }
        Bean localBean = null;
        Set<BeanLifecycle> beanLifecycles = applicationContext.getBeanLifecycle();
        for (BeanLifecycle beanLifecycle : beanLifecycles) {
            if (beanLifecycle.support(getBeanClass()) && beanLifecycle.support(this)) {
                if (preparedBean == null) {
                    if (ReflectionConfigurer.isReflectEnable(applicationContext.getDefaultEnvironment())) {
                        localBean = beanLifecycle.construct(null, this);
                    }
                } else {
                    localBean = preparedBean;
                }
                if (localBean != null) {
                    localBean = beanLifecycle.postConstruct(localBean, this, true);
                }
                if (localBean != null) {
                    preparedBean = localBean;
                }
            }
        }
        return preparedBean;
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
        Method destroyMethod = getDestroyMethod();
        if (destroyMethod != null) {
            ReflectionHelper.invokeMethod(bean, destroyMethod);
        }
        for (BeanLifecycle beanLifecycle : beanLifecycles) {
            if (beanLifecycle.support(getBeanClass()) && beanLifecycle.support(this)) {
                beanLifecycle.doBeforeDestruct(bean);
                beanLifecycle.destruct(bean);
            }
        }
        bean = null;
        preparedBean = null;
        created = false;
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
