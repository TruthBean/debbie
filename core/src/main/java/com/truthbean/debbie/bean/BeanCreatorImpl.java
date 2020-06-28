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
import com.truthbean.debbie.properties.PropertiesConfiguration;
import com.truthbean.debbie.reflection.ReflectionHelper;
import com.truthbean.logger.LoggerFactory;

import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author TruthBean/Rogar·Q
 * @since 0.1.0
 * Created on 2020-06-23 18:32.
 */
public class BeanCreatorImpl<Bean> implements BeanCreator<Bean> {
    private final Class<Bean> beanClass;
    private final DebbieBeanInfo<Bean> beanInfo;
    private final Map<String, Method> beanMethods = new HashMap<>();

    private final BeanFactoryHandler beanFactoryHandler;
    private final BeanInitialization beanInitialization;

    private BeanDependenceProcessor beanDependenceProcessor;
    private InjectedBeanFactory injectedBeanFactory;

    private Method initMethod;
    private Bean bean;
    private boolean created;

    public BeanCreatorImpl(Class<Bean> beanClass, BeanFactoryHandler beanFactoryHandler) {
        this.beanFactoryHandler = beanFactoryHandler;
        this.beanInitialization = beanFactoryHandler.getBeanInitialization();
        BeanInitialization initialization = beanFactoryHandler.getBeanInitialization();
        this.beanClass = beanClass;
        var methods = initialization.getBeanMethods(beanClass);
        this.beanInfo = initialization.getRegisterRawBean(beanClass);

        setMethods(methods);
    }

    public BeanCreatorImpl(DebbieBeanInfo<Bean> beanInfo, BeanFactoryHandler beanFactoryHandler) {
        this.beanFactoryHandler = beanFactoryHandler;
        this.beanInitialization = beanFactoryHandler.getBeanInitialization();
        this.beanClass = beanInfo.getBeanClass();
        var methods = beanInfo.getMethods();
        this.beanInfo = beanInfo;
        setMethods(methods);
    }

    @Override
    public void setBeanDependenceProcessor(BeanDependenceProcessor beanDependenceProcessor) {
        this.beanDependenceProcessor = beanDependenceProcessor;
    }

    @Override
    public void setInjectedBeanFactory(InjectedBeanFactory injectedBeanFactory) {
        this.injectedBeanFactory = injectedBeanFactory;
    }

    @Override
    public void createPreparation(Map<DebbieBeanInfo<?>, BeanCreator<?>> singletonBeanCreatorMap) {
        if (initMethod != null) {
            createPreparationByInitMethod(singletonBeanCreatorMap);
        }
        createPreparationByConstructor(singletonBeanCreatorMap);
    }

    @Override
    public void postConstructor() {

    }

    @Override
    public void postPreparation() {
        String keyPrefix = null;

        Map<Class<? extends Annotation>, Annotation> classAnnotations = beanInfo.getClassAnnotations();
        if (classAnnotations.containsKey(PropertiesConfiguration.class)) {
            var beanConfiguration = (PropertiesConfiguration) classAnnotations.get(PropertiesConfiguration.class);
            keyPrefix = beanConfiguration.keyPrefix();
            if (!keyPrefix.isBlank() && !keyPrefix.endsWith(".")) {
                keyPrefix += ".";
            }
        }

        List<Field> fields = beanInfo.getFields();
        if (fields != null && !fields.isEmpty()) {
            String finalKeyPrefix = keyPrefix;
            fields.forEach(field -> beanDependenceProcessor.resolveFieldValue(injectedBeanFactory, bean, field, finalKeyPrefix));
        }

        Class<?> clazz = beanInfo.getClazz();
        if (BeanAware.class.isAssignableFrom(clazz)) {
            beanDependenceProcessor.resolveAwareValue(injectedBeanFactory, bean, clazz);
        }

        Set<Method> methods = beanInfo.getMethods();
        if (methods != null && !methods.isEmpty()) {
            methods.forEach(method -> beanDependenceProcessor.resolveMethodValue(bean, method));
        }
    }

    @Override
    public void postCreated() {
        //
    }

    @Override
    public Bean getCreatedBean() {
        return bean;
    }

    @Override
    public Bean create() {
        return bean;
    }

    @Override
    public boolean isCreated() {
        return created;
    }

    public void setCreated(boolean created) {
        this.created = created;
    }

    public void create(Bean bean) {
        this.bean = bean;
    }

    @SuppressWarnings("unchecked")
    private void createPreparationByInitMethod(Map<DebbieBeanInfo<?>, BeanCreator<?>> singletonBeanCreatorMap) {
        if (Modifier.isStatic(initMethod.getModifiers())) {
            Parameter[] parameters = initMethod.getParameters();
            if (parameters == null || parameters.length == 0) {
                this.bean = (Bean) ReflectionHelper.invokeStaticMethod(initMethod);
            } else {
                // todo
            }
        }
    }

    // TODO cache
    private void createPreparationByConstructor(Map<DebbieBeanInfo<?>, BeanCreator<?>> singletonBeanCreatorMap) {
        try {
            // get all constructor
            Constructor<Bean>[] constructors = beanInfo.getConstructors();
            createPreparationByDefaultConstructor(constructors);
            // find on params constructor

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
                        Class<? extends Annotation> injectClass = beanFactoryHandler.getInjectType();
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
                            boolean flag = singletonBeanCreatorMap.containsKey(beanInfo);
                            if (flag && beanInfo.getBeanType() == BeanType.SINGLETON) {
                                BeanCreatorImpl<?> beanCreator = (BeanCreatorImpl<?>) singletonBeanCreatorMap.get(beanInfo);
                                constructorBeanDependent.put(i, beanCreator.beanInfo);
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

            if (this.bean == null) {
                throw new IllegalAccessException(beanClass.getName() + "'s constructors is not visible. ");
            }

        } catch (Exception e) {
            LOGGER.error(() -> "new instance (" + beanClass.getName() + ") by constructor error \n");
            BeanCreatedException.throwException(LOGGER, e);
        }
    }

    private void createPreparationByDefaultConstructor(Constructor<Bean>[] constructors) {
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
            BeanCreatedException.throwException(LOGGER, e);
        }
    }

    private void setMethods(Set<Method> methods) {
        methods.forEach(method -> {
            beanMethods.put(method.getName(), method);
            if (method.getAnnotation(BeanInit.class) != null) {
                initMethod = method;
            }
        });
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(BeanCreatorImpl.class);
}
