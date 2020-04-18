package com.truthbean.debbie.bean;

import java.lang.annotation.Annotation;

/**
 * @author TruthBean
 * @since 0.0.1
 * Created on 2019/5/22 21:08.
 */
public interface AnnotationRegister<A extends Annotation> {

    /**
     * 注册被
     *      @see BeanComponent
     * 标识的 annotation
     */
    void register();

    BeanInitialization getBeanInitialization();

    default void register(Class<A> annotation) {
        getBeanInitialization().registerBeanAnnotation(annotation);
    }
}
