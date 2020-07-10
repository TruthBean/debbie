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
import com.truthbean.debbie.proxy.jdk.JdkDynamicProxy;
import com.truthbean.debbie.reflection.FieldInfo;
import com.truthbean.debbie.reflection.ReflectionHelper;
import com.truthbean.logger.LoggerFactory;

import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.util.*;
import java.util.function.Supplier;

/**
 * @author TruthBean/Rogar·Q
 * @since 0.1.0
 * Created on 2020-06-23 18:32.
 */
public class BeanCreatorImpl<Bean> implements BeanCreator<Bean> {
    private final Class<Bean> beanClass;
    private final DebbieBeanInfo<Bean> beanInfo;
    // todo inject by method
    // private final Map<String, Method> beanMethods = new HashMap<>();

    private final DebbieBeanInfoFactory debbieBeanInfoFactory;

    private InjectedBeanFactory injectedBeanFactory;

    private boolean preparationCreated;
    private Method initMethod;
    private Bean bean;
    private boolean created;

    public BeanCreatorImpl(Class<Bean> beanClass, DebbieBeanInfoFactory debbieBeanInfoFactory,
                           BeanInitialization initialization) {
        this.debbieBeanInfoFactory = debbieBeanInfoFactory;
        this.beanClass = beanClass;
        var methods = initialization.getBeanMethods(beanClass);
        this.beanInfo = initialization.getRegisterRawBean(beanClass);

        setMethods(methods);
    }

    public BeanCreatorImpl(DebbieBeanInfo<Bean> beanInfo, DebbieBeanInfoFactory debbieBeanInfoFactory) {
        this.debbieBeanInfoFactory = debbieBeanInfoFactory;
        this.beanClass = beanInfo.getBeanClass();
        var methods = beanInfo.getMethods();
        this.beanInfo = beanInfo;
        setMethods(methods);
    }

    @Override
    public void setInjectedBeanFactory(InjectedBeanFactory injectedBeanFactory) {
        this.injectedBeanFactory = injectedBeanFactory;
    }

    @Override
    public DebbieBeanInfo<Bean> getBeanInfo() {
        return beanInfo;
    }

    @Override
    public synchronized void createPreparation(Map<DebbieBeanInfo<?>, BeanCreator<?>> singletonBeanCreatorMap) {
        if (!preparationCreated) {
            if (initMethod != null) {
                LOGGER.trace(() -> "create " + beanClass + " preparation by init method");
                createPreparationByInitMethod(singletonBeanCreatorMap);
            } else {
                LOGGER.trace(() -> "create " + beanClass + " preparation by constructor");
                createPreparationByConstructor(singletonBeanCreatorMap);
            }
            preparationCreated = true;
        }
    }

    public void setCreatedPreparation(Bean bean) {
        this.bean = bean;
        this.preparationCreated = true;
    }

    @Override
    public void createPreparationByDependence() {
        if (this.bean != null) return;

        LOGGER.trace(() -> "creator " + beanClass + " has no raw value");

        if (initMethod != null) {
            LOGGER.trace(() -> "create " + beanClass + " preparation by init method dependence");
            createPreparationByInitMethodDependent();
        } else {
            LOGGER.trace(() -> "create " + beanClass + " preparation by constructor dependence");
            createPreparationByConstructorDependent();
        }
    }

    @Override
    public void postConstructor() {
        this.beanInfo.setBean(bean);
    }

    @Override
    public void postPreparation(Map<DebbieBeanInfo<?>, BeanCreator<?>> singletonBeanCreatorMap) {
        collectFieldsDependent(singletonBeanCreatorMap);
        resolveFieldValue();

        Class<?> clazz = beanInfo.getClazz();
        if (BeanAware.class.isAssignableFrom(clazz)) {
            injectedBeanFactory.resolveAwareValue(injectedBeanFactory, bean, clazz);
        }

        Set<Method> methods = beanInfo.getMethods();
        if (methods != null && !methods.isEmpty()) {
            methods.forEach(method -> injectedBeanFactory.resolveMethodValue(bean, method));
        }
    }

    @Override
    public void postCreated() {
        beanInfo.setBean(bean);
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

    @Override
    public void create(Bean bean) {
        if (bean != null) {
            this.bean = bean;
            this.created = true;
        }
    }

    @Override
    public void create(Supplier<Bean> bean) {
        this.bean = bean.get();
    }

    @SuppressWarnings("unchecked")
    private void createPreparationByInitMethod(Map<DebbieBeanInfo<?>, BeanCreator<?>> singletonBeanCreatorMap) {
        if (Modifier.isStatic(initMethod.getModifiers())) {
            Parameter[] parameters = initMethod.getParameters();
            if (parameters == null || parameters.length == 0) {
                this.bean = (Bean) ReflectionHelper.invokeStaticMethod(initMethod);
            } else {
                int parameterCount = initMethod.getParameterCount();
                Object[] values = new Object[parameters.length];
                Map<Integer, DebbieBeanInfo<?>> initMethodBeanDependence = new HashMap<>();

                boolean initMethodInjectRequired = isInitExecutableInjectRequired(initMethod);

                boolean allParamsHasValue = createPreparationByExecutable(singletonBeanCreatorMap, parameters, parameterCount, values, initMethodBeanDependence, initMethodInjectRequired);
                if (allParamsHasValue) {
                    this.bean = (Bean) ReflectionHelper.invokeStaticMethod(initMethod, values);
                } else {
                    this.beanInfo.setInitMethodBeanDependent(initMethodBeanDependence);
                }
            }
        }
    }

    private boolean createPreparationByExecutable(Map<DebbieBeanInfo<?>, BeanCreator<?>> singletonBeanCreatorMap, Parameter[] parameters, int parameterCount, Object[] values, Map<Integer, DebbieBeanInfo<?>> initMethodBeanDependence, boolean initMethodInjectRequired) {
        String[] names = new String[parameterCount];
        for (int i = 0; i < parameterCount; i++) {
            Parameter parameter = parameters[i];

            var type = parameter.getType();
            boolean required = initMethodInjectRequired;
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

            DebbieBeanInfo<?> beanInfo = debbieBeanInfoFactory.getBeanInfo(name, type, required);
            if (beanInfo == null && required) {
                throw new NoBeanException("no bean " + names[i] + " found .");
            } else if (beanInfo != null) {
                if (beanInfo.isPresent()) {
                    values[i] = beanInfo.getBean();
                }
                if (beanInfo.hasBeanFactory()) {
                    values[i] = beanInfo.getBeanFactory().getBean();
                }
                boolean flag = singletonBeanCreatorMap.containsKey(beanInfo);
                if (flag && beanInfo.isSingleton()) {
                    BeanCreatorImpl<?> beanCreator = (BeanCreatorImpl<?>) singletonBeanCreatorMap.get(beanInfo);
                    initMethodBeanDependence.put(i, beanCreator.beanInfo);
                    if (beanCreator.isCreated()) {
                        values[i] = beanCreator.getCreatedBean();
                    }
                } else {
                    initMethodBeanDependence.put(i, beanInfo);
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
        return allParamsHasValue;
    }

    private boolean isInitExecutableInjectRequired(Executable executable) {
        boolean initExecutableInjectRequired = false;
        BeanInject beanInject = executable.getAnnotation(BeanInject.class);
        if (beanInject != null) {
            initExecutableInjectRequired = beanInject.require();
        } else {
            Class<? extends Annotation> injectClass = injectedBeanFactory.getInjectType();
            if (injectClass != null) {
                Annotation inject = executable.getAnnotation(injectClass);
                if (inject != null) {
                    initExecutableInjectRequired = true;
                }
            }
        }
        return initExecutableInjectRequired;
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

                    boolean constructorInjectRequired = isInitExecutableInjectRequired(constructor);

                    Parameter[] parameters = constructor.getParameters();
                    boolean allParamsHasValue = createPreparationByExecutable(singletonBeanCreatorMap, parameters, parameterCount, values, constructorBeanDependent, constructorInjectRequired);
                    if (allParamsHasValue) {
                        this.bean = constructor.newInstance(values);
                    } else {
                        this.beanInfo.setConstructorBeanDependent(constructorBeanDependent);
                    }
                }
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

    private void createPreparationByInitMethodDependent() {
        Collection<DebbieBeanInfo<?>> initMethodBeanDependent = beanInfo.getInitMethodBeanDependent().values();
        for (DebbieBeanInfo<?> debbieBeanInfo : initMethodBeanDependent) {
            if ((debbieBeanInfo.optional().isEmpty() || debbieBeanInfo.hasNoVirtualValue())) {
                debbieBeanInfo.setHasVirtualValue(true);
                injectedBeanFactory.factory(debbieBeanInfo);
            }
        }
        if (beanInfo.getBean() == null &&
                (beanInfo.getInitMethodBeanDependent().isEmpty() || beanInfo.isInitMethodBeanDependentHasValue())) {
            createRawBeanByInitMethodDependent();
            if (bean != null) {
                beanInfo.setBean(bean);
                beanInfo.setHasVirtualValue(false);
            }
        }
    }

    private void createPreparationByConstructorDependent() {
        Collection<DebbieBeanInfo<?>> constructorBeanDependent = beanInfo.getConstructorBeanDependent().values();
        for (DebbieBeanInfo<?> debbieBeanInfo : constructorBeanDependent) {
            if ((debbieBeanInfo.optional().isEmpty() || debbieBeanInfo.hasNoVirtualValue())) {
                debbieBeanInfo.setHasVirtualValue(true);
                injectedBeanFactory.factory(debbieBeanInfo);
            }
        }
        if (beanInfo.getBean() == null &&
                (beanInfo.getConstructorBeanDependent().isEmpty() || beanInfo.isConstructorBeanDependentHasValue())) {
            createRawBeanByConstructorDependent();
            if (bean != null) {
                beanInfo.setBean(bean);
                beanInfo.setHasVirtualValue(false);
            }
        }
    }

    @SuppressWarnings({"unchecked"})
    private void createRawBeanByInitMethodDependent() {
        Map<Integer, DebbieBeanInfo<?>> beanDependent = this.beanInfo.getInitMethodBeanDependent();
        int parameterCount = initMethod.getParameterCount();
        Parameter[] parameters = initMethod.getParameters();

        Object[] values = new Object[parameterCount];

        boolean initMethodInjectRequired = isInitExecutableInjectRequired(initMethod);

        for (int i = 0; i < parameterCount; i++) {
            Parameter parameter = parameters[i];
            BeanInject annotation = parameter.getAnnotation(BeanInject.class);
            boolean required = initMethodInjectRequired;
            if (annotation != null) {
                required = annotation.require();
            }
            var bean = beanDependent.get(i);
            String serviceName = bean.getServiceName();
            LOGGER.trace(() -> "resolve bean(" + beanClass.getName() + ") by initMethod(" + initMethod.getName() + ") " +
                    "dependent " + serviceName);
            Object beanValue = bean.getBean();
            if (required && bean.hasNoVirtualValue() && beanValue == null) {
                throw new NoBeanException("bean " + serviceName + " value is null .");
            }
            if (beanValue != null) {
                LOGGER.trace(() -> serviceName + " hashCode: " + beanValue.hashCode());
                values[i] = beanValue;
            }
        }
        try {
            this.bean = (Bean) ReflectionHelper.invokeStaticMethod(initMethod, values);
        } catch (Exception e) {
            LOGGER.error(() -> "new instance (" + beanClass.getName() + ") by initMethod(" + initMethod.getName() + ") error \n");
            if (LOGGER.isTraceEnabled())
                LOGGER.trace("", Objects.requireNonNullElse(e.getCause(), e));
        }
    }

    private void createRawBeanByConstructorDependent() {
        Map<Integer, DebbieBeanInfo<?>> beanDependent = this.beanInfo.getConstructorBeanDependent();
        Constructor<Bean>[] constructors = beanInfo.getConstructors();
        if (constructors != null && constructors.length > 0) {
            Constructor<Bean> constructor = constructors[0];
            int parameterCount = constructor.getParameterCount();
            Parameter[] parameters = constructor.getParameters();

            Object[] values = new Object[parameterCount];

            boolean constructorInjectRequired = isInitExecutableInjectRequired(constructor);

            for (int i = 0; i < parameterCount; i++) {
                Parameter parameter = parameters[i];
                var bean = beanDependent.get(i);
                String serviceName = bean.getServiceName();
                LOGGER.trace(() -> "resolve bean(" + beanClass.getName() + ") constructor dependent " + serviceName);

                boolean required = constructorInjectRequired;

                BeanInject annotation = parameter.getAnnotation(BeanInject.class);
                if (annotation != null) {
                    required = annotation.require();
                }
                Object beanValue = bean.getBean();
                if (required && beanValue == null) {
                    throw new NoBeanException("bean " + serviceName + " value is null .");
                }
                if (beanValue != null) {
                    LOGGER.trace(() -> serviceName + " hashCode: " + beanValue.hashCode());
                    values[i] = beanValue;
                }
            }
            try {
                this.bean = constructor.newInstance(values);
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException | IllegalArgumentException e) {
                LOGGER.error(() -> "new instance (" + beanClass.getName() + ") by constructor error \n");
                if (LOGGER.isTraceEnabled())
                    LOGGER.trace("", Objects.requireNonNullElse(e.getCause(), e));
            }
        } else {
            throw new BeanCreatedException("No Constructor can be visited in class(" + beanClass.getName() + ")");
        }
    }

    private void setMethods(Set<Method> methods) {
        methods.forEach(method -> {
            // beanMethods.put(method.getName(), method);
            if (method.getAnnotation(BeanInit.class) != null) {
                initMethod = method;
                beanInfo.setInitMethod(initMethod);
            }
            if (method.getAnnotation(BeanDestroy.class) != null) {
                beanInfo.setDestroyMethod(method);
            }
        });
    }

    private void collectFieldsDependent(Map<DebbieBeanInfo<?>, BeanCreator<?>> singletonBeanCreatorMap) {
        try {
            // find all field its has BeanInject or Inject annotation
            List<FieldInfo> fields = beanInfo.getFields();
            Map<FieldInfo, DebbieBeanInfo<?>> map = new HashMap<>();
            for (FieldInfo fieldInfo : fields) {
                if (fieldInfo.hasValue()) continue;

                Field field = fieldInfo.getField();
                Class<?> fieldType = field.getType();
                String name = null;

                boolean required = false;

                BeanInject annotation = field.getAnnotation(BeanInject.class);
                if (annotation != null) {
                    name = annotation.name();
                    if (name.isBlank()) {
                        name = annotation.value();
                    }
                    if (name.isBlank()) {
                        name = fieldType.getName();
                    }
                    required = annotation.require();
                } else {
                    Set<Class<? extends Annotation>> injectTypes = injectedBeanFactory.getInjectTypes();
                    boolean injected = false;
                    for (Class<? extends Annotation> injectType : injectTypes) {
                        Annotation inject = field.getAnnotation(injectType);
                        if (inject != null) {
                            injected = true;
                            break;
                        }
                    }
                    if (!injected)
                        continue;
                }

                if (name == null || name.isBlank()) {
                    name = field.getName();
                }
                var fieldValue = ReflectionHelper.getField(this.bean, field);
                if (fieldValue == null) {
                    var beanInfo = debbieBeanInfoFactory.getBeanInfo(name, fieldType, required);
                    if (required && beanInfo == null) {
                        throw new NoBeanException("no bean " + name + " found .");
                    } else if (beanInfo != null) {
                        boolean flag = singletonBeanCreatorMap.containsKey(beanInfo);
                        if (flag && beanInfo.isSingleton()) {
                            BeanCreatorImpl<?> beanCreator = (BeanCreatorImpl<?>) singletonBeanCreatorMap.get(beanInfo);
                            map.put(fieldInfo, beanCreator.beanInfo);
                        } else {
                            var bean = beanInfo.getBean();
                            if (bean != null) {
                                if (!fieldType.isInstance(bean)) {
                                    bean = JdkDynamicProxy.getRealValue(bean);
                                }
                                ReflectionHelper.setField(this.bean, field, bean);
                                fieldInfo.setValue();
                            } else {
                                map.put(fieldInfo, beanInfo);
                            }
                        }
                    }
                }
            }

            if (!map.isEmpty()) {
                this.beanInfo.setFieldBeanDependent(map);
            }
        } catch (Exception e) {
            BeanCreatedException.throwException(LOGGER, e);
        }
    }

    private void resolveFieldValue() {
        String keyPrefix = null;

        Map<Class<? extends Annotation>, Annotation> classAnnotations = beanInfo.getClassAnnotations();
        if (classAnnotations.containsKey(PropertiesConfiguration.class)) {
            var beanConfiguration = (PropertiesConfiguration) classAnnotations.get(PropertiesConfiguration.class);
            keyPrefix = beanConfiguration.keyPrefix();
            if (!keyPrefix.isBlank() && !keyPrefix.endsWith(".")) {
                keyPrefix += ".";
            }
        }

        List<FieldInfo> fields = beanInfo.getFields();
        if (fields != null && !fields.isEmpty()) {
            for (FieldInfo field : fields) {
                if (!field.hasValue()) {
                    injectedBeanFactory.resolveFieldValue(beanInfo, field, keyPrefix);
                }
            }
        }
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(BeanCreatorImpl.class);
}
