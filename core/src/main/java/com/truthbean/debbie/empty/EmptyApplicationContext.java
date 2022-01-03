package com.truthbean.debbie.empty;

import com.truthbean.debbie.bean.*;
import com.truthbean.debbie.boot.ApplicationArgs;
import com.truthbean.debbie.core.ApplicationContext;
import com.truthbean.debbie.env.EnvironmentContent;
import com.truthbean.debbie.env.SystemEnvironmentContent;
import com.truthbean.debbie.event.AbstractDebbieEvent;
import com.truthbean.debbie.io.ResourceResolver;
import com.truthbean.debbie.reflection.ClassLoaderUtils;

import java.util.*;

/**
 * @author TruthBean
 * @since 0.5.3
 */
class EmptyApplicationContext implements ApplicationContext {

    private final ClassLoader classLoader = ClassLoaderUtils.getClassLoader(EmptyApplicationContext.class);
    private final ApplicationArgs applicationArgs = new ApplicationArgs();
    private final EnvironmentContent environmentContent = new SystemEnvironmentContent();
    private final ResourceResolver resourceResolver = new ResourceResolver();
    private final BeanInfoManager beanInfoManager = new EmptyBeanInfoManager();
    private final GlobalBeanFactory globalBeanFactory = new EmptyGlobalBeanFactory();
    private final Set<BeanLifecycle> beanLifecycles = new HashSet<>();

    @Override
    public ApplicationArgs getApplicationArgs() {
        return applicationArgs;
    }

    @Override
    public ClassLoader getClassLoader() {
        return classLoader;
    }

    @Override
    public EnvironmentContent getEnvContent() {
        return environmentContent;
    }

    @Override
    public ResourceResolver getResourceResolver() {
        return resourceResolver;
    }

    @Override
    public BeanInfoManager getBeanInfoManager() {
        return beanInfoManager;
    }

    @Override
    public GlobalBeanFactory getGlobalBeanFactory() {
        return globalBeanFactory;
    }

    @Override
    public Set<BeanLifecycle> getBeanLifecycle() {
        return beanLifecycles;
    }

    @Override
    public <E extends AbstractDebbieEvent> void publishEvent(E event) {
    }

    @Override
    public <T extends I, I> void registerSingleBean(Class<I> beanClass, T bean, String... names) {
    }

    @Override
    public void registerBeanLifecycle(BeanLifecycle beanLifecycle) {
        this.beanLifecycles.add(beanLifecycle);
    }

    // @Override
    public void refreshBeans() {

    }

    @Override
    public <O, T> T transform(O origin, Class<T> target) {
        return null;
    }

    @Override
    public <T> T factory(String beanName) {
        return null;
    }

    @Override
    public <T> T factory(Class<T> beanType) {
        return null;
    }

    @Override
    public void release(String... args) {

    }

    @Override
    public boolean isExiting() {
        return false;
    }
}
