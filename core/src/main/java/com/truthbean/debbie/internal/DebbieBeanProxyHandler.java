package com.truthbean.debbie.internal;

import com.truthbean.Logger;
import com.truthbean.LoggerFactory;
import com.truthbean.debbie.bean.BeanInfo;
import com.truthbean.debbie.bean.ClassBeanInfo;
import com.truthbean.debbie.core.ApplicationContext;
import com.truthbean.debbie.proxy.*;
import com.truthbean.debbie.proxy.asm.AbstractProxy;
import com.truthbean.debbie.proxy.asm.AsmProxy;
import com.truthbean.debbie.proxy.jdk.JdkDynamicProxy;

/**
 * @author TruthBean
 * @since 0.5.3
 * Created on 2021/11/14 22:16.
 */
class DebbieBeanProxyHandler implements BeanProxyHandler {
    private final ApplicationContext applicationContext;

    public DebbieBeanProxyHandler(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T, K extends T> T proxyCreatedBean(BeanInfo<K> beanInfo, K bean, BeanProxyType proxyType) {
        if (proxyType == BeanProxyType.NO) {
            return bean;
        }
        if (beanInfo instanceof ClassBeanInfo<K> classBeanInfo) {
            if (!classBeanInfo.hasAnnotatedMethod() && !classBeanInfo.containClassAnnotation(MethodProxy.class)) {
                return bean;
            }
            Class<?> clazz = classBeanInfo.getBeanClass();
            Class<T> beanInterface = classBeanInfo.getInterface(applicationContext.getBeanInfoManager().getIgnoreInterface());
            if (proxyType == BeanProxyType.JDK && beanInterface != null) {
                LOGGER.trace(() -> "resolve field dependent bean(" + beanInterface + ") by implement class " + clazz);
                JdkDynamicProxy<T, K> dynamicProxy = new JdkDynamicProxy<>();
                if (!classBeanInfo.isCreated()) {
                    LOGGER.error("bean(" + beanInterface + ") has no VALUE!!");
                    return null;
                }
                T value = dynamicProxy.invokeJdkProxy(this.applicationContext, beanInterface, bean);
                if (beanInfo.isSingleton()) {
                    classBeanInfo.setBean((K) value);
                }
                return value;
            }
            if (proxyType == BeanProxyType.ASM && !classBeanInfo.isMethodParameterContainPrimitiveClass()) {
                MethodProxyHandlerHandler handler = new MethodProxyHandlerHandler(LOGGER);
                MethodProxyHandlerProcessor<K> processor =
                        new MethodProxyHandlerProcessor<>(this.applicationContext, handler, classBeanInfo).process();
                if (processor.hasNoProxy()) {
                    return bean;
                }

                try {
                    AbstractProxy<K> proxy = new AsmProxy<>(classBeanInfo, this.applicationContext.getClassLoader(), handler,
                            MethodProxy.class);
                    /*if (!classBeanInfo.isMethodParameterMoreThanOne()) {
                        proxy = new AsmProxy<>(classBeanInfo, this.applicationContext.getClassLoader(), handler,
                                MethodProxy.class);
                    } else {
                        proxy = new JavassistProxy<>(classBeanInfo, this.applicationContext.getClassLoader(),
                                handler, MethodProxy.class);
                    }*/
                    K value = processor.proxy(proxy);
                    if (beanInfo.isSingleton()) {
                        classBeanInfo.setBean(value);
                    }
                    return value;
                } catch (Exception e) {
                    LOGGER.error("", e);
                    return bean;
                }
            } else {
                return bean;
            }
        } else {
            return bean;
        }
    }

    @Override
    public <T, K extends T> T proxyCreatedBean(K bean, Class<T> clazz, BeanProxyType proxyType) {
        if (proxyType == BeanProxyType.NO) {
            return bean;
        }
        if (proxyType == BeanProxyType.JDK && clazz != null && clazz.isInterface()) {
            LOGGER.trace(() -> "resolve field dependent bean(" + clazz + ") by implement class " + clazz);
            JdkDynamicProxy<T, K> dynamicProxy = new JdkDynamicProxy<>();
            return dynamicProxy.invokeJdkProxy(this.applicationContext, clazz, bean);
        }
        return bean;
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(BeanProxyHandler.class);
}
