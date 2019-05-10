package com.truthbean.debbie.core.proxy;

import java.lang.annotation.Annotation;

public interface MethodProxyHandler<A extends Annotation> {

    default void setMethodAnnotation(A methodAnnotation) {
    }

    default void before() {
    }

    default void after() {
    }

    default void whenExceptionCached(Throwable e) {
        e.printStackTrace();
    }

    default void finallyRun() {
    }
}
