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
public class DebbieApplicationExtension extends DebbieApplicationFactory implements ParameterResolver, BeforeTestExecutionCallback, AfterTestExecutionCallback {

    private static final Logger logger = LoggerFactory.getLogger(DebbieApplicationExtension.class.getName());

    private static final String START_TIME = "start time";
    private static final String NAMESPACE = "com.truthbean.debbie.test";

    private static BeanFactoryHandler beanFactoryHandler;

    public DebbieApplicationExtension() {
        if (debbieApplication == null) {
            beanFactoryHandler = getBeanFactoryHandler();
            super.config();
            super.callStarter();
        } else {
            beanFactoryHandler = debbieApplicationFactory;
        }
    }

    @Override
    public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext)
        throws ParameterResolutionException {
        return parameterContext.isAnnotated(BeanInject.class);
    }

    @Override
    public Object resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext)
        throws ParameterResolutionException {
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
        DebbieApplication debbieApplication = DebbieApplicationFactory.factory();
        debbieApplication.start();
        context.getRoot().getStore(ExtensionContext.Namespace.GLOBAL).put(BeanFactoryHandler.class, beanFactoryHandler);

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
        return context.getStore(ExtensionContext.Namespace.create(NAMESPACE, context.getRequiredTestMethod()));
    }
}
