package com.truthbean.debbie.rmi;

import com.truthbean.debbie.bean.AnnotationRegister;
import com.truthbean.debbie.bean.BeanInitialization;
import com.truthbean.debbie.bean.DebbieBeanInfo;

import java.lang.annotation.Annotation;
import java.util.LinkedHashSet;
import java.util.Set;

public class DebbieRmiServiceRegister implements AnnotationRegister<DebbieRmiService> {
    @Override
    public void register() {
        register(DebbieRmiService.class);
    }

    public Set<Class<?>> getRmiServiceMappers(BeanInitialization beanInitialization) {
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
