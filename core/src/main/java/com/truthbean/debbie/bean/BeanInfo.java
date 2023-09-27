package com.truthbean.debbie.bean;

import com.truthbean.debbie.proxy.BeanProxyType;

import java.util.HashSet;
import java.util.Set;

/**
 * @author TruthBean
 * @since 0.5.3
 * Created on 2021/12/04 12:19.
 */
public interface BeanInfo<Bean> {

    /**
     * 不用Class&lt;Bean&gt;或者Class&lt;? extends Bean&gt;的原因是，考虑到泛型问题
     *
     * @return Class&lt;? extends Bean&gt;
     */
    Class<?> getBeanClass();

    default BeanProxyType getBeanProxyType() {
        return BeanProxyType.JDK;
    }

    default boolean needProxy() {
        return getBeanProxyType() != BeanProxyType.NO;
    }

    default Set<BeanCondition> getConditions() {
        Set<BeanCondition> conditions = new HashSet<>();
        conditions.add(DefaultBeanCondition.INSTANCE);
        return conditions;
    }

    default boolean isLazyCreate() {
        return true;
    }

    default BeanType getBeanType() {
        return BeanType.SINGLETON;
    }

    default boolean isSingleton() {
        var beanType = getBeanType();
        return beanType == BeanType.SINGLETON;
    }

    String getName();

    default String profile() {
        return "default";
    }
}
