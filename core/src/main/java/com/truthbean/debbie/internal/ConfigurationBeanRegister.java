/**
 * Copyright (c) 2023 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 * http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.internal;

import com.truthbean.debbie.bean.*;
import com.truthbean.debbie.core.ApplicationContext;
import com.truthbean.debbie.proxy.BeanProxyType;
import com.truthbean.debbie.reflection.ClassInfo;
import com.truthbean.debbie.reflection.ReflectionHelper;

import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Supplier;

/**
 * @author TruthBean
 * @since 0.0.2
 */
class ConfigurationBeanRegister implements BeanRegister {

    private final ApplicationContext applicationContext;

    public ConfigurationBeanRegister(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @Override
    public <Bean> boolean support(ClassBeanInfo<Bean> beanInfo) {
        return beanInfo.containClassAnnotation(BeanConfiguration.class);
    }

    @Override
    public <Bean> BeanFactory<Bean> getBeanFactory(ClassBeanInfo<Bean> beanInfo) {
        DebbieReflectionBeanFactory<Bean> beanFactory = new DebbieReflectionBeanFactory<>(beanInfo);
        register(() -> beanFactory.factoryBean(applicationContext), beanInfo);
        return beanFactory;
    }

    private <Configuration> void register(Supplier<Configuration> configuration, ClassInfo<Configuration> classInfo) {
        Set<Method> annotationMethod = classInfo.getAnnotationMethod(DebbieBean.class);
        if (!annotationMethod.isEmpty()) {
            for (var method : annotationMethod) {
                DebbieBean debbieBean = method.getAnnotation(DebbieBean.class);
                var name = debbieBean.name();
                if (name.isBlank()) {
                    name = method.getName();
                }
                BeanType type = debbieBean.type();
                BeanProxyType proxy = debbieBean.proxy();
                Class<? extends BeanCondition>[] conditions = debbieBean.conditions();
                Set<BeanCondition> conditionSet = new HashSet<>();
                for (Class<? extends BeanCondition> condition : conditions) {
                    if (DefaultBeanCondition.class.isAssignableFrom(condition)) {
                        conditionSet.add(DefaultBeanCondition.INSTANCE);
                    } else if (ReflectionEnableCondition.class.isAssignableFrom(condition)) {
                        conditionSet.add(ReflectionEnableCondition.INSTANCE);
                    } else {
                        conditionSet.add(ReflectionHelper.newInstance(condition));
                    }
                }
                if (conditionSet.isEmpty()) {
                    conditionSet.add(DefaultBeanCondition.INSTANCE);
                }

                var beanInfo = new ConfigurationMethodBeanFactory<>(configuration, method, type, proxy, name, conditionSet);
                applicationContext.getBeanInfoManager().registerBeanInfo(beanInfo);
            }
        }
    }

    @Override
    public int getOrder() {
        return -1;
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }
}