package com.truthbean.debbie.proxy;

import com.truthbean.Logger;
import com.truthbean.LoggerFactory;
import com.truthbean.debbie.bean.BeanInfo;
import com.truthbean.debbie.bean.ClassBeanInfo;
import com.truthbean.debbie.core.ApplicationContext;
import com.truthbean.debbie.proxy.jdk.JdkDynamicProxy;

/**
 * @author TruthBean
 * @since 0.5.3
 * Created on 2021/11/14 22:16.
 */
public class JdkBeanProxyHandler implements BeanProxyHandler {
    private final ApplicationContext applicationContext;

    public JdkBeanProxyHandler(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @Override
    public <T, K extends T> T proxyCreatedBean(BeanInfo<K> beanInfo, K bean, Class<T> clazz, BeanProxyType proxyType) {
        if (proxyType == BeanProxyType.NO) {
            return bean;
        }
        if (proxyType == BeanProxyType.JDK) {
            if (clazz != null && clazz.isInterface()) {
                LOGGER.trace(() -> "resolve field dependent bean(" + clazz + ") by implement class " + clazz);
                JdkDynamicProxy<T, K> dynamicProxy = new JdkDynamicProxy<>();
                return dynamicProxy.invokeJdkProxy(this.applicationContext, clazz, bean);
            } else if (beanInfo instanceof ClassBeanInfo<K> classBeanInfo) {
                if (!classBeanInfo.hasAnnotatedMethod() && !classBeanInfo.containClassAnnotation(MethodProxy.class)) {
                    return bean;
                }
                Class<?> beanClass = classBeanInfo.getBeanClass();
                Class<T> beanInterface = classBeanInfo.getInterface(applicationContext.getBeanInfoManager().getIgnoreInterface());
                if (beanInterface != null) {
                    LOGGER.trace(() -> "resolve field dependent bean(" + beanInterface + ") by implement class " + beanClass);
                    JdkDynamicProxy<T, K> dynamicProxy = new JdkDynamicProxy<>();
                    return dynamicProxy.invokeJdkProxy(this.applicationContext, beanInterface, bean);
                }
            }
        }
        return bean;
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(BeanProxyHandler.class);
}
