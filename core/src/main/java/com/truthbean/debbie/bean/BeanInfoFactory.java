package com.truthbean.debbie.bean;

import java.util.List;
import java.util.Set;

public interface BeanInfoFactory {
    void refreshBeans();

    void autoCreateSingletonBeans(GlobalBeanFactory beanFactory);

    Set<DebbieBeanInfo<?>> getAllDebbieBeanInfo();

    <T, K extends T> List<DebbieBeanInfo<K>> getBeanInfoList(Class<T> type, boolean require);

    <T> DebbieBeanInfo<T> getBeanInfo(String serviceName, Class<T> type, boolean require);

    <T> DebbieBeanInfo<T> getBeanInfo(String serviceName, Class<T> type, boolean require, boolean throwException);

    void destroy(DebbieBeanInfo<?> beanInfo);
}
