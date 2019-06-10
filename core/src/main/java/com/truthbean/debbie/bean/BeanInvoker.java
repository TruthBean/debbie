package com.truthbean.debbie.bean;

import com.truthbean.debbie.reflection.ClassInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * @author TruthBean
 * @since 0.0.1
 * Created on 2019/3/23 12:05.
 */
public class BeanInvoker<Bean> {
    private final Class<Bean> beanClass;
    private static final Map<String, Method> BEAN_METHODS = new HashMap<>();

    private Bean bean;
    private ClassInfo classInfo;

    public BeanInvoker(Class<Bean> beanClass, BeanFactoryHandler beanFactoryHandler) {
        this.beanClass = beanClass;
        var methods = BeanRegisterCenter.getBeanMethods(beanClass);
        this.classInfo = BeanRegisterCenter.getRegisterRawBean(beanClass);
        methods.forEach(method -> BEAN_METHODS.put(method.getName(), method));
        createBean(beanFactoryHandler);
    }

    // TODO cache
    private void createBean(BeanFactoryHandler beanFactoryHandler) {
        try {
            // get all constructor
            Constructor<Bean>[] constructors = classInfo.getConstructors();
            // find on params constructor
            for (Constructor<Bean> constructor : constructors) {
                int parameterCount = constructor.getParameterCount();
                if (parameterCount == 0) {
                    this.bean = constructor.newInstance();
                    break;
                }
            }

            // if has no Non params constructor
            // find a constructor its all param has BeanInject or Inject annotation
            if (this.bean == null) {
                for (Constructor<Bean> constructor : constructors) {
                    int parameterCount = constructor.getParameterCount();
                    if (parameterCount > 0) {
                        Parameter[] parameters = constructor.getParameters();
                        Object[] params = new Object[parameterCount];
                        for (int i = 0; i < parameterCount; i++) {
                            Parameter parameter = parameters[i];
                            BeanInject annotation = parameter.getAnnotation(BeanInject.class);
                            if (annotation != null) {
                                params[i] = beanFactoryHandler.getParameterBean(parameter);
                            } else {
                                break;
                            }
                        }
                        this.bean = constructor.newInstance(params);
                    }
                }
            }

            // todo invoke init method

            if (this.bean == null) {
                throw new IllegalAccessException(beanClass.getName() + "'s constructors is not visible. ");
            }

        } catch (Exception e) {
            LOGGER.error("new instance by constructor error \n");
            throw new BeanCreatedException(e);
        }
    }

    public Bean getBean() {
        return bean;
    }

    public ClassInfo getClassInfo() {
        return classInfo;
    }

    public Object invokeMethod(String methodName, Object[] parameters) {
        var beanMethod = BEAN_METHODS.get(methodName);
        return invokeMethod(beanMethod, parameters);
    }

    public Object invokeMethod(Class<? extends Annotation> methodAnnotation, String methodName, Object[] parameters) {
        var beanMethod = BEAN_METHODS.get(methodName);
        return invokeMethod(beanMethod, parameters);
    }

    public Object invokeMethod(Method routerMethod, Object[] parameters) {
        try {
            if (parameters == null || parameters.length == 0) {
                return routerMethod.invoke(bean);
            } else {
                return routerMethod.invoke(bean, parameters);
            }
        } catch (IllegalAccessException | InvocationTargetException e) {
            Throwable cause = e.getCause();
            LOGGER.error("invokeMethod error \n", Objects.requireNonNullElse(cause, e));
        }
        return null;
    }

    public static <Bean> Object invokeMethod(Bean bean, Method routerMethod, Object[] parameters) {
        try {
            if (parameters == null || parameters.length == 0) {
                return routerMethod.invoke(bean);
            } else {
                return routerMethod.invoke(bean, parameters);
            }
        } catch (IllegalAccessException | InvocationTargetException e) {
            Throwable cause = e.getCause();
            LOGGER.error("invokeMethod error \n", Objects.requireNonNullElse(cause, e));
        }
        return null;
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(BeanInvoker.class);
}