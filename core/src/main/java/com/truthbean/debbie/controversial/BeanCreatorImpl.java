/**
 * Copyright (c) 2024 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 * http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 *//*

package com.truthbean.debbie.controversial;

import com.truthbean.Logger;
import com.truthbean.debbie.bean.*;
import com.truthbean.debbie.core.ApplicationContext;
import com.truthbean.debbie.io.ResourceResolver;
import com.truthbean.debbie.properties.PropertiesConfiguration;
import com.truthbean.debbie.proxy.BeanProxyHandler;
import com.truthbean.debbie.proxy.BeanProxyType;
import com.truthbean.debbie.proxy.jdk.JdkDynamicProxy;
import com.truthbean.debbie.reflection.FieldInfo;
import com.truthbean.debbie.reflection.ReflectionHelper;
import com.truthbean.LoggerFactory;

import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.util.*;
import java.util.function.Supplier;

*/
/**
 * @author TruthBean/Rogar·Q
 * @since 0.1.0
 * Created on 2020-06-23 18:32.
 *//*

class BeanCreatorImpl<Bean> implements ReflectionBeanCreator<Bean> {
    private final Class<Bean> beanClass;
    private DebbieClassFactoryBeanInfo<Bean> beanInfo;
    // todo inject by method
    // private final Map<String, Method> beanMethods = new HashMap<>();

    private final BeanInfoManager beanInfoManager;
    private final BeanProxyHandler beanProxyHandler;

    private DebbieInjectedBeanFactory injectedBeanFactory;
    private ApplicationContext applicationContext;

    private boolean preparationCreated;
    private Method initMethod;
    private Bean bean;
    private boolean created;

    public BeanCreatorImpl(Class<Bean> beanClass,
                           BeanProxyHandler beanProxyHandler, BeanInfoManager beanInfoManager) {
        this.beanInfoManager = beanInfoManager;
        this.beanProxyHandler = beanProxyHandler;
        this.beanClass = beanClass;
        var methods = beanInfoManager.getBeanMethods(beanClass);
        DebbieClassFactoryBeanInfo<Bean> classBeanInfo;
        */
/*var beanInfo = beanInfoManager.getRegisterRawBean(beanClass);
        if (beanInfo instanceof DebbieClassFactoryBeanInfo) {
            classBeanInfo = (DebbieClassFactoryBeanInfo<Bean>) beanInfo;
        } else {
            classBeanInfo = new DebbieClassFactoryBeanInfo<>(beanClass);
        }
        this.beanInfo = classBeanInfo;*//*


        setMethods(methods);
    }

    public BeanCreatorImpl(DebbieClassFactoryBeanInfo<Bean> classBeanInfo,
                           BeanInfoManager beanInfoManager, BeanProxyHandler beanProxyHandler) {
        this.beanInfoManager = beanInfoManager;
        this.beanProxyHandler = beanProxyHandler;
        this.beanClass = classBeanInfo.getBeanClass();
        var methods = classBeanInfo.getMethods();
        this.beanInfo = classBeanInfo;
        setMethods(methods);
    }

    public void setInjectedBeanFactory(DebbieInjectedBeanFactory injectedBeanFactory) {
        this.injectedBeanFactory = injectedBeanFactory;
    }

    public void setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @Override
    public FactoryBeanInfo<Bean> getBeanInfo() {
        return beanInfo;
    }

    @Override
    public synchronized void createPreparation(Map<FactoryBeanInfo<?>, ReflectionBeanCreator<?>> singletonBeanCreatorMap,
                                               Object firstParamValue) {
        if (!preparationCreated) {
            if (initMethod != null) {
                LOGGER.trace(() -> "create " + beanClass + " preparation by init method");
                createPreparationByInitMethod(singletonBeanCreatorMap, firstParamValue);
            } else {
                LOGGER.trace(() -> "create " + beanClass + " preparation by constructor");
                createPreparationByConstructor(singletonBeanCreatorMap, firstParamValue);
            }
            preparationCreated = true;
        }
    }

    public void setCreatedPreparation(Bean bean) {
        this.bean = bean;
        this.preparationCreated = true;
    }

    @Override
    public void createPreparationByDependence(ApplicationContext applicationContext) {
        if (this.bean != null) {
            return;
        }

        LOGGER.trace(() -> "creator " + beanClass + " has no raw value");

        if (initMethod != null) {
            LOGGER.trace(() -> "create " + beanClass + " preparation by init method dependence");
            createPreparationByInitMethodDependent(applicationContext);
        } else {
            LOGGER.trace(() -> "create " + beanClass + " preparation by constructor dependence");
            createPreparationByConstructorDependent(applicationContext);
        }
    }

    @Override
    public void postConstructor() {
        this.beanInfo.setBean(bean);
        if (bean instanceof ConstructPost) {
            ((ConstructPost) bean).postConstruct();
        }
    }

    @Override
    public void postPreparation(Map<FactoryBeanInfo<?>, ReflectionBeanCreator<?>> singletonBeanCreatorMap) {
        collectFieldsDependent(singletonBeanCreatorMap);
        resolveFieldValue();

        Class<?> clazz = beanInfo.getClazz();
        if (Aware.class.isAssignableFrom(clazz)) {
            injectedBeanFactory.resolveAwareValue(applicationContext, bean, clazz);
        }

        Set<Method> methods = beanInfo.getMethods();
        if (methods != null && !methods.isEmpty()) {
            methods.forEach(method -> injectedBeanFactory.resolveMethodValue(bean, method));
        }
    }

    @Override
    public void postCreated() {
        BeanProxyType type = beanInfo.getBeanProxyType();
        if (type != null) {
            this.bean = beanProxyHandler.proxyCreatedBean(beanInfo, beanInfo.getBean(), beanInfo.getBeanProxyType());
        }
        beanInfo.setBean(bean);
        this.created = true;
    }

    @Override
    public Bean getCreatedBean() {
        if (created) {
            if (this.bean != null) {
                return bean;
            } else if (this.beanInfo.isPresent()) {
                return this.beanInfo.getBean();
            }
        }
        return null;
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
        this.created = true;
    }

    @SuppressWarnings("unchecked")
    private void createPreparationByInitMethod(Map<FactoryBeanInfo<?>, ReflectionBeanCreator<?>> singletonBeanCreatorMap,
                                               Object firstParamValue) {
        if (Modifier.isStatic(initMethod.getModifiers())) {
            Parameter[] parameters = initMethod.getParameters();
            if (parameters == null || parameters.length == 0) {
                this.bean = (Bean) ReflectionHelper.invokeStaticMethod(initMethod);
            } else {
                int parameterCount = initMethod.getParameterCount();
                Object[] values = new Object[parameters.length];
                if (firstParamValue != null) {
                    values[0] = firstParamValue;
                    if (values.length == 1) {
                        this.bean = (Bean) ReflectionHelper.invokeStaticMethod(initMethod, values);
                        return;
                    }
                }
                List<BeanExecutableDependence> initMethodBeanDependence = new ArrayList<>();
                if (firstParamValue != null) {
                    DebbieFactoryBeanInfo<Object> info = new DebbieFactoryBeanInfo<>(Object.class);
                    info.setBean(firstParamValue);
                    initMethodBeanDependence.add(new BeanExecutableDependence(0, info, Object.class));
                }

                boolean initMethodInjectRequired = isInitExecutableInjectRequired(initMethod);

                boolean allParamsHasValue = createPreparationByExecutable(singletonBeanCreatorMap, parameters,
                        parameterCount, values, initMethodBeanDependence, initMethodInjectRequired, firstParamValue);
                if (allParamsHasValue) {
                    this.bean = (Bean) ReflectionHelper.invokeStaticMethod(initMethod, values);
                } else {
                    this.beanInfo.setInitMethodBeanDependencies(initMethodBeanDependence);
                }
            }
        }
    }

    private boolean createPreparationByExecutable(Map<FactoryBeanInfo<?>, ReflectionBeanCreator<?>> singletonBeanCreatorMap,
                                                  Parameter[] parameters, int parameterCount, Object[] values,
                                                  List<BeanExecutableDependence> dependence,
                                                  boolean injectRequired, Object firstParameterValue) {
        String[] names = new String[parameterCount];
        int i = firstParameterValue == null ? 0 : 1;
        for (; i < parameterCount; i++) {
            var parameter = parameters[i];

            var type = parameter.getType();
            boolean required = injectRequired;
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

            if ((name == null || name.isBlank()) && parameter.isNamePresent()) {
                name = parameter.getName();
            }

            BeanInfo beanInfo = beanInfoManager.getBeanInfo(name, type, required);
            if (beanInfo == null && required) {
                throw new NoBeanException("no bean " + names[i] + " found .");
            } else if (beanInfo != null) {
                if (beanInfo instanceof FactoryBeanInfo) {
                    FactoryBeanInfo factoryBeanInfo1 = (FactoryBeanInfo) beanInfo;
                    if (factoryBeanInfo1.isPresent()) {
                        values[i] = factoryBeanInfo1.getBean();
                    }
                    if (factoryBeanInfo1.hasBeanFactory()) {
                        BeanFactory<?> beanFactory = factoryBeanInfo1.getBeanFactory();
                        factoryBeanInfo1.create(applicationContext);
                        values[i] = beanFactory.factoryBean(applicationContext);
                    }
                } else if (beanInfo instanceof BeanFactory) {
                    values[i] = ((BeanFactory<?>) beanInfo).factoryBean(applicationContext);
                }

                boolean flag = singletonBeanCreatorMap.containsKey(beanInfo);
                if (flag && beanInfo.isSingleton()) {
                    BeanCreatorImpl<?> beanCreator = (BeanCreatorImpl<?>) singletonBeanCreatorMap.get(beanInfo);
                    dependence.add(new BeanExecutableDependence(i, beanCreator.beanInfo, type));
                    if (beanCreator.isCreated()) {
                        values[i] = beanCreator.getCreatedBean();
                    }
                } else {
                    dependence.add(new BeanExecutableDependence(i, beanInfo, type));
                }
            } else {
                dependence.add(new BeanExecutableDependence(i, null, type));
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
            Class<? extends Annotation> injectClass = beanInfoManager.getInjectType();
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
    private void createPreparationByConstructor(Map<FactoryBeanInfo<?>, ReflectionBeanCreator<?>> singletonBeanCreatorMap,
                                                Object firstParamValue) {
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
                    values[0] = firstParamValue;
                    if (values.length == 1 && firstParamValue != null) {
                        this.bean = constructor.newInstance(values);
                        return;
                    }
                    List<BeanExecutableDependence> constructorBeanDependent = new ArrayList<>();
                    if (firstParamValue != null) {
                        DebbieClassFactoryBeanInfo<Object> info = new DebbieClassFactoryBeanInfo<>(Object.class);
                        info.setBean(firstParamValue);
                        constructorBeanDependent.add(new BeanExecutableDependence(0, info, Object.class));
                    }

                    boolean constructorInjectRequired = isInitExecutableInjectRequired(constructor);

                    Parameter[] parameters = constructor.getParameters();
                    boolean allParamsHasValue = createPreparationByExecutable(singletonBeanCreatorMap, parameters,
                            parameterCount, values, constructorBeanDependent, constructorInjectRequired, firstParamValue);
                    if (allParamsHasValue) {
                        this.bean = constructor.newInstance(values);
                    } else {
                        this.beanInfo.setConstructorBeanDependencies(constructorBeanDependent);
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
                    constructor.setAccessible(true);
                    this.bean = constructor.newInstance();
                    break;
                }
            }
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            LOGGER.error("new instance by constructor error \n");
            BeanCreatedException.throwException(LOGGER, e);
        }
    }

    private void createPreparationByInitMethodDependent(ApplicationContext applicationContext) {
        Collection<BeanExecutableDependence> initMethodBeanDependent = beanInfo.getInitMethodBeanDependencies();
        for (BeanExecutableDependence dependence : initMethodBeanDependent) {
            BeanInfo debbieBeanInfo = dependence.getBeanInfo();
            if (debbieBeanInfo == null) {
                var type = dependence.getType();
                if (type == ApplicationContext.class) {
                    dependence.setValue(applicationContext);
                } else if (type == ResourceResolver.class) {
                    dependence.setValue(applicationContext.getResourceResolver());
                }
                continue;
            }
            if (debbieBeanInfo instanceof MutableFactoryBeanInfo) {
                DebbieClassFactoryBeanInfo<?> mutableBeanInfo = (DebbieClassFactoryBeanInfo<?>) debbieBeanInfo;
                if (mutableBeanInfo.optional().isEmpty() && mutableBeanInfo.hasNoVirtualValue()) {
                    mutableBeanInfo.setVirtualValue(true);
                    // injectedBeanFactory.factory(mutableBeanInfo, false);
                }
            } else if (debbieBeanInfo instanceof FactoryBeanInfo) {
                FactoryBeanInfo factoryBeanInfo1 = (FactoryBeanInfo) debbieBeanInfo;
                if (factoryBeanInfo1.optional().isEmpty()) {
                    injectedBeanFactory.factory(factoryBeanInfo1, false);
                }
            }
        }
        if (beanInfo.getBean() == null &&
                (beanInfo.getInitMethodBeanDependencies().isEmpty() || beanInfo.isInitMethodBeanDependentHasValue())) {
            createRawBeanByInitMethodDependent();
            if (bean != null) {
                beanInfo.setBean(bean);
                beanInfo.setVirtualValue(false);
            }
        }
    }

    private void createPreparationByConstructorDependent(ApplicationContext applicationContext) {
        beanInfo.getCircleDependencyInConstructor();
        Collection<BeanExecutableDependence> constructorBeanDependent = beanInfo.getConstructorBeanDependencies();
        for (BeanExecutableDependence dependence : constructorBeanDependent) {
            var type = dependence.getType();
            var debbieBeanInfo = dependence.getBeanInfo();
            if (debbieBeanInfo == null) {
                if (type == ApplicationContext.class) {
                    dependence.setValue(applicationContext);
                } else if (type == ResourceResolver.class) {
                    dependence.setValue(applicationContext.getResourceResolver());
                }
                continue;
            }
            if (debbieBeanInfo instanceof DebbieClassFactoryBeanInfo) {
                DebbieClassFactoryBeanInfo<?> mutableBeanInfo = (DebbieClassFactoryBeanInfo<?>) debbieBeanInfo;
                if (mutableBeanInfo.optional().isEmpty() && mutableBeanInfo.hasNoVirtualValue()) {
                    mutableBeanInfo.setVirtualValue(true);
                    injectedBeanFactory.factory(mutableBeanInfo, false);
                }
            } else if (debbieBeanInfo instanceof FactoryBeanInfo && ((FactoryBeanInfo)debbieBeanInfo).optional().isEmpty()) {
                injectedBeanFactory.factory((FactoryBeanInfo)debbieBeanInfo, false);
            }
        }
        */
/*if (!fromFactory && beanInfo.getBean() == null && beanInfo.hasBeanFactory()) {
            bean = beanInfo.getBeanFactory().factoryBean();
            beanInfo.setBean(bean);
        }*//*

        if (beanInfo.getBean() == null &&
                (beanInfo.getConstructorBeanDependencies().isEmpty() || beanInfo.isConstructorBeanDependentHasValue())) {
            createRawBeanByConstructorDependent();
            if (bean != null) {
                beanInfo.setBean(bean);
                beanInfo.setVirtualValue(false);
            }
        }
    }

    @SuppressWarnings({"unchecked"})
    private void createRawBeanByInitMethodDependent() {
        List<BeanExecutableDependence> beanDependent = this.beanInfo.getInitMethodBeanDependencies();
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
            var dependence = beanDependent.get(i);
            var bean = dependence.getBeanInfo();
            if (bean != null) {
                String serviceName = bean.getName();
                LOGGER.trace(() -> "resolve bean(" + beanClass.getName() + ") by initMethod(" + initMethod.getName() + ") " +
                        "dependent " + serviceName);
                if (bean instanceof DebbieClassFactoryBeanInfo) {
                    DebbieClassFactoryBeanInfo<?> localBeanInfo = (DebbieClassFactoryBeanInfo<?>) bean;
                    if (required && localBeanInfo.hasNoVirtualValue() && localBeanInfo.isEmpty()) {
                        throw new NoBeanException("bean " + serviceName + " value is null .");
                    }
                } else if (bean instanceof FactoryBeanInfo) {
                    FactoryBeanInfo factoryBeanInfo1 = (FactoryBeanInfo) bean;
                    if (required && factoryBeanInfo1.isEmpty()) {
                        throw new NoBeanException("bean " + serviceName + " value is null .");
                    }
                }
                if (bean instanceof FactoryBeanInfo) {
                    FactoryBeanInfo factoryBeanInfo1 = (FactoryBeanInfo) bean;
                    if (factoryBeanInfo1.isPresent()) {
                        Object beanValue = factoryBeanInfo1.getBean();
                        LOGGER.trace(() -> serviceName + " hashCode: " + beanValue.hashCode());
                        values[i] = beanValue;
                    }
                } else if (bean instanceof BeanFactory) {
                    BeanFactory beanFactory = (BeanFactory) bean;
                    Object beanValue = beanFactory.factoryBean(applicationContext);
                    LOGGER.trace(() -> serviceName + " hashCode: " + beanValue.hashCode());
                    values[i] = beanValue;
                }
            }  else {
                values[i] = ReflectionHelper.getDefaultValue(dependence.getType());
            }
        }
        try {
            this.bean = (Bean) ReflectionHelper.invokeStaticMethod(initMethod, values);
        } catch (Exception e) {
            LOGGER.error(() -> "new instance (" + beanClass.getName() + ") by initMethod(" + initMethod.getName() + ") error \n");
            if (LOGGER.isTraceEnabled()) {
                LOGGER.trace("", Objects.requireNonNullElse(e.getCause(), e));
            }
        }
    }

    private void createRawBeanByConstructorDependent() {
        List<BeanExecutableDependence> beanDependent = this.beanInfo.getConstructorBeanDependencies();
        Constructor<Bean>[] constructors = beanInfo.getConstructors();
        if (constructors != null && constructors.length > 0) {
            Constructor<Bean> constructor = constructors[0];
            int parameterCount = constructor.getParameterCount();
            Parameter[] parameters = constructor.getParameters();

            Object[] values = new Object[parameterCount];

            boolean constructorInjectRequired = isInitExecutableInjectRequired(constructor);

            for (int i = 0; i < parameterCount; i++) {
                Parameter parameter = parameters[i];

                boolean required = constructorInjectRequired;

                BeanInject annotation = parameter.getAnnotation(BeanInject.class);
                if (annotation != null) {
                    required = annotation.require();
                }

                var dependence = beanDependent.get(i);
                var bean = dependence.getBeanInfo();
                if (bean != null) {
                    String serviceName = bean.getName();
                    LOGGER.trace(() -> "resolve bean(" + beanClass.getName() + ") constructor dependent " + serviceName);
                    if (bean instanceof FactoryBeanInfo) {
                        FactoryBeanInfo factoryBeanInfo1 = (FactoryBeanInfo) bean;
                        Object beanValue = factoryBeanInfo1.getBean();
                        if (required && beanValue == null) {
                            throw new NoBeanException("bean " + serviceName + " value is null .");
                        }
                        if (beanValue != null) {
                            LOGGER.trace(() -> serviceName + " hashCode: " + beanValue.hashCode());
                            values[i] = beanValue;
                        }
                    } else if (bean instanceof BeanFactory) {
                        Object beanValue = ((BeanFactory<?>) bean).factoryBean(applicationContext);
                        if (required && beanValue == null) {
                            throw new NoBeanException("bean " + serviceName + " value is null .");
                        }
                        if (beanValue != null) {
                            LOGGER.trace(() -> serviceName + " hashCode: " + beanValue.hashCode());
                            values[i] = beanValue;
                        }
                    }
                } else if (required) {
                    throw new NoBeanException("bean " + parameter.getType() + " value is null .");
                } else {
                    values[i] = ReflectionHelper.getDefaultValue(dependence.getType());
                }
            }
            try {
                this.bean = constructor.newInstance(values);
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException | IllegalArgumentException e) {
                LOGGER.error(() -> "new instance (" + beanClass.getName() + ") by constructor error \n");
                if (LOGGER.isTraceEnabled()) {
                    LOGGER.trace("", Objects.requireNonNullElse(e.getCause(), e));
                }
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

    private void collectFieldsDependent(Map<FactoryBeanInfo<?>, ReflectionBeanCreator<?>> singletonBeanCreatorMap) {
        try {
            // find all field its has BeanInject or Inject annotation
            List<FieldInfo> fields = beanInfo.getFields();
            Map<FieldInfo, BeanInfo<?>> map = new HashMap<>();
            for (FieldInfo fieldInfo : fields) {
                if (fieldInfo.hasValue()) {
                    continue;
                }

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
                    Set<Class<? extends Annotation>> injectTypes = beanInfoManager.getInjectTypes();
                    boolean injected = false;
                    for (Class<? extends Annotation> injectType : injectTypes) {
                        Annotation inject = field.getAnnotation(injectType);
                        if (inject != null) {
                            injected = true;
                            break;
                        }
                    }
                    if (!injected) {
                        continue;
                    }
                }

                if (name == null || name.isBlank()) {
                    name = field.getName();
                }
                var fieldValue = ReflectionHelper.getField(this.bean, field);
                if (fieldValue == null) {
                    var beanInfo = beanInfoManager.getBeanInfo(name, fieldType, required);
                    if (required && beanInfo == null) {
                        throw new NoBeanException("no bean " + name + " found .");
                    } else if (beanInfo != null) {
                        boolean flag = singletonBeanCreatorMap.containsKey(beanInfo);
                        if (flag && beanInfo.isSingleton()) {
                            BeanCreatorImpl<?> beanCreator = (BeanCreatorImpl<?>) singletonBeanCreatorMap.get(beanInfo);
                            map.put(fieldInfo, beanCreator.beanInfo);
                        } else if (beanInfo instanceof FactoryBeanInfo) {
                            var bean = ((FactoryBeanInfo)beanInfo).getBean();
                            if (bean != null) {
                                if (!fieldType.isInstance(bean)) {
                                    bean = JdkDynamicProxy.getRealValue(bean);
                                }
                                ReflectionHelper.invokeFieldBySetMethod(this.bean, field, bean);
                                fieldInfo.setValue();
                            } else {
                                map.put(fieldInfo, beanInfo);
                            }
                        } else {
                            map.put(fieldInfo, beanInfo);
                        }
                    }
                }
            }

            if (!map.isEmpty()) {
                this.beanInfo.setFieldBeanDependencies(map);
            }
        } catch (Exception e) {
            BeanCreatedException.throwException(LOGGER, e);
        }
    }

    private void resolveFieldValue() {
        String keyPrefix = null;

        if (beanInfo.containClassAnnotation(PropertiesConfiguration.class)) {
            var beanConfiguration = beanInfo.getClassAnnotation(PropertiesConfiguration.class);
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
*/
