package com.truthbean.debbie.empty;

import com.truthbean.debbie.bean.*;
import com.truthbean.debbie.boot.ApplicationArgs;
import com.truthbean.debbie.core.ApplicationContext;
import com.truthbean.debbie.env.EnvironmentContent;
import com.truthbean.debbie.env.SystemEnvironmentContent;
import com.truthbean.debbie.io.ResourceResolver;
import com.truthbean.debbie.properties.DebbieConfigurationCenter;
import com.truthbean.debbie.reflection.ClassLoaderUtils;

/**
 * @author TruthBean
 * @since 0.5.3
 */
class EmptyApplicationContext implements ApplicationContext {

    private final ClassLoader classLoader = ClassLoaderUtils.getClassLoader(EmptyApplicationContext.class);
    private final ApplicationArgs applicationArgs = new ApplicationArgs();
    private final EnvironmentContent environmentContent = new SystemEnvironmentContent();
    private final ResourceResolver resourceResolver = new ResourceResolver();
    private final BeanInitialization beanInitialization = new EmptyBeanInitialization();
    private final BeanInfoFactory beanInfoFactory = new EmptyBeanInfoFactory();
    private final InjectedBeanFactory injectedBeanFactory = new EmptyInjectedBeanFactory();
    private final GlobalBeanFactory globalBeanFactory = new EmptyGlobalBeanFactory();

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
    public BeanInitialization getBeanInitialization() {
        return beanInitialization;
    }

    @Override
    public BeanInfoFactory getBeanInfoFactory() {
        return beanInfoFactory;
    }

    @Override
    public DebbieConfigurationCenter getConfigurationCenter() {
        return null;
    }

    @Override
    public InjectedBeanFactory getInjectedBeanFactory() {
        return injectedBeanFactory;
    }

    @Override
    public GlobalBeanFactory getGlobalBeanFactory() {
        return globalBeanFactory;
    }

    @Override
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
}
