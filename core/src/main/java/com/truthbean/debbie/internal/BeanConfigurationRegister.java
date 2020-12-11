/**
 * Copyright (c) 2020 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *         http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.internal;

import com.truthbean.debbie.bean.*;
import com.truthbean.debbie.io.ResourceResolver;
import com.truthbean.debbie.reflection.ClassInfo;
import com.truthbean.debbie.reflection.ReflectionHelper;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Set;

/**
 * @author TruthBean
 * @since 0.0.2
 */
class BeanConfigurationRegister {

    public final ResourceResolver resourceResolver;
    public final BeanRegisterCenter beanRegisterCenter;

    BeanConfigurationRegister(BeanRegisterCenter beanRegisterCenter, ResourceResolver resourceResolver) {
        this.resourceResolver = resourceResolver;
        this.beanRegisterCenter = beanRegisterCenter;
    }

    public void register(Class<?>... classes) {
        if (classes != null) {
            for (Class<?> clazz : classes) {
                registerConfiguration(clazz);
            }
        }
    }

    public void register(Collection<Class<?>> classes) {
        if (classes != null && !classes.isEmpty()) {
            for (Class<?> clazz : classes) {
                registerConfiguration(clazz);
            }
        }
    }

    public void init(ClassLoader classLoader, String... packageName) {
        if (packageName != null) {
            for (String s : packageName) {
                register(s, classLoader);
            }
        }
    }

    public void register(String packageName, ClassLoader classLoader) {
        var allClass = ReflectionHelper.getAllClassByPackageName(packageName, classLoader, resourceResolver);
        if (!allClass.isEmpty()) {
            register(allClass);
        }
    }

    public <C> void registerConfiguration(Class<C> beanConfigurationClass) {
        if (beanRegisterCenter.support(beanConfigurationClass)
                && beanConfigurationClass.getAnnotation(BeanConfiguration.class) != null) {
            ClassInfo<C> classInfo = new ClassInfo<>(beanConfigurationClass);
            C configuration = ReflectionHelper.newInstance(beanConfigurationClass);
            register(configuration, classInfo);
        }
    }

    public <Configuration> void register(Configuration beanConfiguration) {
        @SuppressWarnings("unchecked")
        Class<Configuration> beanConfigurationClass = (Class<Configuration>) beanConfiguration.getClass();
        ClassInfo<Configuration> classInfo = new ClassInfo<>(beanConfigurationClass);
        register(beanConfiguration, classInfo);
    }

    private <Configuration> void register(Configuration configuration, ClassInfo<Configuration> classInfo) {
        Set<Method> annotationMethod = classInfo.getAnnotationMethod(DebbieBean.class);
        if (!annotationMethod.isEmpty()) {
            for (var method : annotationMethod) {
                MutableBeanInfo<?> beanInfo = new DebbieBeanInfo<>(method.getReturnType());
                DebbieBean debbieBean = method.getAnnotation(DebbieBean.class);
                var name = debbieBean.name();
                if (name.isBlank()) {
                    name = method.getName();
                }
                beanInfo.addBeanName(name);
                beanInfo.setBeanType(BeanType.SINGLETON);
                // todo params
                beanInfo.setBean(() -> ReflectionHelper.invokeMethod(configuration, method));
                beanInfo.setBeanFactory(new ConfigurationMethodBeanFactory<>(configuration, method));
                beanRegisterCenter.register(beanInfo);
            }
        }
    }
}
