package com.truthbean.debbie.bean;

import com.truthbean.Logger;
import com.truthbean.LoggerFactory;
import com.truthbean.debbie.core.ApplicationContext;
import com.truthbean.debbie.properties.ClassesScanProperties;
import com.truthbean.debbie.proxy.BeanProxyHandler;
import com.truthbean.debbie.proxy.BeanProxyType;

/**
 * @author TruthBean
 * @since 0.5.3
 * Created on 2021/12/03 22:05.
 */
public class SimpleBeanLifecycle extends AbstractBeanLifecycle {

    private final ApplicationContext applicationContext;
    private final BeanProxyHandler beanProxyHandler;
    private final boolean enableJdkProxy;

    public SimpleBeanLifecycle(ApplicationContext applicationContext, BeanProxyHandler beanProxyHandler) {
        this.applicationContext = applicationContext;
        enableJdkProxy = applicationContext.getDefaultEnvironment().getBooleanValue(ClassesScanProperties.JDK_PROXY_ENABLE_KEY, true);
        this.beanProxyHandler = beanProxyHandler;
    }

    @Override
    public boolean support(BeanFactory<?> beanFactory) {
        return beanFactory instanceof SimpleBeanFactory;
    }

    @Override
    public <T> T construct(T preparedBean, Object... params) {
        return preparedBean;
    }

    @Override
    public <T> T postConstruct(T bean, Object... params) {
        LOGGER.trace("postConstruct : " + bean);
        doConstructPost(bean);
        resolveAwareValue(applicationContext, bean);
        return bean;
    }

    @Override
    public <T, K extends T> T doPreCreated(BeanInfo<K> beanInfo, K bean, Class<T> clazz, BeanProxyType proxyType) {
        if (enableJdkProxy && beanProxyHandler != null) {
            return beanProxyHandler.proxyCreatedBean(beanInfo, bean, clazz, proxyType);
        }
        return bean;
    }

    @Override
    public <Bean> Bean getCreatedBean(Bean bean, Object... params) {
        LOGGER.trace("getCreatedBean : " + bean);
        doCreatedPost(bean);
        return bean;
    }

    @Override
    public void doBeforeDestruct(Object bean) {
    }

    @Override
    public void destruct(Object bean) {
        if (bean instanceof AutoCloseable) {
            try {
                ((AutoCloseable) bean).close();
            } catch (Exception e) {
                LOGGER.error("bean(" + bean.getClass() + ") destruct error. ", e);
            }
        }
        if (bean instanceof BeanClosure) {
            ((BeanClosure) bean).destruct(applicationContext);
        }
    }

    @Override
    public Logger getLogger() {
        return LOGGER;
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(SimpleBeanLifecycle.class);
}
