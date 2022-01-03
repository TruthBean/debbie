/**
 * Copyright (c) 2021 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 * http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 *//*

package com.truthbean.debbie.internal;

import com.truthbean.Logger;
import com.truthbean.LoggerFactory;
import com.truthbean.debbie.bean.*;
import com.truthbean.debbie.properties.DebbieConfiguration;
import com.truthbean.debbie.properties.DebbieConfigurationCenter;
import com.truthbean.debbie.proxy.*;
import com.truthbean.debbie.reflection.ReflectionHelper;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Supplier;

*/
/**
 * @author TruthBean/Rogar·Q
 * @since 0.1.0
 *//*

class DebbieGlobalBeanFactory {

    private final BeanInfoManager beanInfoManager;
    private DebbieApplicationContext applicationContext;
    private DebbieInjectedBeanFactory injectedBeanFactory;
    private BeanProxyHandler beanProxyHandler;

    DebbieGlobalBeanFactory(final BeanInfoManager beanInfoManager) {
        beanInfoManager.refreshBeans();
        this.beanInfoManager = beanInfoManager;
    }

    protected void setDebbieApplicationContext(DebbieApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    protected void setInjectedBeanFactory(DebbieInjectedBeanFactory injectedBeanFactory) {
        this.injectedBeanFactory = injectedBeanFactory;
    }
    
    protected void setBeanProxyHandler(BeanProxyHandler beanProxyHandler) {
        this.beanProxyHandler = beanProxyHandler;
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
        synchronized (beanInfoManager) {
            BeanInfo<T> debbieBeanInfo;

            BeanInfo beanInfo = null;
            // var beanInfo = this.beanInfoManager.getBeanInfo(null, rawBean.getClass(), false, false);
            if (beanInfo == null) {
                debbieBeanInfo = new DebbieReflectionBeanFactory<>((Class<T>) rawBean.getClass(), rawBean);
            } else {
                debbieBeanInfo = (BeanInfo<T>) beanInfo;
            }

            if (debbieBeanInfo instanceof DebbieClassFactoryBeanInfo classBeanInfo) {
                BeanCreatorImpl<T> beanCreator = new BeanCreatorImpl<T>(classBeanInfo, beanInfoManager, beanProxyHandler);
                beanCreator.setApplicationContext(applicationContext);
                beanCreator.setCreatedPreparation(rawBean);
                beanCreator.setInjectedBeanFactory(injectedBeanFactory);
                T bean = injectedBeanFactory.factory(beanCreator);
                classBeanInfo.setBean(bean);
                if (beanInfo != null) {
                    beanProxyHandler.proxyCreatedBean(classBeanInfo, classBeanInfo.getBean(), beanInfo.getBeanProxyType());
                } else {
                    beanProxyHandler.proxyCreatedBean(classBeanInfo, classBeanInfo.getBean(), BeanProxyType.ASM);
                }
            } else if (debbieBeanInfo instanceof BeanFactory) {
                ((BeanFactory<?>) debbieBeanInfo).factoryBean(applicationContext);
            }
        }
    }

    @Override
    public <T> T factoryByNoBean(Class<T> noBeanType) {
        synchronized (beanInfoManager) {
            BeanInfo debbieBeanInfo;

            var beanInfo = this.beanInfoManager.getBeanInfo(null, noBeanType, false, false);
            debbieBeanInfo = Objects.requireNonNullElseGet(beanInfo, () -> new DebbieReflectionBeanFactory<>(noBeanType));

            if (debbieBeanInfo instanceof DebbieClassFactoryBeanInfo) {
                DebbieClassFactoryBeanInfo<T> classBeanInfo = (DebbieClassFactoryBeanInfo<T>) debbieBeanInfo;
                BeanCreatorImpl<T> beanCreator = new BeanCreatorImpl<>(classBeanInfo, beanInfoManager, beanProxyHandler);
                beanCreator.setApplicationContext(applicationContext);
                beanCreator.setCreatedPreparation(ReflectionHelper.newInstance(noBeanType));
                beanCreator.setInjectedBeanFactory(injectedBeanFactory);
                T bean = injectedBeanFactory.factory(beanCreator);
                if (bean instanceof MutableFactoryBeanInfo) {
                    ((MutableFactoryBeanInfo<T>) debbieBeanInfo).setBean(bean);
                }
                return beanProxyHandler.proxyCreatedBean(classBeanInfo, classBeanInfo.getBean(), beanInfo.getBeanProxyType());
            } else if (debbieBeanInfo instanceof BeanFactory) {
                return (T) ((BeanFactory<?>) debbieBeanInfo).factoryBean(applicationContext);
            }
            return null;
        }
    }

    @Override
    public <T, K extends T> K factory(BeanInfo<T> baseBeanInfo) {
        if (baseBeanInfo instanceof FactoryBeanInfo) {
            FactoryBeanInfo<K> beanInfo = (FactoryBeanInfo<K>) baseBeanInfo;
            if (beanInfo.isSingleton() && beanInfo.isPresent() && beanInfo.needProxy()) {
                return beanProxyHandler.proxyCreatedBean(beanInfo, beanInfo.getBean(), beanInfo.getBeanProxyType());
            }
            var beanFactory = beanInfo.getBeanFactory();
            if (beanFactory != null) {
                return factoryBeanByFactory(beanInfo, beanFactory);
            }

            return factoryBeanByDependenceProcessor(beanInfo, false);
        } else if (baseBeanInfo instanceof BeanFactory) {
            return ((BeanFactory<K>) baseBeanInfo).factoryBean(applicationContext);
        }
        return null;
    }

    private <T, K extends T> T factoryWithoutProxy(FactoryBeanInfo<K> beanInfo) {
        if (beanInfo.isSingleton() && beanInfo.isPresent()) {
            return beanInfo.getBean();
        }
        var beanFactory = beanInfo.getBeanFactory();
        if (beanFactory != null && !(beanFactory instanceof SkipCreatedBeanFactory)) {
            K bean = beanFactory.factoryBean(applicationContext);
            if (beanInfo instanceof MutableFactoryBeanInfo) {
                ((MutableFactoryBeanInfo<K>) beanInfo).setBean(bean);
            }
            return bean;
        }

        K bean = injectedBeanFactory.factory(beanInfo, true);
        if (beanInfo instanceof MutableFactoryBeanInfo) {
            ((MutableFactoryBeanInfo<K>) beanInfo).setBean(bean);
        }
        return bean;
    }

    @SuppressWarnings("unchecked")
    protected <T> T factory(String serviceName, Class<T> type, boolean require, boolean throwException) {
        if (serviceName != null && type != null && DebbieConfiguration.class.isAssignableFrom(type)) {
            DebbieConfiguration configuration = DebbieConfigurationCenter.getConfiguration((Class<? extends DebbieConfiguration>) type, serviceName);
            if (configuration != null) {
                return (T) configuration;
            }
        } else if (type != null && DebbieConfiguration.class.isAssignableFrom(type)) {
            DebbieConfiguration configuration = DebbieConfigurationCenter.getConfiguration((Class<? extends DebbieConfiguration>) type);
            if (configuration != null) {
                return (T) configuration;
            }
        }
        synchronized (beanInfoManager) {
            var beanFactory = this.beanInfoManager.getBeanFactory(serviceName, type, require);
            if (!require && beanFactory == null) {
                return null;
            }
            assert beanFactory != null;
            return beanFactory.factoryBean(applicationContext);
        }
    }

    protected <T> T factory(String serviceName, Class<T> type, boolean require) {
        return factory(serviceName, type, require, true);
    }

    private <T> T factoryBeanByFactory(FactoryBeanInfo<T> beanInfo, BeanFactory<T> beanFactory) {
        if (beanFactory instanceof SkipCreatedBeanFactory && beanInfo.needProxy()) {
            return beanProxyHandler.proxyCreatedBean(beanInfo, beanInfo.getBean(), beanInfo.getBeanProxyType());
        }
        T bean = beanFactory.factoryBean(applicationContext);
        if (beanInfo instanceof MutableFactoryBeanInfo) {
            ((MutableFactoryBeanInfo<T>) beanInfo).setBean(bean);
        }
        if (beanInfo.needProxy()) {
            return beanProxyHandler.proxyCreatedBean(beanInfo, beanInfo.getBean(), beanInfo.getBeanProxyType());
        } else {
            return beanInfo.getBean();
        }
    }

    @Override
    public <T> T factoryBeanByDependenceProcessor(FactoryBeanInfo<T> beanInfo, boolean skipFactory) {
        T bean = injectedBeanFactory.factory(beanInfo, skipFactory);
        if (beanInfo instanceof MutableFactoryBeanInfo) {
            ((MutableFactoryBeanInfo<T>) beanInfo).setBean(bean);
        }
        if (beanInfo.needProxy()) {
            return beanProxyHandler.proxyCreatedBean(beanInfo, beanInfo.getBean(), beanInfo.getBeanProxyType());
        } else {
            return beanInfo.getBean();
        }
    }

    @Override
    public <T> T factoryBeanByDependenceProcessor(FactoryBeanInfo<T> beanInfo, boolean skipFactory, Object firstParamValue) {
        T bean = injectedBeanFactory.factory(beanInfo, skipFactory, firstParamValue);
        if (beanInfo instanceof MutableFactoryBeanInfo) {
            ((MutableFactoryBeanInfo<T>) beanInfo).setBean(bean);
        }
        if (beanInfo.needProxy()) {
            return beanProxyHandler.proxyCreatedBean(beanInfo, beanInfo.getBean(), beanInfo.getBeanProxyType());
        } else {
            return beanInfo.getBean();
        }
    }

    @Override
    public <Bean> List<Bean> getBeanList(Class<Bean> superType) {
        return getBeanList(superType, false);
    }

    @Override
    public <Bean> List<Bean> getBeanList(Class<Bean> superType, boolean withoutProxy) {
        synchronized (beanInfoManager) {
            List<Bean> result = new ArrayList<>();
            LOGGER.trace(() -> "factory bean with type " + superType.getName());
            List<BeanInfo> beanInfoList = this.beanInfoManager.getBeanInfoList(superType, false);
            if (beanInfoList != null && !beanInfoList.isEmpty()) {
                for (BeanInfo beanInfo : beanInfoList) {
                    Bean bean = null;
                    if (beanInfo instanceof FactoryBeanInfo) {
                        FactoryBeanInfo<Bean> kBeanInfo = (FactoryBeanInfo<Bean>) beanInfo;
                        if (withoutProxy) {
                            bean = this.factoryWithoutProxy(kBeanInfo);
                        } else {
                            bean = (Bean) this.factory(kBeanInfo);
                        }
                    } else if (beanInfo instanceof BeanFactory) {
                        bean = (Bean) ((BeanFactory<?>) beanInfo).factoryBean(applicationContext);
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
        synchronized (beanInfoManager) {
            return this.beanInfoManager.getBeanInfoList(beanType, false) != null;
        }
    }

    @Override
    public boolean containsBean(String beanName) {
        synchronized (beanInfoManager) {
            return this.beanInfoManager.getBeanInfo(beanName, null, false, false) != null;
        }
    }

    @Override
    public <T> T getBeanByFactory(FactoryBeanInfo<T> beanInfo) {
        Class<T> beanClass = beanInfo.getBeanClass();
        BeanFactory<T> beanFactory = beanInfo.getBeanFactory();
        return getBeanByFactory(beanClass, beanFactory);
    }

    @Override
    public <T> T getBeanByFactory(Class<T> beanClass, BeanFactory<T> beanFactory) {
        if (beanClass.isInterface() || Modifier.isAbstract(beanClass.getModifiers())) {
            if (beanFactory != null) {
                return beanFactory.factoryBean(applicationContext);
            } else {
                return factory(beanClass);
            }
        }
        return null;
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(GlobalBeanFactory.class);
}
*/
