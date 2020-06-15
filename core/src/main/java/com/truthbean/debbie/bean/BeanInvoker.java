/**
 * Copyright (c) 2020 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *         http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.bean;

import com.truthbean.debbie.proxy.JdkDynamicProxy;
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
    private final DebbieBeanInfo<Bean> beanInfo;
    private final Map<String, Method> beanMethods = new HashMap<>();

    private Method initMethod;
    private Bean bean;

    public BeanInvoker(Class<Bean> beanClass, BeanFactoryHandler beanFactoryHandler) {
        BeanInitialization initialization = beanFactoryHandler.getBeanInitialization();
        this.beanClass = beanClass;
        var methods = initialization.getBeanMethods(beanClass);
        this.beanInfo = initialization.getRegisterRawBean(beanClass);

        setMethods(methods);
        if (initMethod != null) {
            this.bean = beanFactoryHandler.getBeanByInitMethod(initMethod, beanFactoryHandler);
        }

        if (this.bean == null) {
            this.bean = beanFactoryHandler.getBeanByFactory(beanClass, null);
            if (this.bean == null) {
                createBean(beanFactoryHandler);
            }
        }
    }

    public BeanInvoker(DebbieBeanInfo<Bean> beanInfo, BeanFactoryHandler beanFactoryHandler) {
        this.beanClass = beanInfo.getBeanClass();
        var methods = beanInfo.getMethods();
        this.beanInfo = beanInfo;
        setMethods(methods);
        if (initMethod != null) {
            this.bean = beanFactoryHandler.getBeanByInitMethod(initMethod, beanFactoryHandler);
        }

        if (bean == null)
            this.bean = beanFactoryHandler.getBeanByFactory(beanInfo);
    }

    private void setMethods(Set<Method> methods) {
        methods.forEach(method -> {
            beanMethods.put(method.getName(), method);
            if (method.getAnnotation(BeanInit.class) != null) {
                initMethod = method;
            }
        });
    }

    public static <T> BeanInvoker<T> create(DebbieBeanInfo<T> beanInfo, BeanFactoryHandler factoryHandler,
                                            Map<DebbieBeanInfo<?>, BeanInvoker<?>> singletonBeanInvokerMap) {
        BeanInvoker<T> invoker = new BeanInvoker<>(beanInfo, factoryHandler);
        invoker.resolveConstructor(factoryHandler, singletonBeanInvokerMap);
        return invoker;
    }

    public void resolveConstructor(BeanFactoryHandler factoryHandler,
                                   Map<DebbieBeanInfo<?>, BeanInvoker<?>> singletonBeanInvokerMap) {
        if (this.bean == null) {
            this.resolveConstructorDependent(factoryHandler, singletonBeanInvokerMap);
        }
        this.getFieldsDependent(factoryHandler);
    }

    @SuppressWarnings("unchecked")
    private void resolveConstructorDependent(BeanFactoryHandler beanFactoryHandler,
                                             Map<DebbieBeanInfo<?>, BeanInvoker<?>> singletonBeanInvokerMap) {
        try {
            // get all constructor
            Constructor<Bean>[] constructors = beanInfo.getConstructors();
            createBeanByDefaultConstructor(constructors);

            // if has no Non params constructor
            // find a constructor its all param has BeanInject or Inject annotation
            if (this.bean == null && constructors.length > 0) {
                Constructor<Bean> constructor = constructors[0];
                int parameterCount = constructor.getParameterCount();
                if (parameterCount > 0) {
                    Object[] values = new Object[parameterCount];
                    Map<Integer, DebbieBeanInfo<?>> constructorBeanDependent = new HashMap<>();

                    boolean constructorInjectRequired = false;
                    BeanInject beanInject = constructor.getAnnotation(BeanInject.class);
                    if (beanInject != null) {
                        constructorInjectRequired = beanInject.require();
                    } else {
                        Class injectClass = beanFactoryHandler.getInjectType();
                        if (injectClass != null) {
                            Annotation inject = constructor.getAnnotation(injectClass);
                            if (inject != null) {
                                constructorInjectRequired = true;
                            }
                        }
                    }

                    Parameter[] parameters = constructor.getParameters();
                    String[] names = new String[parameterCount];
                    for (int i = 0; i < parameterCount; i++) {
                        Parameter parameter = parameters[i];

                        var type = parameter.getType();
                        boolean required = constructorInjectRequired;
                        String name = null;
                        BeanInject annotation = parameter.getAnnotation(BeanInject.class);
                        if (annotation != null) {
                            name = annotation.name();
                            if (name.isBlank()) {
                                name = annotation.value();
                            }
                            if (name.isBlank()) {
                                names[i] = type.getName();
                            } else {
                                names[i] = name;
                            }
                            required = annotation.require();
                        }

                        DebbieBeanInfo<?> beanInfo = beanFactoryHandler.getBeanInfo(name, type, required);
                        if (beanInfo == null && required) {
                            throw new NoBeanException("no bean " + names[i] + " found .");
                        } else if (beanInfo != null) {
                            boolean flag = singletonBeanInvokerMap.containsKey(beanInfo);
                            if (flag && beanInfo.getBeanType() == BeanType.SINGLETON) {
                                BeanInvoker<?> beanInvoker = singletonBeanInvokerMap.get(beanInfo);
                                constructorBeanDependent.put(i, beanInvoker.getBeanInfo());
                            } else {
                                constructorBeanDependent.put(i, beanInfo);
                                var bean = beanFactoryHandler.factory(beanInfo);
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
                } else {
                    this.bean = constructor.newInstance();
                }
            }
        } catch (Exception e) {
            LOGGER.error("new instance (" + beanClass.getName() + ") by constructor error \n");
            throwException(e);
        }
    }

    @SuppressWarnings("unchecked")
    void getFieldsDependent(BeanFactoryHandler beanFactoryHandler) {
        try {
            // find all field its has BeanInject or Inject annotation
            if (this.bean != null) {
                List<Field> fields = beanInfo.getFields();
                Map<Field, DebbieBeanInfo<?>> map = new HashMap<>();
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
                            } else if (beanInfo != null) {
                                var bean = beanInfo.getBean();
                                if (bean != null) {
                                    if (fieldType.isInstance(bean)) {
                                        ReflectionHelper.setField(this.bean, field, bean);
                                    } else {
                                        bean = JdkDynamicProxy.getRealValue(bean);
                                        ReflectionHelper.setField(this.bean, field, bean);
                                    }
                                } else {
                                    map.put(field, beanInfo);
                                }
                            }
                        }
                    } else {
                        Class injectClass = beanFactoryHandler.getInjectType();
                        if (injectClass == null) continue;
                        Object inject = field.getAnnotation(injectClass);
                        if (inject != null) {
                            String name = fieldType.getName();
                            var fieldValue = ReflectionHelper.getField(this.bean, field);
                            if (fieldValue == null) {
                                var beanInfo = beanFactoryHandler.getBeanInfo(name, fieldType, true);
                                if (beanInfo == null) {
                                    throw new NoBeanException("no bean " + name + " found .");
                                } else {
                                    var bean = beanInfo.getBean();
                                    if (bean != null) {
                                        if (fieldType.isInstance(bean)) {
                                            ReflectionHelper.setField(this.bean, field, bean);
                                        } else {
                                            bean = JdkDynamicProxy.getRealValue(bean);
                                            ReflectionHelper.setField(this.bean, field, bean);
                                        }
                                    } else {
                                        map.put(field, beanInfo);
                                    }
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
            throwException(e);
        }
    }

    void createBeanByConstructorDependent() {
        Map<Integer, DebbieBeanInfo<?>> beanDependent = this.beanInfo.getConstructorBeanDependent();
        Constructor<Bean>[] constructors = beanInfo.getConstructors();
        if (constructors != null && constructors.length > 0) {
            Constructor<Bean> constructor = constructors[0];
            int parameterCount = constructor.getParameterCount();
            Parameter[] parameters = constructor.getParameters();

            Object[] values = new Object[parameterCount];

            for (int i = 0; i < parameterCount; i++) {
                Parameter parameter = parameters[i];
                BeanInject annotation = parameter.getAnnotation(BeanInject.class);
                if (annotation != null) {
                    var bean = beanDependent.get(i);
                    String serviceName = bean.getServiceName();
                    LOGGER.trace("resolve bean(" + beanClass.getName() + ") constructor dependent " + serviceName);
                    Object beanValue = bean.getBean();
                    if (annotation.require() && !bean.isHasVirtualValue() && beanValue == null) {
                        throw new NoBeanException("bean " + serviceName + " value is null .");
                    }
                    if (beanValue != null) {
                        LOGGER.trace(serviceName + " hashCode: " + beanValue.hashCode());
                        values[i] = beanValue;
                    }
                }
            }
            try {
                this.bean = constructor.newInstance(values);
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException | IllegalArgumentException e) {
                LOGGER.error("new instance (" + beanClass.getName() + ") by constructor error \n");
                LOGGER.trace("", Objects.requireNonNullElse(e.getCause(), e));
            }
        }  else {
            throw new BeanCreatedException("No Constructor can be visited in class(" + beanClass.getName() + ")");
        }
    }

    @SuppressWarnings("unchecked")
    void resolveFieldsDependent(BeanFactoryHandler beanFactoryHandler) {
        Map<Field, DebbieBeanInfo<?>> fieldBeanDependent = this.beanInfo.getFieldBeanDependent();
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
                    } else {
                        Class injectClass = beanFactoryHandler.getInjectType();
                        if (injectClass == null) continue;
                        Object inject = field.getAnnotation(injectClass);
                        if (inject != null) {
                            String name  = fieldType.getName();
                            var fieldValue = ReflectionHelper.getField(this.bean, field);
                            if (fieldValue == null) {
                                DebbieBeanInfo<?> beanInfo = fieldBeanDependent.get(field);
                                if (beanInfo == null) {
                                    throw new NoBeanException("no bean " + name + " found .");
                                } else {
                                    var bean = beanFactoryHandler.factory(beanInfo);
                                    if (bean != null) {
                                        ReflectionHelper.setField(this.bean, field, bean);
                                    } else {
                                        throw new NoBeanException("bean " + getBeanInfo().getServiceName() + " field " + name + " required .");
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            throwException(e);
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
            throwException(e);
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
                            // BeanInject annotation = parameter.getAnnotation(BeanInject.class);
                            // if (annotation != null) {
                            params[i] = beanFactoryHandler.getParameterBean(parameter);
                            // }
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
            throwException(e);
        }
    }

    private void throwException(Exception e) {
        LOGGER.trace("", e);
        Throwable cause = e.getCause();
        var errorMessage = e.getMessage();
        if (cause != null) {
            errorMessage = cause.getMessage();
        } else {
            cause = e;
        }
        if (errorMessage != null)
            throw new BeanCreatedException(errorMessage, cause, true, true);
        else throw new BeanCreatedException("", Objects.requireNonNullElse(cause, e), true, true);
    }

    public Bean getBean() {
        return bean;
    }

    public DebbieBeanInfo<Bean> getBeanInfo() {
        return beanInfo;
    }

    public Object invokeMethod(String methodName, Object[] parameters) {
        var beanMethod = beanMethods.get(methodName);
        return invokeMethod(beanMethod, parameters);
    }

    public Object invokeMethod(Class<? extends Annotation> methodAnnotation, String methodName, Object[] parameters) {
        var beanMethod = beanMethods.get(methodName);
        // TODO: 2019-11-30  methodAnnotation ???
        return invokeMethod(beanMethod, parameters);
    }

    public Object invokeMethod(Method routerMethod, Object[] parameters) {
        return ReflectionHelper.invokeMethod(bean, routerMethod, parameters);
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(BeanInvoker.class);
}
