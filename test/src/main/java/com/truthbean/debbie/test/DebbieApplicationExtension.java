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

import com.truthbean.debbie.bean.BeanFactoryContext;
import com.truthbean.debbie.bean.BeanInject;
import com.truthbean.debbie.boot.DebbieApplication;
import com.truthbean.debbie.boot.DebbieApplicationFactory;
import com.truthbean.debbie.proxy.jdk.JdkDynamicProxy;
import com.truthbean.debbie.reflection.ReflectionHelper;
import org.junit.jupiter.api.extension.*;
import com.truthbean.Logger;
import com.truthbean.logger.LoggerFactory;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.List;
import java.util.Optional;

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
        return parameterContext.isAnnotated(BeanInject.class);
    }

    @Override
    public Object resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext)
        throws ParameterResolutionException {
        logger.trace("resolveParameter....");
        return getDebbieBean(parameterContext.getParameter(), extensionContext);
    }

    private Object getDebbieBean(Parameter parameter, ExtensionContext extensionContext) {
        BeanInject beanInject = parameter.getAnnotation(BeanInject.class);
        String name = beanInject.name();
        if (name.isBlank()) {
            name = beanInject.value();
        }

        Class<?> type = parameter.getType();
        BeanFactoryContext applicationContext = getBeanFactoryHandler(extensionContext);

        Object result;
        if (name.isBlank()) {
            result = applicationContext.factory(type);
        } else {
            result = applicationContext.factory(name);
        }

        if (type.isInstance(result)) {
            return result;
        } else {
            return JdkDynamicProxy.getRealValue(result);
        }
    }

    private BeanFactoryContext getBeanFactoryHandler(ExtensionContext context) {
        return context.getRoot()
                .getStore(ExtensionContext.Namespace.GLOBAL)
                .getOrComputeIfAbsent(BeanFactoryContext.class);
    }

    @Override
    public void beforeTestExecution(ExtensionContext context) throws Exception {
        BeanFactoryContext applicationContext = getBeanFactoryHandler(context);
        Optional<Object> instance = context.getTestInstance();
        instance.ifPresent(o -> {
            var clazz = o.getClass();
            List<Field> declaredFields = ReflectionHelper.getDeclaredFields(clazz);
            if (!declaredFields.isEmpty()) {
                for (Field field: declaredFields) {
                    BeanInject beanInject = field.getAnnotation(BeanInject.class);
                    if (beanInject != null) {
                        String name = beanInject.name();
                        if (name.isBlank()) {
                            name = beanInject.value();
                        }

                        Class<?> type = field.getType();

                        Object result;
                        if (name.isBlank()) {
                            result = applicationContext.factory(type);
                        } else {
                            result = applicationContext.factory(name);
                        }

                        if (!type.isInstance(result)) {
                            result = JdkDynamicProxy.getRealValue(result);
                        }

                        ReflectionHelper.setField(o, field, result);
                    } else {
                        @SuppressWarnings("unchecked")
                        Class injectClass = applicationContext.getInjectType();
                        if (injectClass == null) return;
                        @SuppressWarnings("unchecked")
                        Object inject = field.getAnnotation(injectClass);
                        if (inject != null) {
                            Class<?> type = field.getType();

                            Object result = applicationContext.factory(type);

                            if (!type.isInstance(result)) {
                                result = JdkDynamicProxy.getRealValue(result);
                            }

                            ReflectionHelper.setField(o, field, result);
                        }
                    }
                }
            }
        });

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
        BeanFactoryContext applicationContext = applicationFactory.getBeanFactoryHandler();
        ExtensionContext.Store store = context.getRoot().getStore(ExtensionContext.Namespace.GLOBAL);
        store.put(BeanFactoryContext.class, applicationContext);
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
