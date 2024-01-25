/**
 * Copyright (c) 2024 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *         http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.jdbc.repository;

import com.truthbean.debbie.bean.BeanFactory;
import com.truthbean.debbie.bean.BeanType;
import com.truthbean.debbie.core.ApplicationContext;
import com.truthbean.debbie.proxy.BeanProxyType;

import java.util.HashSet;
import java.util.Set;

/**
 * @author truthbean/Rogar·Q
 * @since 0.0.2
 * Created on 2020-04-13 15:11.
 */
public class DdlRepositoryFactory implements BeanFactory<DdlRepository> {

    private volatile DdlRepository ddlRepository;
    private final Set<String> names = new HashSet<>();
    public DdlRepositoryFactory(String name) {
        names.add(name);
    }

    @Override
    public DdlRepository factoryBean(ApplicationContext applicationContext) {
        if (ddlRepository == null) {
            ddlRepository = new DdlRepository();
        }
        return ddlRepository;
    }

    /*@Override
    public DdlRepository factoryNamedBean(String name, ApplicationContext applicationContext) {
        if (ddlRepository == null) {
            ddlRepository = new DdlRepository();
        }
        return ddlRepository;
    }

    @Override
    public DdlRepository factory(String name, Class beanClass, BeanType type, BeanProxyType proxyType, ApplicationContext applicationContext) {
        if (ddlRepository == null) {
            ddlRepository = new DdlRepository();
        }
        return ddlRepository;
    }*/

    @Override
    public Class<?> getBeanClass() {
        return DdlRepository.class;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

    @Override
    public BeanType getBeanType() {
        return BeanType.SINGLETON;
    }

    @Override
    public Set<String> getAllName() {
        return names;
    }

    @Override
    public void destruct(ApplicationContext applicationContext) {
        names.clear();
    }

    @Override
    public boolean isCreated() {
        return ddlRepository != null;
    }

    @Override
    public DdlRepository getCreatedBean() {
        return ddlRepository;
    }

    @Override
    public boolean equals(Object o) {
        return isEquals(o);
    }

    @Override
    public int hashCode() {
        return getHashCode(super.hashCode());
    }
}
