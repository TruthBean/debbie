/**
 * Copyright (c) 2021 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *         http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 *//*

package com.truthbean.debbie.controversial;

import com.truthbean.Logger;
import com.truthbean.debbie.bean.*;
import com.truthbean.debbie.core.ApplicationContext;
import com.truthbean.debbie.proxy.BeanProxyType;

import java.util.Objects;
import java.util.Set;

*/
/**
 * @author TruthBean
 * @since 0.0.2
 * Created on 2019/06/02 16:54.
 *//*

class DebbieBeanFactory<Bean> implements BeanFactory<Bean> {

    private FactoryBeanInfo<Bean> factoryBeanInfo;
    private GlobalBeanFactory globalBeanFactory;

    public DebbieBeanFactory() {
    }

    public DebbieBeanFactory(FactoryBeanInfo<Bean> factoryBeanInfo) {
        this.factoryBeanInfo = factoryBeanInfo;
    }

    public void setBeanInfo(FactoryBeanInfo<Bean> factoryBeanInfo) {
        this.factoryBeanInfo = factoryBeanInfo;
    }

    public void setGlobalBeanFactory(GlobalBeanFactory globalBeanFactory) {
        this.globalBeanFactory = globalBeanFactory;
    }

    private boolean canNew() {
        var beanType = factoryBeanInfo.getBeanType();
        return beanType == BeanType.NO_LIMIT || (beanType == BeanType.SINGLETON && factoryBeanInfo.isEmpty());
    }

    @Override
    public Bean factoryBean(ApplicationContext applicationContext) {
        globalBeanFactory = applicationContext.getGlobalBeanFactory();
        if (factoryBeanInfo.isSingleton() && factoryBeanInfo.isPresent()) {
            return factoryBeanInfo.getBean();
        }
        */
/*if (canNew() && isSingleton() && ClassDetailedMutableFactoryBeanInfo.class.isAssignableFrom(factoryBeanInfo.getClass())) {
            ClassDetailedMutableFactoryBeanInfo<Bean> multableBeanInfo = (ClassDetailedMutableFactoryBeanInfo<Bean>) factoryBeanInfo;
            Bean bean = globalBeanFactory.factoryBeanByDependenceProcessor(multableBeanInfo, true);
            multableBeanInfo.setBean(bean);
            return bean;
        } else if (canNew()) {
            return globalBeanFactory.factoryBeanByDependenceProcessor(factoryBeanInfo, true);
        }*//*

        return factoryBeanInfo.getBean();
    }

    @Override
    public boolean isCreated() {
        return false;
    }

    @Override
    public Class<?> getBeanClass() {
        return factoryBeanInfo.getBeanClass();
    }

    public boolean isSkipCreatedBeanFactory() {
        return false;
    }

    @Override
    public BeanProxyType getBeanProxyType() {
        return factoryBeanInfo.getBeanProxyType();
    }

    @Override
    public boolean needProxy() {
        return factoryBeanInfo.needProxy();
    }

    @Override
    public Set<BeanCondition> getConditions() {
        return factoryBeanInfo.getConditions();
    }

    @Override
    public boolean isLazyCreate() {
        return factoryBeanInfo.isLazyCreate();
    }

    @Override
    public BeanType getBeanType() {
        return factoryBeanInfo.getBeanType();
    }

    @Override
    public String getServiceName() {
        return factoryBeanInfo.getServiceName();
    }

    @Override
    public boolean containName(String name) {
        return factoryBeanInfo.containName(name);
    }

    @Override
    public Set<String> getBeanNames() {
        return factoryBeanInfo.getBeanNames();
    }

    @Override
    public BeanFactory<Bean> copy() {
        return BeanFactory.super.copy();
    }

    @Override
    public Logger getLogger() {
        return BeanFactory.super.getLogger();
    }

    @Override
    public boolean isSingleton() {
        return factoryBeanInfo.getBeanType() == BeanType.SINGLETON;
    }

    @Override
    public void destruct(ApplicationContext applicationContext) {
        if (factoryBeanInfo instanceof MutableFactoryBeanInfo) {
            ((MutableFactoryBeanInfo<Bean>) factoryBeanInfo).setBean(() -> null);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DebbieBeanFactory<?> that = (DebbieBeanFactory<?>) o;
        return Objects.equals(factoryBeanInfo, that.factoryBeanInfo) &&
                Objects.equals(globalBeanFactory, that.globalBeanFactory);
    }

    @Override
    public int hashCode() {
        return Objects.hash(factoryBeanInfo, globalBeanFactory);
    }
}
*/
