/**
 * Copyright (c) 2020 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 * http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.internal;

import com.truthbean.Logger;
import com.truthbean.debbie.bean.*;
import com.truthbean.debbie.core.ApplicationContext;
import com.truthbean.debbie.io.ResourceResolver;
import com.truthbean.debbie.lang.Nullable;
import com.truthbean.debbie.properties.DebbieConfigurationCenter;
import com.truthbean.logger.LoggerFactory;

/**
 * @author TruthBean
 * @since 0.0.1
 * Created on 2019/3/23 11:32.
 */
class DebbieApplicationContext implements ApplicationContext {

    private final BeanInitialization beanInitialization;
    private final DebbieConfigurationCenter configurationCenter;
    // private final MethodProxyHandlerRegister methodProxyHandlerRegister;

    private final ClassLoader classLoader;
    private final ResourceResolver resourceResolver;

    private final DebbieInjectedBeanFactory injectedBeanFactory;
    private final DebbieBeanInfoFactory debbieBeanInfoFactory;
    private final DebbieGlobalBeanFactory globalBeanFactory;

    private static final Object object = new Object();

    protected DebbieApplicationContext(@Nullable Class<?> applicationClass, ClassLoader classLoader) {
        synchronized (object) {
            resourceResolver = new ResourceResolver();
            beanInitialization = DebbieBeanInitialization.getInstance(applicationClass, classLoader, resourceResolver);
            this.configurationCenter = new DebbieConfigurationCenter();
            // methodProxyHandlerRegister = new MethodProxyHandlerRegister();

            this.classLoader = classLoader;

            this.injectedBeanFactory = new DebbieInjectedBeanFactory();
            this.debbieBeanInfoFactory = new DebbieBeanInfoFactory(this.beanInitialization);
            this.globalBeanFactory = new DebbieGlobalBeanFactory(debbieBeanInfoFactory);
        }
    }

    protected void postConstructor() {
        this.configurationCenter.setApplicationContext(this);
        this.injectedBeanFactory.setApplicationContext(this);

        this.globalBeanFactory.setDebbieApplicationContext(this);
        this.globalBeanFactory.setInjectedBeanFactory(injectedBeanFactory);

        this.injectedBeanFactory.setGlobalBeanFactory(this.globalBeanFactory);

        this.configurationCenter.setApplicationContext(this);
    }

    @Override
    public ClassLoader getClassLoader() {
        return classLoader;
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
    public DebbieBeanInfoFactory getBeanInfoFactory() {
        return debbieBeanInfoFactory;
    }

    @Override
    public DebbieConfigurationCenter getConfigurationCenter() {
        return configurationCenter;
    }

    /*public MethodProxyHandlerRegister getMethodProxyHandlerRegister() {
        return methodProxyHandlerRegister;
    }*/

    @Override
    public DebbieInjectedBeanFactory getInjectedBeanFactory() {
        return injectedBeanFactory;
    }

    @Override
    public GlobalBeanFactory getGlobalBeanFactory() {
        return globalBeanFactory;
    }

    @Override
    public void refreshBeans() {
        this.debbieBeanInfoFactory.refreshBeans();
    }

    @Override
    public <O, T> T transform(final O origin, final Class<T> target) {
        return DataTransformerFactory.transform(origin, target);
    }

    @Override
    public <T> T factory(String beanName) {
        return this.globalBeanFactory.factory(beanName);
    }

    @Override
    public <T> T factory(Class<T> beanType) {
        return this.globalBeanFactory.factory(beanType);
    }

    @Override
    public void release(String... args) {
        // must do nothing
    }

    protected void releaseBeans() {
        synchronized (DebbieApplicationContext.class) {
            injectedBeanFactory.destroy();

            this.debbieBeanInfoFactory.releaseBeans();

            beanInitialization.reset();
            resourceResolver.cleanResources();
            DataTransformerFactory.reset();
            LOGGER.info("release all bean.");
        }
    }

    public static final Logger LOGGER = LoggerFactory.getLogger(DebbieApplicationContext.class);
}
