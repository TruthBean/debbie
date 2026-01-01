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

    /**
     * check whether the bean register support the bean info
     * @param beanInfo the bean info
     * @return true if the bean register support the bean info
     * @param <Bean> the bean type
     */
    <Bean> boolean support(ClassBeanInfo<Bean> beanInfo);

    /**
     * check whether the bean register support the bean info with the annotation
     * @param beanInfo the bean info
     * @param annotationClass the annotation class
     * @return true if the bean register support the bean info with the annotation
     * @param <Bean> the bean type
     */
    default <Bean> boolean support(ClassBeanInfo<Bean> beanInfo, Class<? extends Annotation> annotationClass) {
        if (beanInfo.containClassAnnotation(annotationClass)) {
            final Map<Class<? extends Annotation>, AnnotationInfo> classAnnotations = beanInfo.getClassAnnotations();
            var value = classAnnotations.get(annotationClass);
            return value != null;
        }
        return false;
    }

    /**
     * get the bean factory by ClassBeanInfo
     * @param beanInfo ClassBeanInfo
     * @return the bean factory
     * @param <Bean> the bean type
     */
    <Bean> BeanFactory<Bean> getBeanFactory(ClassBeanInfo<Bean> beanInfo);

    /**
     * get the order of the bean register,
     * the lower the order, the higher the priority
     * @return the order of the bean register
     */
    int getOrder();

    @Override
    default int compareTo(BeanRegister o) {
        return Integer.compare(getOrder(), o.getOrder());
    }
}
