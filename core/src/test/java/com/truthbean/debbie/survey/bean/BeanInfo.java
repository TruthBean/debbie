package com.truthbean.debbie.survey.bean;

import com.truthbean.debbie.bean.BeanCondition;
import com.truthbean.debbie.proxy.BeanProxyType;

import java.util.Set;

/**
 * @author TruthBean
 * @since 0.5.3
 * Created on 2021/12/04 12:19.
 */
public interface BeanInfo {
    String NO_SCOPE = "noScope";

    String SINGLETON = "singleton";

    String getProfile();

    String getName();

    String getScope();

    default boolean isSingleton() {
        return SINGLETON.equals(getScope());
    }

    default boolean isNoScope() {
        return getScope() == null || NO_SCOPE.equals(getScope());
    }

    BeanProxyType getBeanProxyType();

    <T> Class<T> getBeanClass();

    Set<BeanCondition> getBeanConditions();

    boolean isLazyCreate();

    <T> T getValue();
}
