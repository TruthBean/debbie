package com.truthbean.debbie.internal;

import com.truthbean.debbie.bean.*;
import com.truthbean.debbie.boot.ApplicationArgs;
import com.truthbean.debbie.boot.ApplicationBootContext;
import com.truthbean.debbie.core.ApplicationContext;
import com.truthbean.debbie.environment.Environment;
import com.truthbean.debbie.environment.EnvironmentDepositoryHolder;
import com.truthbean.debbie.event.AbstractDebbieEvent;
import com.truthbean.debbie.io.ResourceResolver;
import com.truthbean.debbie.proxy.BeanProxyType;

import java.util.List;
import java.util.Set;

/**
 * @author TruthBean/Rogar·Q
 * @since 0.5.5
 */
public class DebbieApplicationBootContext implements ApplicationBootContext {

    private final ApplicationContext applicationContext;

    public DebbieApplicationBootContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @Override
    public ApplicationArgs getApplicationArgs() {
        return applicationContext.getApplicationArgs();
    }

    @Override
    public ClassLoader getClassLoader() {
        return applicationContext.getClassLoader();
    }

    @Override
    public EnvironmentDepositoryHolder getEnvironmentHolder() {
        return applicationContext.getEnvironmentHolder();
    }

    @Override
    public Environment getDefaultEnvironment() {
        return applicationContext.getDefaultEnvironment();
    }

    @Override
    public ResourceResolver getResourceResolver() {
        return applicationContext.getResourceResolver();
    }

    @Override
    public BeanInfoManager getBeanInfoManager() {
        return applicationContext.getBeanInfoManager();
    }

    @Override
    public GlobalBeanFactory getGlobalBeanFactory() {
        return applicationContext.getGlobalBeanFactory();
    }

    @Override
    public Set<BeanLifecycle> getBeanLifecycle() {
        return applicationContext.getBeanLifecycle();
    }

    @Override
    public <T> void registerBean(BeanFactory<T> beanFactory) {
        applicationContext.getBeanInfoManager().registerBeanInfo(beanFactory);
    }

    @Override
    public <T extends I, I> void registerSingleBean(Class<I> beanClass, T bean, String... names) {
        applicationContext.getBeanInfoManager().registerBeanInfo(new SimpleBeanFactory<>(bean, beanClass, BeanProxyType.JDK, names));
    }

    @Override
    public void registerBeanLifecycle(BeanLifecycle beanLifecycle) {
        applicationContext.getBeanInfoManager().registerBeanLifecycle(beanLifecycle);
    }

    @Override
    public <O, T> T transform(O origin, Class<T> target) {
        return applicationContext.transform(origin, target);
    }

    @Override
    public <T> T factory(String beanName) {
        return applicationContext.getGlobalBeanFactory().factory(beanName);
    }

    @Override
    public <T> T factory(Class<T> beanType) {
        return applicationContext.getGlobalBeanFactory().factory(beanType);
    }

    @Override
    public <T> List<T> factories(Class<T> beanType) {
        return applicationContext.getGlobalBeanFactory().factories(beanType);
    }

    @Override
    public <T> T factoryConfiguration(String profile, String category, Class<T> beanType) {
        return applicationContext.getGlobalBeanFactory().factoryConfiguration(beanType, profile, category);
    }

    @Override
    public <T> T factory(BeanInjection<T> injection) {
        return applicationContext.getGlobalBeanFactory().factory(injection);
    }

    @Override
    public void release(String... args) {
        applicationContext.release(args);
    }

    @Override
    public boolean isExiting() {
        return applicationContext.isExiting();
    }

    @Override
    public <E extends AbstractDebbieEvent> void publishEvent(E event) {
        applicationContext.publishEvent(event);
    }
}
