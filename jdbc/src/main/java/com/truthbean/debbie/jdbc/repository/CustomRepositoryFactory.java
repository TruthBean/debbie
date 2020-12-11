/**
 * Copyright (c) 2020 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *         http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.jdbc.repository;

import com.truthbean.debbie.bean.*;
import com.truthbean.debbie.jdbc.datasource.DataSourceConfiguration;

/**
 * @author truthbean
 * @since 0.1.0
 * Created on 2020/7/11 17:01.
 */
public class CustomRepositoryFactory<R extends CustomRepository<E, I>, E, I> implements BeanFactory<R> {

    private final Object reference = new Object();

    private GlobalBeanFactory globalBeanFactory;
    private DataSourceConfiguration configuration;

    private final BeanInfo<R> beanInfo;
    private volatile R bean;

    public CustomRepositoryFactory(BeanInfo<R> beanInfo) {
        this.beanInfo = beanInfo;
    }

    @Override
    public R getBean() {
        if (bean == null) {
            synchronized (reference) {
                if (bean == null) {
                    if (configuration == null)
                        configuration = globalBeanFactory.factory(DataSourceConfiguration.class);
                    DmlRepositoryHandler<E, I> repositoryHandler = new DmlRepositoryHandler<>(configuration, getBeanType());
                    R localBean = globalBeanFactory.factoryBeanByDependenceProcessor(beanInfo, true);
                    localBean.setRepositoryHandler(repositoryHandler);
                    bean = localBean;
                }
            }
        }
        return bean;
    }

    @Override
    public Class<R> getBeanType() {
        return beanInfo.getBeanClass();
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

    @Override
    public void destroy() {
        this.bean = null;
    }

    @Override
    public void setGlobalBeanFactory(GlobalBeanFactory globalBeanFactory) {
        this.globalBeanFactory = globalBeanFactory;
    }

}
