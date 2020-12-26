/**
 * Copyright (c) 2020 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 * http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.internal;

import com.truthbean.Logger;
import com.truthbean.debbie.bean.*;
import com.truthbean.debbie.properties.DebbieConfiguration;
import com.truthbean.debbie.properties.DebbieConfigurationCenter;
import com.truthbean.debbie.proxy.BeanProxyType;
import com.truthbean.debbie.proxy.MethodProxy;
import com.truthbean.debbie.proxy.MethodProxyHandlerHandler;
import com.truthbean.debbie.proxy.MethodProxyHandlerProcessor;
import com.truthbean.debbie.proxy.asm.AbstractProxy;
import com.truthbean.debbie.proxy.asm.AsmProxy;
import com.truthbean.debbie.proxy.jdk.JdkDynamicProxy;
import com.truthbean.debbie.reflection.ReflectionHelper;
import com.truthbean.logger.LoggerFactory;

import java.lang.reflect.Modifier;
import java.security.AccessControlContext;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Supplier;

/**
 * @author TruthBean/Rogar·Q
 * @since 0.1.0
 */
class DebbieGlobalBeanFactory implements GlobalBeanFactory {

    private final DebbieBeanInfoFactory beanInfoFactory;
    private DebbieApplicationContext applicationContext;
    private DebbieInjectedBeanFactory injectedBeanFactory;

    DebbieGlobalBeanFactory(final DebbieBeanInfoFactory beanInfoFactory) {
        this.beanInfoFactory = beanInfoFactory;
        this.beanInfoFactory.refreshBeans();
    }

    protected void setDebbieApplicationContext(DebbieApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    protected void setInjectedBeanFactory(DebbieInjectedBeanFactory injectedBeanFactory) {
        this.injectedBeanFactory = injectedBeanFactory;
    }


    @Override
    public synchronized <T> T factory(String serviceName) {
        LOGGER.trace(() -> "factory bean with name " + serviceName);
        return factory(serviceName, null, true);
    }

    @Override
    public <T> T factory(Class<T> type) {
        LOGGER.trace(() -> "factory bean with type " + type.getName());
        return factory(null, type, true);
    }

    @Override
    public <T> T factoryIfPresent(Class<T> type) {
        LOGGER.trace(() -> "factory bean with type " + type.getName() + " if present");
        return factory(null, type, false, false);
    }

    @Override
    public <T> Optional<T> factoryIfPresent(String beanName) {
        LOGGER.trace(() -> "factory bean with name " + beanName + " if present");
        return Optional.ofNullable(factory(beanName, null, false, false));
    }

    @Override
    public <T> Supplier<T> supply(String beanName) {
        return () -> factory(beanName, null, true, false);
    }

    @Override
    public <T> Supplier<T> supply(Class<T> type) {
        return () -> factory(null, type, true, false);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> void factoryByRawBean(T rawBean) {
        synchronized (beanInfoFactory) {
            DebbieClassBeanInfo<T> debbieBeanInfo;

            var beanInfo = this.beanInfoFactory.getBeanInfo(null, rawBean.getClass(), false, false);
            if (beanInfo == null)
                debbieBeanInfo = new DebbieClassBeanInfo<>((Class<T>) rawBean.getClass());
            else {
                debbieBeanInfo = (DebbieClassBeanInfo<T>) beanInfo;
            }

            BeanCreatorImpl<T> beanCreator = new BeanCreatorImpl<>(debbieBeanInfo, beanInfoFactory);
            beanCreator.setCreatedPreparation(rawBean);
            beanCreator.setInjectedBeanFactory(injectedBeanFactory);
            T bean = injectedBeanFactory.factory(beanCreator);
            debbieBeanInfo.setBean(bean);
            this.factoryAfterCreatedByProxy(debbieBeanInfo, BeanProxyType.ASM);
        }
    }

    @Override
    public <T> T factoryByNoBean(Class<T> noBeanType) {
        synchronized (beanInfoFactory) {
            BeanInfo<T> debbieBeanInfo;

            var beanInfo = this.beanInfoFactory.getBeanInfo(null, noBeanType, false, false);
            debbieBeanInfo = Objects.requireNonNullElseGet(beanInfo, () -> new DebbieBeanInfo<>(noBeanType));

            BeanCreatorImpl<T> beanCreator = new BeanCreatorImpl<>(debbieBeanInfo, beanInfoFactory);
            beanCreator.setCreatedPreparation(ReflectionHelper.newInstance(noBeanType));
            beanCreator.setInjectedBeanFactory(injectedBeanFactory);
            T bean = injectedBeanFactory.factory(beanCreator);
            if (bean instanceof MutableBeanInfo) {
                ((MutableBeanInfo<T>) debbieBeanInfo).setBean(bean);
            }
            return this.factoryAfterCreatedByProxy(debbieBeanInfo, BeanProxyType.ASM);
        }
    }

    @Override
    public <T, K extends T> T factory(BeanInfo<K> beanInfo) {
        if (beanInfo.isSingleton() && beanInfo.isPresent()) {
            return factoryAfterCreatedByProxy(beanInfo, BeanProxyType.NO);
        }
        var beanFactory = beanInfo.getBeanFactory();
        if (beanFactory != null) {
            beanFactory.setGlobalBeanFactory(this);
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

        return factoryBeanByDependenceProcessor(beanInfo, false);
    }

    private <T, K extends T> T factoryWithoutProxy(BeanInfo<K> beanInfo) {
        if (beanInfo.isSingleton() && beanInfo.isPresent()) {
            return beanInfo.getBean();
        }
        var beanFactory = beanInfo.getBeanFactory();
        if (beanFactory != null) {
            beanFactory.setGlobalBeanFactory(this);
            K bean = beanFactory.factoryBean();
            if (beanInfo instanceof MutableBeanInfo) {
                ((MutableBeanInfo<K>) beanInfo).setBean(bean);
            }
            return bean;
        }

        K bean = injectedBeanFactory.factory(beanInfo, true);
        if (beanInfo instanceof MutableBeanInfo) {
            ((MutableBeanInfo<K>) beanInfo).setBean(bean);
        }
        return bean;
    }

    @SuppressWarnings("unchecked")
    protected <T> T factory(String serviceName, Class<T> type, boolean require, boolean throwException) {
        if (type != null && DebbieConfiguration.class.isAssignableFrom(type)) {
            DebbieConfiguration configuration = DebbieConfigurationCenter.getConfiguration((Class<? extends DebbieConfiguration>) type);
            if (configuration != null) {
                return (T) configuration;
            }
        }
        synchronized (beanInfoFactory) {
            var beanInfo = this.beanInfoFactory.getBeanInfo(serviceName, type, require, throwException);
            if (!require && beanInfo == null)
                return null;
            assert beanInfo != null;
            return factory(beanInfo);
        }
    }

    protected <T> T factory(String serviceName, Class<T> type, boolean require) {
        return factory(serviceName, type, require, true);
    }

    private <T> T factoryBeanByFactory(BeanInfo<T> beanInfo, BeanFactory<T> beanFactory) {
        T bean = beanFactory.factoryBean();
        if (beanInfo instanceof MutableBeanInfo) {
            ((MutableBeanInfo<T>) beanInfo).setBean(bean);
        }
        return this.factoryAfterCreatedByProxy(beanInfo, BeanProxyType.ASM);
    }

    @Override
    public <T> T factoryBeanByDependenceProcessor(BeanInfo<T> beanInfo, boolean skipFactory) {
        T bean = injectedBeanFactory.factory(beanInfo, skipFactory);
        if (beanInfo instanceof MutableBeanInfo) {
            ((MutableBeanInfo<T>) beanInfo).setBean(bean);
        }
        return this.factoryAfterCreatedByProxy(beanInfo, BeanProxyType.ASM);
    }

    @Override
    public <T> T factoryBeanByDependenceProcessor(BeanInfo<T> beanInfo, boolean skipFactory, Object firstParamValue) {
        T bean = injectedBeanFactory.factory(beanInfo, skipFactory, firstParamValue);
        if (beanInfo instanceof MutableBeanInfo) {
            ((MutableBeanInfo<T>) beanInfo).setBean(bean);
        }
        return this.factoryAfterCreatedByProxy(beanInfo, BeanProxyType.ASM);
    }

    @SuppressWarnings("unchecked")
    <T, K extends T> T factoryAfterCreatedByProxy(BeanInfo<K> beanInfo, BeanProxyType proxyType) {
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
            if (beanInterface != null) {
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
            if (!classBeanInfo.isMethodParameterContainPrimitiveClass()) {
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

    @Override
    public <T> DebbieBeanInfo<T> getBeanInfoWithBean(Class<T> type) {
        synchronized (beanInfoFactory) {
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
    }

    @Override
    public <T, K extends T> List<K> getBeanList(Class<T> superType) {
        return getBeanList(superType, false);
    }

    @Override
    public <T, K extends T> List<K> getBeanList(Class<T> superType, boolean withoutProxy) {
        synchronized (beanInfoFactory) {
            List<K> result = new ArrayList<>();
            LOGGER.trace(() -> "factory bean with type " + superType.getName());
            List<BeanInfo<K>> beanInfoList = this.beanInfoFactory.getBeanInfoList(superType, false);
            if (beanInfoList != null && !beanInfoList.isEmpty()) {
                for (BeanInfo<K> beanInfo : beanInfoList) {
                    K bean;
                    if (withoutProxy) {
                        bean = this.factoryWithoutProxy(beanInfo);
                    } else {
                        bean = this.factory(beanInfo);
                    }
                    if (bean == null) {
                        throw new BeanCreatedException("create bean (" + beanInfo.toString() + ") error");
                    }
                    result.add(bean);
                }
            }
            return result;
        }
    }

    @Override
    public <T> boolean containsBean(Class<T> beanType) {
        synchronized (beanInfoFactory) {
            return this.beanInfoFactory.getBeanInfoList(beanType, false) != null;
        }
    }

    @Override
    public boolean containsBean(String beanName) {
        synchronized (beanInfoFactory) {
            return this.beanInfoFactory.getBeanInfo(beanName, null, false, false) != null;
        }
    }

    @Override
    public <T> T getBeanByFactory(BeanInfo<T> beanInfo) {
        Class<T> beanClass = beanInfo.getBeanClass();
        BeanFactory<T> beanFactory = beanInfo.getBeanFactory();
        return getBeanByFactory(beanClass, beanFactory);
    }

    @Override
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
