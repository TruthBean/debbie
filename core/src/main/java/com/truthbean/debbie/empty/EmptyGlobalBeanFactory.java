package com.truthbean.debbie.empty;

import com.truthbean.debbie.bean.BeanFactory;
import com.truthbean.debbie.bean.BeanInfo;
import com.truthbean.debbie.bean.GlobalBeanFactory;

import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

/**
 * @author TruthBean
 * @since 0.5.3
 */
class EmptyGlobalBeanFactory implements GlobalBeanFactory {
    @Override
    public <T> T factory(String serviceName) {
        return null;
    }

    @Override
    public <T> T factory(Class<T> type) {
        return null;
    }

    @Override
    public <T> T factoryIfPresent(Class<T> type) {
        return null;
    }

    @Override
    public <T> Optional<T> factoryIfPresent(String beanName) {
        return Optional.empty();
    }

    @Override
    public <T> Supplier<T> supply(String beanName) {
        return null;
    }

    @Override
    public <T> Supplier<T> supply(Class<T> type) {
        return null;
    }

    @Override
    public <T> void factoryByRawBean(T rawBean) {

    }

    @Override
    public <T> T factoryByNoBean(Class<T> noBeanType) {
        return null;
    }

    @Override
    public <T, K extends T> T factory(BeanInfo<K> beanInfo) {
        return null;
    }

    @Override
    public <T> T factoryBeanByDependenceProcessor(BeanInfo<T> beanInfo, boolean skipFactory) {
        return null;
    }

    @Override
    public <T> T factoryBeanByDependenceProcessor(BeanInfo<T> beanInfo, boolean skipFactory, Object firstParamValue) {
        return null;
    }

    @Override
    public <T> BeanInfo<T> getBeanInfoWithBean(Class<T> type) {
        return null;
    }

    @Override
    public <T, K extends T> List<K> getBeanList(Class<T> superType) {
        return null;
    }

    @Override
    public <T, K extends T> List<K> getBeanList(Class<T> superType, boolean withoutProxy) {
        return null;
    }

    @Override
    public <T> boolean containsBean(Class<T> beanType) {
        return false;
    }

    @Override
    public boolean containsBean(String beanName) {
        return false;
    }

    @Override
    public <T> T getBeanByFactory(BeanInfo<T> beanInfo) {
        return null;
    }

    @Override
    public <T> T getBeanByFactory(Class<T> beanClass, BeanFactory<T> beanFactory) {
        return null;
    }
}
