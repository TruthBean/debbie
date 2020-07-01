/**
 * Copyright (c) 2020 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *         http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.test;

import com.truthbean.debbie.bean.DebbieApplicationContext;
import com.truthbean.debbie.bean.BeanInject;
import com.truthbean.debbie.bean.GlobalBeanFactory;
import com.truthbean.debbie.bean.InjectedBeanFactory;
import com.truthbean.debbie.boot.DebbieApplication;
import com.truthbean.debbie.boot.DebbieApplicationFactory;
import com.truthbean.debbie.properties.PropertyInject;
import com.truthbean.debbie.proxy.jdk.JdkDynamicProxy;
import org.junit.jupiter.api.extension.*;
import com.truthbean.Logger;
import com.truthbean.logger.LoggerFactory;

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
        BeforeTestExecutionCallback, AfterTestExecutionCallback {

    private static final Logger logger = LoggerFactory.getLogger(DebbieApplicationExtension.class);

    private static final String START_TIME = "start time";

    @Override
    public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext)
        throws ParameterResolutionException {
        logger.trace("supportsParameter....");
        DebbieApplicationContext applicationContext = getBeanFactoryHandler(extensionContext);
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
        DebbieApplicationContext applicationContext = getBeanFactoryHandler(extensionContext);
        if (parameterContext.isAnnotated(BeanInject.class)) {
            return getDebbieBean(parameterContext.getParameter(), applicationContext);
        } else if (parameterContext.isAnnotated(PropertyInject.class)) {
            return getPropertyValue(parameterContext.getParameter(), applicationContext);
        } else {
            return getOtherInjectedBean(parameterContext.getParameter(), applicationContext);
        }
    }

    private Object getDebbieBean(Parameter parameter, DebbieApplicationContext applicationContext) {
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

    private Object getOtherInjectedBean(Parameter parameter, DebbieApplicationContext applicationContext) {
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

    private Object getPropertyValue(Parameter parameter, DebbieApplicationContext applicationContext) {
        PropertyInject propertyInject = parameter.getAnnotation(PropertyInject.class);
        InjectedBeanFactory injectedBeanFactory = applicationContext.getInjectedBeanFactory();
        Class<?> type = parameter.getType();
        return injectedBeanFactory.factoryProperty(type, null, propertyInject);
    }

    private DebbieApplicationContext getBeanFactoryHandler(ExtensionContext context) {
        return context.getRoot()
                .getStore(ExtensionContext.Namespace.GLOBAL)
                .getOrComputeIfAbsent(DebbieApplicationContext.class);
    }

    @Override
    public void beforeTestExecution(ExtensionContext context) throws Exception {
        DebbieApplicationContext applicationContext = getBeanFactoryHandler(context);
        GlobalBeanFactory globalBeanFactory = applicationContext.getGlobalBeanFactory();
        Optional<Object> instance = context.getTestInstance();
        instance.ifPresent(globalBeanFactory::factoryByRawBean);

        getStore(context).put(START_TIME, System.currentTimeMillis());
    }

    @Override
    public void afterTestExecution(ExtensionContext context) throws Exception {
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
        logger.debug("beforeTestExecution...");
        Optional<Class<?>> testClass = context.getTestClass();
        Class<?> applicationClass = DebbieApplicationFactory.class;
        if (testClass.isPresent()) {
            applicationClass = testClass.get();
        }
        DebbieApplicationFactory applicationFactory = DebbieApplicationFactory.configure(applicationClass);
        DebbieApplication debbieApplication = applicationFactory.postCreateApplication();
        debbieApplication.start();
        DebbieApplicationContext applicationContext = applicationFactory.getApplicationContext();
        ExtensionContext.Store store = context.getRoot().getStore(ExtensionContext.Namespace.GLOBAL);
        store.put(DebbieApplicationContext.class, applicationContext);
        store.put(DebbieApplication.class, debbieApplication);
    }

    @Override
    public void afterAll(ExtensionContext context) throws Exception {
        logger.debug("afterTestExecution...");
        ExtensionContext.Store store = context.getRoot().getStore(ExtensionContext.Namespace.GLOBAL);
        DebbieApplication debbieApplication = store.getOrComputeIfAbsent(DebbieApplication.class);
        debbieApplication.exit();
    }
}
