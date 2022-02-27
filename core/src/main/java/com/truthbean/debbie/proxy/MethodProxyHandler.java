/**
 * Copyright (c) 2022 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *         http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.proxy;

import com.truthbean.debbie.core.ApplicationContext;
import com.truthbean.debbie.core.ApplicationContextAware;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

/**
 * @author truthbean
 * @since 0.0.1
 */
public interface MethodProxyHandler<A extends Annotation> extends Comparable<MethodProxyHandler>,
        ApplicationContextAware {

    @Override
    default void setApplicationContext(ApplicationContext applicationContext) {
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

    default boolean sync() {
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
