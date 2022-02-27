package com.truthbean.debbie.asm;

import com.truthbean.debbie.bean.BeanFactory;
import com.truthbean.debbie.bean.BeanInfo;
import com.truthbean.debbie.bean.BeanLifecycle;
import com.truthbean.debbie.proxy.BeanProxyHandler;
import com.truthbean.debbie.proxy.BeanProxyType;

import java.lang.reflect.Modifier;

/**
 * @author TruthBean
 * @since 0.5.4
 * Created on 2022/01/10 21:08.
 */
public class AsmBeanLifecycle implements BeanLifecycle {

    private final BeanProxyHandler beanProxyHandler;

    public AsmBeanLifecycle(BeanProxyHandler beanProxyHandler) {
        this.beanProxyHandler = beanProxyHandler;
    }

    @Override
    public boolean support(Class<?> clazz) {
        return !clazz.isInterface() && !clazz.isEnum() && !clazz.isPrimitive() && !clazz.isAnnotation()
                && !clazz.isArray()
                && !Modifier.isAbstract(clazz.getModifiers()) && !Modifier.isPrivate(clazz.getModifiers())
                && !Modifier.isFinal(clazz.getModifiers());
    }

    @Override
    public boolean support(BeanFactory<?> beanFactory) {
        return beanFactory.getBeanProxyType() == BeanProxyType.ASM;
    }

    @Override
    public <T> T construct(T preparedBean, Object... params) {
        return preparedBean;
    }

    @Override
    public <T> T postConstruct(T bean, Object... params) {
        return bean;
    }

    @Override
    public <T, K extends T> T doPreCreated(BeanInfo<K> beanInfo, K bean, Class<T> clazz, BeanProxyType proxyType) {
        if (beanProxyHandler != null) {
            return beanProxyHandler.proxyCreatedBean(beanInfo, bean, clazz, proxyType);
        }
        return bean;
    }

    @Override
    public <Bean> Bean getCreatedBean(Bean bean, Object... params) {
        return bean;
    }
}
