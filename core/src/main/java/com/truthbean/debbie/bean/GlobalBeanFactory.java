/**
 * Copyright (c) 2020 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 * http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.bean;

import com.truthbean.Logger;
import com.truthbean.debbie.proxy.BeanProxyType;
import com.truthbean.debbie.proxy.MethodProxy;
import com.truthbean.debbie.proxy.MethodProxyHandlerHandler;
import com.truthbean.debbie.proxy.MethodProxyHandlerProcessor;
import com.truthbean.debbie.proxy.asm.AbstractProxy;
import com.truthbean.debbie.proxy.asm.AsmProxy;
import com.truthbean.debbie.proxy.javaassist.JavassistProxy;
import com.truthbean.debbie.proxy.jdk.JdkDynamicProxy;
import com.truthbean.logger.LoggerFactory;

import java.lang.reflect.Modifier;
import java.security.AccessControlContext;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.List;

/**
 * @author TruthBean/Rogar·Q
 * @since 0.1.0
 * Created on 2020-07-01 11:32.
 */
public class GlobalBeanFactory {

    private DebbieBeanInfoFactory beanInfoFactory;
    private DebbieApplicationContext applicationContext;
    private InjectedBeanFactory injectedBeanFactory;

    GlobalBeanFactory() {
    }

    void setDebbieApplicationContext(DebbieApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    void setDebbieBeanInfoFactory(DebbieBeanInfoFactory beanInfoFactory) {
        this.beanInfoFactory = beanInfoFactory;
    }

    void setInjectedBeanFactory(InjectedBeanFactory injectedBeanFactory) {
        this.injectedBeanFactory = injectedBeanFactory;
    }


    public synchronized <T> T factory(String serviceName) {
        LOGGER.trace(() -> "factory bean with name " + serviceName);
        return factory(serviceName, null, true);
    }

    public <T> T factory(Class<T> type) {
        LOGGER.trace(() -> "factory bean with type " + type.getName());
        return factory(null, type, true);
    }

    public <T> T factoryIfPresent(Class<T> type) {
        LOGGER.trace(() -> "factory bean with type " + type.getName() + " if present");
        return factory(null, type, false, false);
    }

    @SuppressWarnings("unchecked")
    public <T> void factoryByRawBean(T rawBean) {
        DebbieBeanInfo<T> debbieBeanInfo;

        var beanInfo = this.beanInfoFactory.getBeanInfo(null, rawBean.getClass(), false, false);
        if (beanInfo == null)
            debbieBeanInfo = new DebbieBeanInfo<>((Class<T>) rawBean.getClass());
        else {
            debbieBeanInfo = (DebbieBeanInfo<T>) beanInfo;
        }

        BeanCreatorImpl<T> beanCreator = new BeanCreatorImpl<>(debbieBeanInfo, beanInfoFactory);
        beanCreator.setCreatedPreparation(rawBean);
        beanCreator.setInjectedBeanFactory(injectedBeanFactory);
        T bean = injectedBeanFactory.factory(beanCreator);
        debbieBeanInfo.setBean(bean);
        this.factoryAfterCreatedByProxy(debbieBeanInfo, BeanProxyType.ASM);
    }

    public <T, K extends T> T factory(DebbieBeanInfo<K> beanInfo) {
        if (beanInfo.isSingleton() && beanInfo.isPresent()) {
            return factoryAfterCreatedByProxy(beanInfo, BeanProxyType.NO);
        }
        var beanFactory = beanInfo.getBeanFactory();
        if (beanFactory != null) {
            if (System.getSecurityManager() != null) {
                // TODO to set securityContextProvider
                try {
                    AccessControlContext securityContextProvider = AccessController.getContext();
                    AccessController.doPrivileged((PrivilegedAction<T>) () -> factoryBeanByFactory(beanInfo, beanFactory), securityContextProvider);
                } catch (Exception e) {
                    LOGGER.error("getBean from factory via securityContextProvider error", e);
                }
            } else
                return factoryBeanByFactory(beanInfo, beanFactory);
        }

        return factoryBeanByDependenceProcessor(beanInfo);
    }

    protected <T> T factory(String serviceName, Class<T> type, boolean require, boolean throwException) {
        var beanInfo = this.beanInfoFactory.getBeanInfo(serviceName, type, require, throwException);
        if (!require && beanInfo == null)
            return null;
        assert beanInfo != null;
        return factory(beanInfo);
    }

    protected <T> T factory(String serviceName, Class<T> type, boolean require) {
        return factory(serviceName, type, require, true);
    }

    private <T> T factoryBeanByFactory(DebbieBeanInfo<T> beanInfo, BeanFactory<T> beanFactory) {
        T bean = beanFactory.factoryBean();
        beanInfo.setBean(bean);
        return this.factoryAfterCreatedByProxy(beanInfo, BeanProxyType.ASM);
    }

    public <T> T factoryBeanByDependenceProcessor(DebbieBeanInfo<T> beanInfo) {
        T bean = injectedBeanFactory.factory(beanInfo);
        beanInfo.setBean(bean);
        return this.factoryAfterCreatedByProxy(beanInfo, BeanProxyType.ASM);
    }

    <T, K extends T> T factoryAfterCreatedByProxy(DebbieBeanInfo<K> beanInfo, BeanProxyType proxyType) {
        if (!beanInfo.hasAnnotatedMethod()) {
            return beanInfo.getBean();
        }
        Class<K> clazz = beanInfo.getBeanClass();
        Class<T> beanInterface = beanInfo.getBeanInterface();
        if (beanInterface != null) {
            LOGGER.trace(() -> "resolve field dependent bean(" + beanInterface + ") by implement class " + clazz);
            JdkDynamicProxy<T, K> dynamicProxy = new JdkDynamicProxy<>();
            if (beanInfo.isEmpty()) {
                LOGGER.error("bean(" + beanInterface + ") has no value!!");
                return null;
            }
            return dynamicProxy.invokeJdkProxy(this.applicationContext, beanInterface, beanInfo.getBean());
        }
        if (proxyType == BeanProxyType.NO) {
            return beanInfo.getBean();
        }
        if (!beanInfo.isMethodParameterContainPrimitiveClass()) {
            MethodProxyHandlerHandler handler = new MethodProxyHandlerHandler(LOGGER);
            MethodProxyHandlerProcessor<K> processor =
                    new MethodProxyHandlerProcessor<>(this.applicationContext, handler, beanInfo).process();
            if (processor.hasNoProxy()) return beanInfo.getBean();

            try {
                AbstractProxy<K> proxy;
                if (!beanInfo.isMethodParameterMoreThanOne()) {
                    proxy = new AsmProxy<>(beanInfo.getBeanClass(), this.applicationContext.getClassLoader(), handler,
                            MethodProxy.class);
                } else {
                    proxy = new JavassistProxy<>(beanInfo.getBeanClass(), this.applicationContext.getClassLoader(),
                            handler, MethodProxy.class);
                }
                return processor.proxy(proxy);
            } catch (Exception e) {
                LOGGER.error("", e);
                return beanInfo.getBean();
            }
        } else {
            return beanInfo.getBean();
        }
    }

    public <T> DebbieBeanInfo<T> getBeanInfoWithBean(Class<T> type) {
        LOGGER.trace(() -> "factory bean with type " + type.getName());
        var beanInfo = this.beanInfoFactory.getBeanInfo(null, type, true, true);
        if (beanInfo != null) {
            T bean = this.factory(beanInfo);
            DebbieBeanInfo<T> result = new DebbieBeanInfo<>(beanInfo.getBeanClass());
            result.setBean(bean);
            return result;
        }
        return null;
    }

    public <T, K extends T> List<K> getBeanList(Class<T> superType) {
        List<K> result = new ArrayList<>();
        LOGGER.trace(() -> "factory bean with type " + superType.getName());
        List<DebbieBeanInfo<K>> beanInfoList = this.beanInfoFactory.getBeanInfoList(superType, false);
        if (beanInfoList != null) {
            for (DebbieBeanInfo<K> beanInfo : beanInfoList) {
                K bean = this.factory(beanInfo);
                result.add(bean);
            }
        }
        return result;
    }

    public <T> boolean containsBean(Class<T> beanType) {
        return this.beanInfoFactory.getBeanInfoList(beanType, false) != null;
    }

    public boolean containsBean(String beanName) {
        return this.beanInfoFactory.getBeanInfo(beanName, null, false, false) != null;
    }

    public <T> T getBeanByFactory(DebbieBeanInfo<T> beanInfo) {
        Class<T> beanClass = beanInfo.getBeanClass();
        BeanFactory<T> beanFactory = beanInfo.getBeanFactory();
        return getBeanByFactory(beanClass, beanFactory);
    }

    public <T> T getBeanByFactory(Class<T> beanClass, BeanFactory<T> beanFactory) {
        if (beanClass.isInterface() || Modifier.isAbstract(beanClass.getModifiers())) {
            if (beanFactory != null) {
                return beanFactory.getBean();
            } else {
                return factory(beanClass);
            }
        }
        return null;
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(GlobalBeanFactory.class);
}
