/**
 * Copyright (c) 2020 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *         http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.rmi;

import com.truthbean.debbie.bean.AnnotationRegister;
import com.truthbean.debbie.bean.BeanInitialization;
import com.truthbean.debbie.bean.DebbieBeanInfo;

import java.lang.annotation.Annotation;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * @author truthbean
 * @since 0.0.2
 */
public class DebbieRmiServiceRegister implements AnnotationRegister<DebbieRmiService> {

    private final BeanInitialization beanInitialization;

    public DebbieRmiServiceRegister(BeanInitialization beanInitialization) {
        this.beanInitialization = beanInitialization;
    }

    @Override
    public void register() {
        register(DebbieRmiService.class);
    }

    @Override
    public BeanInitialization getBeanInitialization() {
        return beanInitialization;
    }

    public Set<Class<?>> getRmiServiceMappers() {
        Set<Class<?>> rmiServiceMappers = new LinkedHashSet<>();
        Set<DebbieBeanInfo<?>> beanInfos = beanInitialization.getRegisteredRawBeans();
        for (DebbieBeanInfo<?> beanInfo : beanInfos) {
            Annotation classAnnotation = beanInfo.getClassAnnotation(DebbieRmiMapper.class);
            if (classAnnotation != null && beanInfo.getBeanClass().isInterface()) {
                rmiServiceMappers.add(beanInfo.getBeanClass());
            }
        }
        return rmiServiceMappers;
    }
}
