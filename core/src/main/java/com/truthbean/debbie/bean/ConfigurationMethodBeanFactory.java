/**
 * Copyright (c) 2023 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 * http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.bean;

import com.truthbean.Logger;
import com.truthbean.LoggerFactory;
import com.truthbean.core.util.StringUtils;
import com.truthbean.debbie.core.ApplicationContext;
import com.truthbean.debbie.proxy.BeanProxyType;
import com.truthbean.debbie.reflection.ReflectionHelper;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Supplier;

/**
 * @author TruthBean/Rogar·Q
 * @since 0.1.0
 * Created on 2020-08-27 14:11
 */
public class ConfigurationMethodBeanFactory<Configuration, Bean> implements BeanFactory<Bean> {

    private final Supplier<Configuration> supplier;
    private final BeanType beanType;
    private final Method method;
    private final Set<String> names = new HashSet<>();
    private final Set<BeanCondition> conditions;

    private volatile Bean bean;

    public ConfigurationMethodBeanFactory(Supplier<Configuration> supplier, Method method) {
        this.supplier = supplier;
        this.method = method;
        this.beanType = BeanType.SINGLETON;
        this.names.add(method.getName());
        this.conditions = new HashSet<>();
    }

    public ConfigurationMethodBeanFactory(Supplier<Configuration> supplier, Method method, BeanType beanType, String name,
                                          Set<BeanCondition> conditions) {
        this.supplier = supplier;
        this.method = method;
        this.beanType = beanType;
        this.names.add(name);
        this.conditions = conditions;
    }

    @Override
    public Bean factoryBean(ApplicationContext applicationContext) {
        if (beanType == BeanType.SINGLETON && bean != null) {
            return bean;
        }
        Parameter[] parameters = method.getParameters();
        if (parameters != null && parameters.length > 0) {
            Object[] params = new Object[parameters.length];
            for (int i = 0; i < parameters.length; i++) {
                Parameter parameter = parameters[i];
                BeanInject annotation = parameter.getAnnotation(BeanInject.class);
                if (annotation != null) {
                    String name = annotation.name();
                    if (!StringUtils.hasText(name)) {
                        name = annotation.value();
                    }
                    params[i] = applicationContext.factory(name);
                }
            }
            return ReflectionHelper.invokeMethod(supplier.get(), method, params);
        }
        return ReflectionHelper.invokeMethod(supplier.get(), method);
    }

    @Override
    public Bean factoryNamedBean(String name, ApplicationContext applicationContext) {
        if (beanType == BeanType.SINGLETON && bean != null) {
            return bean;
        }
        Parameter[] parameters = method.getParameters();
        if (parameters != null && parameters.length > 0) {
            Object[] params = new Object[parameters.length];
            var beanInfoManager = applicationContext.getBeanInfoManager();
            for (int i = 0; i < parameters.length; i++) {
                Parameter parameter = parameters[i];
                BeanInject annotation = parameter.getAnnotation(BeanInject.class);
                if (annotation != null) {
                    var required = annotation.require();
                    String paramName = annotation.name();
                    if (!StringUtils.hasText(paramName)) {
                        paramName = annotation.value();
                    }
                    var paramType = parameter.getType();
                    var baseBeanFactory = beanInfoManager.getBeanFactory(paramName, paramType, required, false);
                    params[i] = baseBeanFactory.factoryProxiedBean(paramName, paramType, applicationContext);
                }
            }
            return ReflectionHelper.invokeMethod(supplier.get(), method, params);
        }
        return ReflectionHelper.invokeMethod(supplier.get(), method);
    }

    @Override
    @SuppressWarnings({"rawtypes"})
    public Bean factory(String name, Class beanClass, BeanType type, BeanProxyType proxyType, ApplicationContext applicationContext) {
        if (type == BeanType.SINGLETON && bean != null) {
            return bean;
        }
        Parameter[] parameters = method.getParameters();
        if (parameters != null && parameters.length > 0) {
            Object[] params = new Object[parameters.length];
            var beanInfoManager = applicationContext.getBeanInfoManager();
            for (int i = 0; i < parameters.length; i++) {
                Parameter parameter = parameters[i];
                BeanInject annotation = parameter.getAnnotation(BeanInject.class);
                if (annotation != null) {
                    var required = annotation.require();
                    String paramName = annotation.name();
                    if (!StringUtils.hasText(paramName)) {
                        paramName = annotation.value();
                    }
                    var paramType = parameter.getType();
                    var baseBeanFactory = beanInfoManager.getBeanFactory(paramName, paramType, required, false);
                    params[i] = baseBeanFactory.factoryProxiedBean(paramName, paramType, applicationContext);
                }
            }
            return ReflectionHelper.invokeMethod(supplier.get(), method, params);
        }
        return ReflectionHelper.invokeMethod(supplier.get(), method);
    }

    @Override
    public boolean isCreated() {
        return false;
    }

    @Override
    public Bean getCreatedBean() {
        return null;
    }

    @Override
    public Set<BeanCondition> getConditions() {
        return conditions;
    }

    @Override
    public Class<?> getBeanClass() {
        return method.getReturnType();
    }

    @Override
    public BeanType getBeanType() {
        return beanType;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

    public void addName(String name) {
        this.names.add(name);
    }

    @Override
    public Set<String> getBeanNames() {
        return names;
    }

    @Override
    public void destruct(ApplicationContext applicationContext) {
    }

    @Override
    public boolean equals(Object o) {
        return isEquals(o);
    }

    @Override
    public int hashCode() {
        return getHashCode(super.hashCode());
    }

    @Override
    public Logger getLogger() {
        return LoggerFactory.getLogger(ConfigurationMethodBeanFactory.class);
    }
}
