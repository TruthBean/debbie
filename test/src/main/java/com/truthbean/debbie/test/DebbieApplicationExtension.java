package com.truthbean.debbie.test;

import com.truthbean.debbie.bean.BeanFactoryHandler;
import com.truthbean.debbie.bean.BeanInject;
import com.truthbean.debbie.boot.DebbieApplication;
import com.truthbean.debbie.boot.DebbieApplicationFactory;
import org.junit.jupiter.api.extension.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

/**
 * @author truthbean
 * @since 0.0.2
 */
public class DebbieApplicationExtension implements ParameterResolver, BeforeTestExecutionCallback, AfterTestExecutionCallback {

    private static final Logger logger = LoggerFactory.getLogger(DebbieApplicationExtension.class);

    private static final String START_TIME = "start time";
    private static final String NAMESPACE = "com.truthbean.debbie.test";

    @Override
    public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext)
        throws ParameterResolutionException {
        logger.debug("supportsParameter....");
        return parameterContext.isAnnotated(BeanInject.class);
    }

    @Override
    public Object resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext)
        throws ParameterResolutionException {
        logger.debug("resolveParameter....");
        return getDebbieBean(parameterContext.getParameter(), extensionContext);
    }

    private Object getDebbieBean(Parameter parameter, ExtensionContext extensionContext) {
        BeanInject beanInject = parameter.getAnnotation(BeanInject.class);
        String name = beanInject.name();
        if (name.isBlank()) {
            name = beanInject.value();
        }

        Class<?> type = parameter.getType();
        BeanFactoryHandler beanFactoryHandler = extensionContext.getRoot()
            .getStore(ExtensionContext.Namespace.GLOBAL)
            .getOrComputeIfAbsent(BeanFactoryHandler.class);
        if (name.isBlank()) {
            return beanFactoryHandler.factory(type);
        } else {
            return beanFactoryHandler.factory(name);
        }
    }

    @Override
    public void beforeTestExecution(ExtensionContext context) throws Exception {
        logger.debug("beforeTestExecution...");
        DebbieApplicationFactory applicationFactory = new DebbieApplicationFactory();
        DebbieApplication debbieApplication = applicationFactory.createApplication();
        debbieApplication.start();
        BeanFactoryHandler beanFactoryHandler = applicationFactory.getBeanFactoryHandler();
        ExtensionContext.Store store = context.getRoot().getStore(ExtensionContext.Namespace.GLOBAL);
        store.put(BeanFactoryHandler.class, beanFactoryHandler);
        store.put(DebbieApplication.class, debbieApplication);

        getStore(context).put(START_TIME, System.currentTimeMillis());
    }

    @Override
    public void afterTestExecution(ExtensionContext context) throws Exception {
        logger.debug("afterTestExecution...");
        Method testMethod = context.getRequiredTestMethod();
        long startTime = getStore(context).remove(START_TIME, long.class);
        long duration = System.currentTimeMillis() - startTime;

        logger.info(String.format("Method [%s.%s] took %s ms.", testMethod.getDeclaringClass(), testMethod.getName(), duration));

        ExtensionContext.Store store = context.getRoot().getStore(ExtensionContext.Namespace.GLOBAL);
        DebbieApplication debbieApplication = store.getOrComputeIfAbsent(DebbieApplication.class);
        debbieApplication.exit();
    }

    private ExtensionContext.Store getStore(ExtensionContext context) {
        return context.getStore(ExtensionContext.Namespace.create(NAMESPACE, context.getRequiredTestMethod()));
    }
}
