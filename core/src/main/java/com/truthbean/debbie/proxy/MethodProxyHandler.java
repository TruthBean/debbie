package com.truthbean.debbie.proxy;

import com.truthbean.debbie.bean.BeanFactoryHandler;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

/**
 * @author truthbean
 * @since 0.0.1
 */
public interface MethodProxyHandler<A extends Annotation> extends Comparable<MethodProxyHandler> {

    default void setBeanFactoryHandler(BeanFactoryHandler beanFactoryHandler) {
    }

    /**
     * when method has more Annotation
     * call handler order by it
     * @return order
     */
    int getOrder();

    void setOrder(int order);

    default boolean exclusive() {
        return false;
    }

    default void setMethodAnnotation(A methodAnnotation) {
    }

    default void setClassAnnotation(A classAnnotation) {
    }

    default void setMethod(Method method) {
    }

    void before();

    void after();

    void catchException(Throwable e) throws Throwable;

    void finallyRun();

    @Override
    default int compareTo(MethodProxyHandler o) {
        int order = o.getOrder();
        return Integer.compare(order, getOrder());
    }
}
