/**
 * Copyright (c) 2021 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 * http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.bean;

import com.truthbean.debbie.reflection.ReflectionHelper;

import java.lang.reflect.Method;

/**
 * @author TruthBean/Rogar·Q
 * @since 0.1.0
 * Created on 2020-08-27 14:11
 */
public class ConfigurationMethodBeanFactory<Configuration, Bean> implements BeanFactory<Bean> {

    private final Configuration configuration;
    private final Method method;
    public ConfigurationMethodBeanFactory(Configuration configuration, Method method) {
        this.configuration = configuration;
        this.method = method;
    }

    @Override
    public Bean getBean() {
        // todo params
        return ReflectionHelper.invokeMethod(configuration, method);
    }

    @SuppressWarnings("unchecked")
    @Override
    public Class<Bean> getBeanType() {
        return (Class<Bean>) method.getReturnType();
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

    @Override
    public void destroy() {
    }

    @Override
    public void setGlobalBeanFactory(GlobalBeanFactory globalBeanFactory) {
    }
}
