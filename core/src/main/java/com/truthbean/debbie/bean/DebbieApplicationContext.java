/**
 * Copyright (c) 2020 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 * http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.bean;

import com.truthbean.Logger;
import com.truthbean.debbie.data.transformer.DataTransformerFactory;
import com.truthbean.debbie.io.ResourceResolver;
import com.truthbean.debbie.properties.DebbieConfigurationFactory;
import com.truthbean.debbie.proxy.*;
import com.truthbean.logger.LoggerFactory;

import java.lang.annotation.Annotation;

/**
 * @author TruthBean
 * @since 0.0.1
 * Created on 2019/3/23 11:32.
 */
public class DebbieApplicationContext {

    private final BeanInitialization beanInitialization;
    private final DebbieConfigurationFactory configurationFactory;
    private final MethodProxyHandlerRegister methodProxyHandlerRegister;

    private final ClassLoader classLoader;
    private final ResourceResolver resourceResolver;

    private final InjectedBeanFactory injectedBeanFactory;
    private final DebbieBeanInfoFactory debbieBeanInfoFactory;
    private final GlobalBeanFactory globalBeanFactory;

    private static final Object object = new Object();

    protected DebbieApplicationContext(ClassLoader classLoader) {
        synchronized (object) {
            resourceResolver = new ResourceResolver();
            beanInitialization = BeanInitialization.getInstance(classLoader, resourceResolver);
            this.configurationFactory = new DebbieConfigurationFactory();
            methodProxyHandlerRegister = new MethodProxyHandlerRegister();

            this.classLoader = classLoader;

            this.injectedBeanFactory = new InjectedBeanFactory();
            this.debbieBeanInfoFactory = new DebbieBeanInfoFactory(this.beanInitialization);
            this.globalBeanFactory = new GlobalBeanFactory(debbieBeanInfoFactory);
        }
    }

    protected void postConstructor() {
        this.configurationFactory.setDebbieApplicationContext(this);
        this.injectedBeanFactory.setBeanFactoryContext(this);

        this.globalBeanFactory.setDebbieApplicationContext(this);
        this.globalBeanFactory.setInjectedBeanFactory(injectedBeanFactory);

        this.injectedBeanFactory.setGlobalBeanFactory(this.globalBeanFactory);

        this.configurationFactory.setDebbieApplicationContext(this);
    }

    public ClassLoader getClassLoader() {
        return classLoader;
    }

    public ResourceResolver getResourceResolver() {
        return resourceResolver;
    }

    public BeanInitialization getBeanInitialization() {
        return beanInitialization;
    }

    public DebbieBeanInfoFactory getDebbieBeanInfoFactory() {
        return debbieBeanInfoFactory;
    }

    public DebbieConfigurationFactory getConfigurationFactory() {
        return configurationFactory;
    }

    public MethodProxyHandlerRegister getMethodProxyHandlerRegister() {
        return methodProxyHandlerRegister;
    }

    public InjectedBeanFactory getInjectedBeanFactory() {
        return injectedBeanFactory;
    }

    public GlobalBeanFactory getGlobalBeanFactory() {
        return globalBeanFactory;
    }

    public void refreshBeans() {
        this.debbieBeanInfoFactory.refreshBeans();
    }

    public <O, T> T transform(final O origin, final Class<T> target) {
        return DataTransformerFactory.transform(origin, target);
    }

    public void release(String... args) {
        // must do nothing
    }

    protected void releaseBeans() {
        injectedBeanFactory.destroy();

        this.debbieBeanInfoFactory.releaseBeans();

        beanInitialization.reset();
        resourceResolver.cleanResources();
        LOGGER.info("release all bean.");
    }

    public static final Logger LOGGER = LoggerFactory.getLogger(DebbieApplicationContext.class);
}
