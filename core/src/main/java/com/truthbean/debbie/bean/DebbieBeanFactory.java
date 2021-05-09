/**
 * Copyright (c) 2021 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *         http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.bean;

import java.util.Objects;

/**
 * @author TruthBean
 * @since 0.0.2
 * Created on 2019/06/02 16:54.
 */
public class DebbieBeanFactory<Bean> implements BeanFactory<Bean> {

    private BeanInfo<Bean> beanInfo;
    private GlobalBeanFactory globalBeanFactory;

    public DebbieBeanFactory() {
    }

    public DebbieBeanFactory(BeanInfo<Bean> beanInfo) {
        this.beanInfo = beanInfo;
    }

    public void setBeanInfo(BeanInfo<Bean> beanInfo) {
        this.beanInfo = beanInfo;
    }

    @Override
    public void setGlobalBeanFactory(GlobalBeanFactory globalBeanFactory) {
        this.globalBeanFactory = globalBeanFactory;
    }

    private boolean canNew() {
        var beanType = beanInfo.getBeanType();
        return beanType == BeanType.NO_LIMIT || (beanType == BeanType.SINGLETON && beanInfo.isEmpty());
    }

    @Override
    public Bean getBean() {
        if (beanInfo.isSingleton() && beanInfo.isPresent()) {
            return beanInfo.getBean();
        }
        if (canNew() && ClassDetailedMutableBeanInfo.class.isAssignableFrom(beanInfo.getClass())) {
            ClassDetailedMutableBeanInfo<Bean> multableBeanInfo = (ClassDetailedMutableBeanInfo<Bean>) beanInfo;
            Bean bean = globalBeanFactory.factoryBeanByDependenceProcessor(multableBeanInfo, true);
            multableBeanInfo.setBean(bean);
            return bean;
        }
        return beanInfo.getBean();
    }

    @Override
    public Class<Bean> getBeanType() {
        return beanInfo.getBeanClass();
    }

    @Override
    public boolean isSingleton() {
        return beanInfo.getBeanType() == BeanType.SINGLETON;
    }

    @Override
    public void destroy() {
        // do nothing
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DebbieBeanFactory<?> that = (DebbieBeanFactory<?>) o;
        return Objects.equals(beanInfo, that.beanInfo) &&
                Objects.equals(globalBeanFactory, that.globalBeanFactory);
    }

    @Override
    public int hashCode() {
        return Objects.hash(beanInfo, globalBeanFactory);
    }
}
