package com.truthbean.debbie.bean;

import com.truthbean.debbie.reflection.ReflectionHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.util.*;

/**
 * @author TruthBean
 * @since 0.0.1
 * Created on 2019/3/23 12:05.
 */
public class BeanInvoker<Bean> {
    private final Class<Bean> beanClass;
    private static final Map<String, Method> BEAN_METHODS = new HashMap<>();

    private Bean bean;
    private DebbieBeanInfo<Bean> beanInfo;

    public BeanInvoker(Class<Bean> beanClass, BeanFactoryHandler beanFactoryHandler) {
        this.beanClass = beanClass;
        var methods = BeanRegisterCenter.getBeanMethods(beanClass);
        this.beanInfo = BeanRegisterCenter.getRegisterRawBean(beanClass);
        methods.forEach(method -> BEAN_METHODS.put(method.getName(), method));
        createBean(beanFactoryHandler);
    }

    public BeanInvoker(DebbieBeanInfo<Bean> beanInfo, BeanFactoryHandler beanFactoryHandler) {
        this.beanClass = beanInfo.getBeanClass();
        var methods = beanInfo.getMethods();
        this.beanInfo = beanInfo;
        methods.forEach(method -> BEAN_METHODS.put(method.getName(), method));

        resolveConstructorDependent(beanFactoryHandler);
        getFieldsDependent(beanFactoryHandler);
    }

    private void resolveConstructorDependent(BeanFactoryHandler beanFactoryHandler) {
        try {
            // get all constructor
            Constructor<Bean>[] constructors = beanInfo.getConstructors();
            createBeanByDefaultConstructor(constructors);

            // if has no Non params constructor
            // find a constructor its all param has BeanInject or Inject annotation
            if (this.bean == null) {
                Constructor<Bean> constructor = constructors[0];
                int parameterCount = constructor.getParameterCount();
                if (parameterCount > 0) {
                    Object[] values = new Object[parameterCount];
                    Map<Integer, DebbieBeanInfo> constructorBeanDependent = new HashMap<>();

                    Parameter[] parameters = constructor.getParameters();
                    String[] names = new String[parameterCount];
                    for (int i = 0; i < parameterCount; i++) {
                        Parameter parameter = parameters[i];
                        BeanInject annotation = parameter.getAnnotation(BeanInject.class);
                        if (annotation != null) {
                            String name = annotation.name();
                            if (name.isBlank()) {
                                name = annotation.value();
                            }
                            if (name.isBlank()) {
                                names[i] = parameter.getType().getName();
                            } else {
                                names[i] = name;
                            }
                            DebbieBeanInfo<?> beanInfo = beanFactoryHandler.getBeanInfo(name, parameter.getType(), annotation.require());
                            if (beanInfo == null && annotation.require()) {
                                throw new NoBeanException("no bean " + names[i] + " found .");
                            } else if (beanInfo != null) {
                                constructorBeanDependent.put(i, beanInfo);
                                var bean = beanInfo.getBean();
                                if (bean != null) {
                                    values[i] = bean;
                                }
                            }
                        }
                    }
                    boolean allParamsHasValue = false;
                    for (Object value : values) {
                        if (value == null) {
                            allParamsHasValue = false;
                            break;
                        } else {
                            allParamsHasValue = true;
                        }
                    }
                    if (allParamsHasValue) {
                        this.bean = constructor.newInstance(values);
                    } else {
                        this.beanInfo.setConstructorBeanDependent(constructorBeanDependent);
                    }
                }
            }
        } catch (Exception e) {
            LOGGER.error("new instance (" + beanClass.getName() + ") by constructor error \n");
            Throwable cause = e.getCause();
            var errorMessage = e.getMessage();
            if (cause != null) {
                errorMessage = cause.getMessage();
            }
            throw new BeanCreatedException(errorMessage);
        }
    }

    void getFieldsDependent(BeanFactoryHandler beanFactoryHandler) {
        try {
            // find all field its has BeanInject or Inject annotation
            if (this.bean != null) {
                List<Field> fields = beanInfo.getFields();
                Map<Field, DebbieBeanInfo> map = new HashMap<>();
                for (Field field : fields) {
                    Class<?> fieldType = field.getType();
                    BeanInject annotation = field.getAnnotation(BeanInject.class);
                    if (annotation != null) {
                        String name = annotation.name();
                        if (name.isBlank()) {
                            name = annotation.value();
                        }
                        if (name.isBlank()) {
                            name = fieldType.getName();
                        }
                        var fieldValue = ReflectionHelper.getField(this.bean, field);
                        if (fieldValue == null) {
                            var beanInfo = beanFactoryHandler.getBeanInfo(name, fieldType, annotation.require());
                            if (annotation.require() && beanInfo == null) {
                                throw new NoBeanException("no bean " + name + " found .");
                            } else {
                                var bean = beanInfo.getBean();
                                if (bean != null) {
                                    ReflectionHelper.setField(this.bean, field, bean);
                                } else {
                                    map.put(field, beanInfo);
                                }
                            }
                        }
                    }
                }

                if (!map.isEmpty()) {
                    this.beanInfo.setFieldBeanDependent(map);
                }
            }
        } catch (Exception e) {
            Throwable cause = e.getCause();
            var errorMessage = e.getMessage();
            if (cause != null) {
                errorMessage = cause.getMessage();
            }
            throw new BeanCreatedException(errorMessage);
        }
    }

    void createBeanByConstructorDependent() {
        Map<Integer, DebbieBeanInfo> beanDependent = this.beanInfo.getConstructorBeanDependent();
        Constructor<Bean> constructor = beanInfo.getConstructors()[0];
        int parameterCount = constructor.getParameterCount();
        Parameter[] parameters = constructor.getParameters();

        Object[] values = new Object[parameterCount];

        for (int i = 0; i < parameterCount; i++) {
            Parameter parameter = parameters[i];
            BeanInject annotation = parameter.getAnnotation(BeanInject.class);
            if (annotation != null) {
                var bean = beanDependent.get(i);
                LOGGER.debug("resolve bean(" + beanClass.getName() + ") constructor dependent " + bean.getServiceName());
                if (annotation.require() && !bean.isHasVirtualValue() && bean.getBean() == null) {
                    throw new NoBeanException("bean " + bean.getServiceName() + " value is null .");
                }
                if (bean.getBean() != null) {
                    values[i] = bean.getBean();
                }
            }
        }
        try {
            this.bean = constructor.newInstance(values);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            LOGGER.error("new instance (" + beanClass.getName() + ") by constructor error \n");
        }
    }

    void resolveFieldsDependent(BeanFactoryHandler beanFactoryHandler) {
        Map<Field, DebbieBeanInfo> fieldBeanDependent = this.beanInfo.getFieldBeanDependent();
        try {
            // find all field its has BeanInject or Inject annotation
            if (this.bean != null) {
                List<Field> fields = beanInfo.getFields();
                for (Field field : fields) {
                    Class<?> fieldType = field.getType();
                    BeanInject annotation = field.getAnnotation(BeanInject.class);
                    if (annotation != null) {
                        String name = annotation.name();
                        if (name.isBlank()) {
                            name = annotation.value();
                        }
                        if (name.isBlank()) {
                            name = fieldType.getName();
                        }
                        var fieldValue = ReflectionHelper.getField(this.bean, field);
                        if (fieldValue == null) {
                            DebbieBeanInfo<?> beanInfo = fieldBeanDependent.get(field);
                            if (beanInfo == null) {
                                throw new NoBeanException("no bean " + name + " found .");
                            } else {
                                var bean = beanFactoryHandler.factory(beanInfo);
                                if (bean != null) {
                                    ReflectionHelper.setField(this.bean, field, bean);
                                } else if (annotation.require()) {
                                    throw new NoBeanException("bean " + getBeanInfo().getServiceName() + " field " + name + " required .");
                                }
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            Throwable cause = e.getCause();
            var errorMessage = e.getMessage();
            if (cause != null) {
                errorMessage = cause.getMessage();
            }
            throw new BeanCreatedException(errorMessage);
        }
    }

    private void createBeanByDefaultConstructor(Constructor<Bean>[] constructors) {
        try {
            // find on params constructor
            for (Constructor<Bean> constructor : constructors) {
                int parameterCount = constructor.getParameterCount();
                if (parameterCount == 0) {
                    this.bean = constructor.newInstance();
                    break;
                }
            }
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            LOGGER.error("new instance by constructor error \n");
            Throwable cause = e.getCause();
            var errorMessage = e.getMessage();
            if (cause != null) {
                errorMessage = cause.getMessage();
            }
            throw new BeanCreatedException(errorMessage);
        }
    }

    // TODO cache
    private void createBean(BeanFactoryHandler beanFactoryHandler) {
        try {
            // get all constructor
            Constructor<Bean>[] constructors = beanInfo.getConstructors();
            createBeanByDefaultConstructor(constructors);
            // find on params constructor

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
            Throwable cause = e.getCause();
            var errorMessage = e.getMessage();
            if (cause != null) {
                errorMessage = cause.getMessage();
            }
            throw new BeanCreatedException(errorMessage);
        }
    }

    public Bean getBean() {
        return bean;
    }

    public DebbieBeanInfo<Bean> getBeanInfo() {
        return beanInfo;
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
        return invokeMethod(bean, routerMethod, parameters);
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
