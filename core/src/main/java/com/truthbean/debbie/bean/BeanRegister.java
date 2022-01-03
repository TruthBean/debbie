package com.truthbean.debbie.bean;

import com.truthbean.debbie.annotation.AnnotationInfo;

import java.lang.annotation.Annotation;
import java.util.Map;

/**
 * @author TruthBean
 * @since 0.5.3
 * Created on 2021/12/07 19:24.
 */
public interface BeanRegister extends Comparable<BeanRegister> {

    <Bean> boolean support(ClassBeanInfo<Bean> beanInfo);

    default <Bean> boolean support(ClassBeanInfo<Bean> beanInfo, Class<? extends Annotation> annotationClass) {
        if (beanInfo.containClassAnnotation(annotationClass)) {
            final Map<Class<? extends Annotation>, AnnotationInfo> classAnnotations = beanInfo.getClassAnnotations();
            var value = classAnnotations.get(annotationClass);
            return value != null;
        }
        return false;
    }

    <Bean> BeanFactory<Bean> getBeanFactory(ClassBeanInfo<Bean> beanInfo);

    int getOrder();

    @Override
    default int compareTo(BeanRegister o) {
        return Integer.compare(getOrder(), o.getOrder());
    }
}
