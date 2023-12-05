package com.truthbean.debbie.empty;

import com.truthbean.debbie.bean.BeanInfo;
import com.truthbean.debbie.bean.BeanInjection;
import com.truthbean.debbie.bean.BeanSupplier;
import com.truthbean.debbie.bean.GlobalBeanFactory;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Supplier;

/**
 * @author TruthBean
 * @since 0.5.3
 */
class EmptyGlobalBeanFactory implements GlobalBeanFactory {
    @Override
    public <T> T factory(String beanName) {
        return null;
    }

    @Override
    public <T> T factory(Class<T> type) {
        return null;
    }

    @Override
    public <T> T factoryWithoutProxy(Class<T> type) {
        return null;
    }

    @Override
    public <T> T factory(String serviceName, Class<T> type) {
        return null;
    }

    @Override
    public <T> T factory(String serviceName, Class<T> type, boolean required) {
        return null;
    }

    //@Override
    public <T> T factoryConfiguration(String profile, String category, Class<T> beanType) {
        return null;
    }

    @Override
    public <T> T factoryIfPresent(Class<T> type) {
        return null;
    }

    @Override
    public <T> T factoryIfPresentOrElse(Class<T> type, Supplier<T> otherFactory) {
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
    public <T> T factoryConfiguration(Class<T> type, String profile, String category) {
        return null;
    }

    @Override
    public <T> T factory(BeanInjection<T> injection) {
        return null;
    }

    @Override
    public <T> T factoryByRawBean(BeanInjection<T> injection, T rawBean) {
        return null;
    }

    @Override
    public <T> T factory(BeanInjection<T> injection, BeanSupplier<T> beanInfo) {
        return null;
    }

    // @Override
    public <T> T factory(BeanInjection<T> injection, BeanInfo<T> beanInfo) {
        return null;
    }

    @Override
    public <Bean> Set<Bean> getBeanList(Class<Bean> superType) {
        return null;
    }

    @Override
    public <T> List<T> factories(Class<T> beanType) {
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
}
