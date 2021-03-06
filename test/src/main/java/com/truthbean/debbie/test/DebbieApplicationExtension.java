/**
 * Copyright (c) 2021 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *         http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.test;

import com.truthbean.debbie.bean.BeanInject;
import com.truthbean.debbie.bean.GlobalBeanFactory;
import com.truthbean.debbie.bean.InjectedBeanFactory;
import com.truthbean.debbie.boot.DebbieApplication;
import com.truthbean.debbie.core.ApplicationContext;
import com.truthbean.debbie.core.ApplicationFactory;
import com.truthbean.debbie.properties.PropertyInject;
import com.truthbean.debbie.proxy.jdk.JdkDynamicProxy;
import com.truthbean.debbie.reflection.ClassLoaderUtils;
import org.junit.jupiter.api.extension.*;
import com.truthbean.Logger;
import com.truthbean.LoggerFactory;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Optional;
import java.util.Set;

/**
 * @author truthbean
 * @since 0.0.2
 */
public class DebbieApplicationExtension implements BeforeAllCallback, AfterAllCallback, ParameterResolver,
        BeforeEachCallback, AfterEachCallback, TestInstancePostProcessor,
        BeforeTestExecutionCallback, AfterTestExecutionCallback {

    private static final Logger logger = LoggerFactory.getLogger(DebbieApplicationExtension.class);

    private static final String START_TIME = "start time";

    static {
        try {
            Class.forName("org.apache.logging.log4j.jul.LogManager", false, ClassLoaderUtils.getClassLoader(DebbieApplicationExtension.class));
            System.setProperty("java.util.logging.manager", "org.apache.logging.log4j.jul.LogManager");
        } catch (Throwable ignored) {
        }
    }

    @Override
    public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext)
        throws ParameterResolutionException {
        logger.trace("supportsParameter....");
        ApplicationContext applicationContext = getApplicationContext(extensionContext);
        InjectedBeanFactory injectedBeanFactory = applicationContext.getInjectedBeanFactory();
        Set<Class<? extends Annotation>> injectTypes = injectedBeanFactory.getInjectTypes();
        for (Class<? extends Annotation> injectType : injectTypes) {
            if (parameterContext.isAnnotated(injectType)) {
                if (logger.isTraceEnabled())
                    logger.trace("parameter inject type is: " + injectType);
                return true;
            }
        }
        return false;
    }

    @Override
    public Object resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext)
        throws ParameterResolutionException {
        logger.trace("resolveParameter....");
        ApplicationContext applicationContext = getApplicationContext(extensionContext);
        if (parameterContext.isAnnotated(BeanInject.class)) {
            return getDebbieBean(parameterContext.getParameter(), applicationContext);
        } else if (parameterContext.isAnnotated(PropertyInject.class)) {
            return getPropertyValue(parameterContext.getParameter(), applicationContext);
        } else {
            return getOtherInjectedBean(parameterContext.getParameter(), applicationContext);
        }
    }

    private Object getDebbieBean(Parameter parameter, ApplicationContext applicationContext) {
        BeanInject beanInject = parameter.getAnnotation(BeanInject.class);
        String name = beanInject.name();
        if (name.isBlank()) {
            name = beanInject.value();
        }

        Class<?> type = parameter.getType();
        GlobalBeanFactory globalBeanFactory = applicationContext.getGlobalBeanFactory();

        Object result;
        if (name.isBlank()) {
            result = globalBeanFactory.factory(type);
        } else {
            result = globalBeanFactory.factory(name);
        }

        if (type.isInstance(result)) {
            return result;
        } else {
            return JdkDynamicProxy.getRealValue(result);
        }
    }

    private Object getOtherInjectedBean(Parameter parameter, ApplicationContext applicationContext) {
        InjectedBeanFactory injectedBeanFactory = applicationContext.getInjectedBeanFactory();
        Set<Class<? extends Annotation>> injectTypes = injectedBeanFactory.getInjectTypes();

        Class<? extends Annotation> injectClass = null;
        Annotation inject;

        for (Class<? extends Annotation> injectType : injectTypes) {
            inject = parameter.getAnnotation(injectType);
            if (inject != null) {
                injectClass = injectType;
                break;
            }
        }

        if (logger.isTraceEnabled())
            logger.trace("parameter inject type is: " + injectClass);

        Class<?> type = parameter.getType();
        GlobalBeanFactory globalBeanFactory = applicationContext.getGlobalBeanFactory();

        Object result = globalBeanFactory.factory(type);

        if (type.isInstance(result)) {
            return result;
        } else {
            return JdkDynamicProxy.getRealValue(result);
        }
    }

    private Object getPropertyValue(Parameter parameter, ApplicationContext applicationContext) {
        PropertyInject propertyInject = parameter.getAnnotation(PropertyInject.class);
        InjectedBeanFactory injectedBeanFactory = applicationContext.getInjectedBeanFactory();
        Class<?> type = parameter.getType();
        return injectedBeanFactory.factoryProperty(type, null, propertyInject);
    }

    private ApplicationContext getApplicationContext(ExtensionContext context) {
        return context.getRoot()
                .getStore(ExtensionContext.Namespace.GLOBAL)
                .getOrComputeIfAbsent(ApplicationContext.class);
    }

    @Override
    public void beforeTestExecution(final ExtensionContext context) throws Exception {
        logger.debug("beforeTestExecution ...");
        Optional<Object> instance = context.getTestInstance();
        if (instance.isEmpty()) {
            logger.debug(() -> "test instance is empty");
        }
        getStore(context).put(START_TIME, System.currentTimeMillis());
    }

    @Override
    public void afterTestExecution(ExtensionContext context) throws Exception {
        logger.debug("afterTestExecution ...");
        Method testMethod = context.getRequiredTestMethod();
        long startTime = getStore(context).remove(START_TIME, long.class);
        long duration = System.currentTimeMillis() - startTime;

        logger.info(String.format("Method [%s.%s] took %s ms.", testMethod.getDeclaringClass(), testMethod.getName(), duration));
    }

    private ExtensionContext.Store getStore(ExtensionContext context) {
        return context.getStore(ExtensionContext.Namespace
                .create(TestSuitConstants.NAMESPACE, context.getRequiredTestMethod()));
    }

    @Override
    public void beforeAll(ExtensionContext context) throws Exception {
        logger.debug("beforeAll ...");
        Optional<Class<?>> testClass = context.getTestClass();
        Class<?> applicationClass = ApplicationFactory.class;
        if (testClass.isPresent()) {
            applicationClass = testClass.get();
        }
        ApplicationFactory applicationFactory = ApplicationFactory.configure(applicationClass);
        DebbieApplication debbieApplication = applicationFactory.postCreateApplication();
        debbieApplication.start();
        ApplicationContext applicationContext = applicationFactory.getApplicationContext();
        ExtensionContext.Store store = context.getRoot().getStore(ExtensionContext.Namespace.GLOBAL);
        store.put(ApplicationContext.class, applicationContext);
        store.put(DebbieApplication.class, debbieApplication);
    }

    @Override
    public void afterAll(ExtensionContext context) throws Exception {
        logger.debug("afterAll...");
        ExtensionContext.Store store = context.getRoot().getStore(ExtensionContext.Namespace.GLOBAL);
        DebbieApplication debbieApplication = store.get(DebbieApplication.class, DebbieApplication.class);
        if (debbieApplication != null) {
            debbieApplication.exit();
        }
    }

    @Override
    public void afterEach(ExtensionContext context) throws Exception {
        logger.debug("afterEach");
    }

    @Override
    public void beforeEach(ExtensionContext context) throws Exception {
        logger.debug("beforeEach");
        Optional<Object> instance = context.getTestInstance();
        instance.ifPresent(o -> {
            ApplicationContext applicationContext = getApplicationContext(context);
            GlobalBeanFactory globalBeanFactory = applicationContext.getGlobalBeanFactory();
            globalBeanFactory.factoryByRawBean(o);
            logger.trace(() -> "test instance: " + o);
        });
    }

    @Override
    public void postProcessTestInstance(Object testInstance, ExtensionContext context) throws Exception {
        logger.debug("postProcessTestInstance");
    }
}
