package com.truthbean.debbie.internal;

import com.truthbean.Logger;
import com.truthbean.LoggerFactory;
import com.truthbean.debbie.bean.BeanInfo;
import com.truthbean.debbie.bean.DebbieClassBeanInfo;
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

    @SuppressWarnings("unchecked")
    public <T, K extends T> T proxyCreatedBean(BeanInfo<K> beanInfo, BeanProxyType proxyType) {
        if (proxyType == BeanProxyType.NO) {
            return beanInfo.getBean();
        }
        if (beanInfo instanceof DebbieClassBeanInfo) {
            DebbieClassBeanInfo<K> classBeanInfo = (DebbieClassBeanInfo<K>) beanInfo;
            if (!classBeanInfo.hasAnnotatedMethod() && !classBeanInfo.containClassAnnotation(MethodProxy.class)) {
                return beanInfo.getBean();
            }
            Class<K> clazz = beanInfo.getBeanClass();
            Class<T> beanInterface = classBeanInfo.getBeanInterface();
            if (proxyType == BeanProxyType.JDK && beanInterface != null) {
                LOGGER.trace(() -> "resolve field dependent bean(" + beanInterface + ") by implement class " + clazz);
                JdkDynamicProxy<T, K> dynamicProxy = new JdkDynamicProxy<>();
                if (beanInfo.isEmpty()) {
                    LOGGER.error("bean(" + beanInterface + ") has no value!!");
                    return null;
                }
                T value = dynamicProxy.invokeJdkProxy(this.applicationContext, beanInterface, beanInfo.getBean());
                classBeanInfo.setBean((K) value);
                return value;
            }
            if (proxyType == BeanProxyType.ASM && !classBeanInfo.isMethodParameterContainPrimitiveClass()) {
                MethodProxyHandlerHandler handler = new MethodProxyHandlerHandler(LOGGER);
                MethodProxyHandlerProcessor<K> processor =
                        new MethodProxyHandlerProcessor<>(this.applicationContext, handler, classBeanInfo).process();
                if (processor.hasNoProxy()) return beanInfo.getBean();

                try {
                    AbstractProxy<K> proxy = new AsmProxy<>(classBeanInfo, this.applicationContext.getClassLoader(), handler,
                            MethodProxy.class);
                /*if (!beanInfo.isMethodParameterMoreThanOne()) {
                    proxy = new AsmProxy<>(beanInfo, this.applicationContext.getClassLoader(), handler,
                            MethodProxy.class);
                } else {
                    proxy = new JavassistProxy<>(beanInfo, this.applicationContext.getClassLoader(),
                            handler, MethodProxy.class);
                }*/
                    K value = processor.proxy(proxy);
                    classBeanInfo.setBean(value);
                    return value;
                } catch (Exception e) {
                    LOGGER.error("", e);
                    return beanInfo.getBean();
                }
            } else {
                return beanInfo.getBean();
            }
        } else {
            return beanInfo.getBean();
        }
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(BeanProxyHandler.class);
}
