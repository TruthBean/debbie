package com.truthbean.debbie.bean;

import java.util.List;
import java.util.Set;

public interface BeanInfoFactory {
    BeanInfoFactory refreshBeans();

    void autoCreateSingletonBeans(GlobalBeanFactory beanFactory);

    Set<BeanInfo<?>> getAllDebbieBeanInfo();

    <T, K extends T> List<BeanInfo<K>> getBeanInfoList(Class<T> type, boolean require);

    <T> BeanInfo<T> getBeanInfo(String serviceName, Class<T> type, boolean require);

    <T> BeanInfo<T> getBeanInfo(String serviceName, Class<T> type, boolean require, boolean throwException);

    void destroy(BeanInfo<?> beanInfo);
}
