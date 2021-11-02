package com.truthbean.debbie.empty;

import com.truthbean.debbie.bean.BeanInfo;
import com.truthbean.debbie.bean.BeanInfoFactory;
import com.truthbean.debbie.bean.GlobalBeanFactory;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author TruthBean
 * @since 0.5.3
 */
class EmptyBeanInfoFactory implements BeanInfoFactory {
    @Override
    public BeanInfoFactory refreshBeans() {
        return new EmptyBeanInfoFactory();
    }

    @Override
    public void autoCreateSingletonBeans(GlobalBeanFactory beanFactory) {

    }

    @Override
    public Set<BeanInfo<?>> getAllDebbieBeanInfo() {
        return new HashSet<>();
    }

    @Override
    public <T, K extends T> List<BeanInfo<K>> getBeanInfoList(Class<T> type, boolean require) {
        return new ArrayList<>();
    }

    @Override
    public <T> BeanInfo<T> getBeanInfo(String serviceName, Class<T> type, boolean require) {
        return null;
    }

    @Override
    public <T> BeanInfo<T> getBeanInfo(String serviceName, Class<T> type, boolean require, boolean throwException) {
        return null;
    }

    @Override
    public void destroy(BeanInfo<?> beanInfo) {

    }
}
