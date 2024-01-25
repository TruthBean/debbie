/**
 * Copyright (c) 2024 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 * http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.bean;

import com.truthbean.Logger;
import com.truthbean.LoggerFactory;
import com.truthbean.core.util.StringUtils;
import com.truthbean.debbie.core.ApplicationContext;
import com.truthbean.debbie.environment.Environment;
import com.truthbean.debbie.environment.EnvironmentDepositoryHolder;
import com.truthbean.debbie.io.ResourceResolver;
import com.truthbean.debbie.properties.*;
import com.truthbean.debbie.proxy.BeanProxyHandler;
import com.truthbean.debbie.proxy.BeanProxyType;
import com.truthbean.debbie.proxy.jdk.JdkDynamicProxy;
import com.truthbean.debbie.reflection.FieldInfo;
import com.truthbean.debbie.reflection.ReflectionHelper;
import com.truthbean.transformer.DataTransformer;
import com.truthbean.transformer.DataTransformerCenter;

import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * bean lifecycle: <br/>
 * 1. getReflectionBeanFactory <br/>
 * <p>
 * 2. getCreatePreparation, eg. static method, constructor and so on <br/>
 * 3. doConstruct, eg. invoke static method, invoke constructor <br/>
 * 4. getPostConstructPreparation eg. special method, field <br/>
 * 4. postConstruct eg. invoke special method(eg. postConstruct, aware), field <br/>
 * 5. doPreCreated eg. do customize processor <br/>
 * 6. getCreatedBean <br/>
 * <p>
 * 7. getBeforeDestructPreparation <br/>
 * 8. doBeforeDestruct <br/>
 * 9. destruct <br/>
 *
 * @author TruthBean
 * @since 0.5.3
 * Created on 2021/12/03 21:49.
 */
public class ReflectionBeanLifecycle extends AbstractBeanLifecycle {

    private final BeanInfoManager beanInfoManager;
    private final BeanProxyHandler beanProxyHandler;
    private final GlobalBeanFactory globalBeanFactory;
    private final ApplicationContext applicationContext;
    private final boolean enableJdkProxy;

    public ReflectionBeanLifecycle(BeanProxyHandler beanProxyHandler, ApplicationContext applicationContext) {
        this.beanInfoManager = applicationContext.getBeanInfoManager();
        this.beanProxyHandler = beanProxyHandler;
        enableJdkProxy = applicationContext.getDefaultEnvironment().getBooleanValue(ClassesScanProperties.JDK_PROXY_ENABLE_KEY, true);
        this.applicationContext = applicationContext;
        this.globalBeanFactory = applicationContext.getGlobalBeanFactory();
    }

    private final Map<BeanFactory<?>, DebbieReflectionBeanFactory<?>> singletonBeanFactoryMap = new ConcurrentHashMap<>();
    private final Map<BeanFactory<?>, DebbieReflectionBeanFactory<?>> preparations = new LinkedHashMap<>();

    @Override
    public boolean support(BeanInfo<?> beanFactory) {
        return beanFactory instanceof DebbieReflectionBeanFactory;
    }

    @Override
    public boolean support(Class<?> clazz) {
        return !clazz.isInterface() && !clazz.isEnum() && !clazz.isPrimitive() && !clazz.isAnnotation()
                && !clazz.isArray()
                && !Modifier.isAbstract(clazz.getModifiers()) && !Modifier.isPrivate(clazz.getModifiers());
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T construct(T object, Object... params) {
        Object firstParamValue = null;
        if (params != null && params.length > 0) {
            firstParamValue = params[0];
        }
        if (firstParamValue instanceof DebbieReflectionBeanFactory<?> beanFactory) {
            prepareIfNotExist(beanFactory);
            this.createPreparation(beanFactory);
            this.createPreparationByDependence(beanFactory);
            this.preparations.put(beanFactory, beanFactory);
            return (T) beanFactory.getPreparedBean();
        }
        return object;
    }

    @SuppressWarnings("unchecked")
    private <Bean> void prepareIfNotExist(DebbieReflectionBeanFactory<Bean> beanFactory) {
        DebbieReflectionBeanFactory<Bean> preparation = null;
        if (singletonBeanFactoryMap.containsKey(beanFactory)) {
            preparation = (DebbieReflectionBeanFactory<Bean>) singletonBeanFactoryMap.get(beanFactory);
            beanFactory.copyFrom(preparation);
        }
        if (preparation == null) {
            if (preparations.containsKey(beanFactory)) {
                preparation = (DebbieReflectionBeanFactory<Bean>) preparations.get(beanFactory);
                beanFactory.copyFrom(preparation);
            } else {
                preparation = beanFactory;
            }
            if (beanFactory.isSingleton()) {
                singletonBeanFactoryMap.put(beanFactory, preparation);
            }
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T postConstruct(T tempValue, Object... params) {
        if (params != null && params.length > 0) {
            Object object = params[0];
            if (object instanceof DebbieReflectionBeanFactory beanFactory) {
                boolean earlyExposure = false;
                if (params.length >= 2 && params[1] instanceof Boolean) {
                    earlyExposure = (boolean) params[1];
                }
                String profile = null;
                if (params.length >= 3 && params[2] instanceof String) {
                    profile = (String) params[2];
                }
                String category = null;
                if (params.length >= 4 && params[3] instanceof String) {
                    category = (String) params[3];
                }
                if (!earlyExposure) {
                    doConstructPost(tempValue);
                    resolveAwareValue(applicationContext, tempValue);
                }
                // this.resolveFields(beanFactory, tempValue, earlyExposure);
                collectFieldsDependent(beanFactory, tempValue);
                if (!earlyExposure) {
                    resolveFieldValue(profile, category, beanFactory, tempValue);
                }
                if (!earlyExposure) {
                    this.resolveMethods(beanFactory, tempValue);
                }
                if (beanFactory.isFieldBeanDependencyHasVirtualValue()) {
                    Map<FieldInfo, BeanInfo<?>> map = beanFactory.getHasVirtualValueFieldBeanDependencies();
                    final Object finalLocalBean = tempValue;
                    map.forEach((fieldInfo, beanInfo) -> {
                        if (beanInfo instanceof BeanFactory<?> fieldBeanFactory) {
                            Object fieldValue = fieldBeanFactory.factoryBean(applicationContext);
                            if (finalLocalBean.getClass().getName().startsWith("jdk.proxy")) {
                                Object obj = getRealValueFromJdkProxy(finalLocalBean);
                                ReflectionHelper.setField(obj, fieldInfo.getField(), fieldValue);
                            } else {
                                ReflectionHelper.setField(finalLocalBean, fieldInfo.getField(), fieldValue);
                            }
                        }
                    });
                }
                return tempValue;
            }
        }
        return tempValue;
    }

    private Object getRealValueFromJdkProxy(Object proxy) {
        while (proxy.getClass().getName().startsWith("jdk.proxy")) {
            proxy = JdkDynamicProxy.getRealValue(proxy);
        }
        return proxy;
    }

    @Override
    public <T, K extends T> T doPreCreated(BeanInfo<K> beanInfo, K bean, Class<T> clazz, BeanProxyType proxyType) {
        if (enableJdkProxy && beanProxyHandler != null) {
            return beanProxyHandler.proxyCreatedBean(beanInfo, bean, clazz, proxyType);
        }
        return bean;
    }

    @Override
    public <Bean> Bean getCreatedBean(Bean bean, Object... params) {
        if (params != null && params.length > 0) {
            Object object = params[0];
            if (object instanceof DebbieReflectionBeanFactory<?> beanFactory) {
                beanFactory.setVirtualValue(false);
                preparations.remove(beanFactory);
            }
        }
        doCreatedPost(bean);
        return bean;
    }

    @Override
    public void doBeforeDestruct(Object bean) {
    }

    @Override
    public void destruct(Object bean) {
        if (bean instanceof BeanClosure) {
            ((BeanClosure) bean).destruct(applicationContext);
        } else if (bean instanceof AutoCloseable) {
            try {
                ((AutoCloseable) bean).close();
            } catch (Exception e) {
                LOGGER.error("bean(" + bean.getClass() + ") destruct error. ", e);
            }
        }
        preparations.clear();
        singletonBeanFactoryMap.clear();
    }

    // @Override
    public synchronized <Bean> void createPreparation(DebbieReflectionBeanFactory<Bean> beanFactory) {
        if (!beanFactory.isPreparationCreated()) {
            if (beanFactory.getInitMethod() != null) {
                LOGGER.trace(() -> "create " + beanFactory.getBeanClass() + " preparation by init method");
                createPreparationByInitMethod(beanFactory);
            } else {
                LOGGER.trace(() -> "create " + beanFactory.getBeanClass() + " preparation by constructor");
                createPreparationByConstructor(beanFactory);
            }
            beanFactory.setPreparationCreated(true);
        }
    }

    // @Override
    public <Bean> void createPreparationByDependence(DebbieReflectionBeanFactory<Bean> beanFactory) {
        if (beanFactory.getPreparedBean() != null) {
            return;
        }

        Class<?> clazz = beanFactory.getClazz();
        LOGGER.trace(() -> "creator " + clazz + " has no raw value");

        if (beanFactory.getInitMethod() != null) {
            LOGGER.trace(() -> "create " + clazz + " preparation by init method dependence");
            createPreparationByInitMethodDependent(beanFactory);
        } else {
            LOGGER.trace(() -> "create " + clazz + " preparation by constructor dependence");
            createPreparationByConstructorDependent(beanFactory);
        }
    }

    // @Override
    public <Bean> void resolveFields(DebbieReflectionBeanFactory<Bean> beanFactory, Bean preparedBean, boolean earlyExposure) {
        collectFieldsDependent(beanFactory, preparedBean);
        resolveFieldValue(null, null, beanFactory, preparedBean);
    }

    public <Bean> void resolveMethods(DebbieReflectionBeanFactory<Bean> beanFactory, Bean preparedBean) {
        Set<Method> methods = beanFactory.getMethods();
        if (methods != null && !methods.isEmpty()) {
            methods.forEach(method -> this.resolveMethodValue(preparedBean, method));
        }
    }

    @SuppressWarnings("unchecked")
    private <Bean> void createPreparationByInitMethod(DebbieReflectionBeanFactory<Bean> beanFactory) {
        Method initMethod = beanFactory.getInitMethod();
        if (Modifier.isStatic(initMethod.getModifiers())) {
            Parameter[] parameters = initMethod.getParameters();
            if (parameters == null || parameters.length == 0) {
                Bean tempValue = (Bean) ReflectionHelper.invokeStaticMethod(initMethod);
                beanFactory.setPreparedBean(tempValue);
            } else {
                int parameterCount = initMethod.getParameterCount();
                Object[] values = new Object[parameters.length];
                List<BeanExecutableDependence> initMethodBeanDependence = new ArrayList<>();

                boolean initMethodInjectRequired = isInitExecutableInjectRequired(initMethod, beanFactory.getBeanProxyType());

                boolean allParamsHasValue = createPreparationByExecutable(parameters,
                        parameterCount, values, initMethodBeanDependence, initMethodInjectRequired);
                if (allParamsHasValue) {
                    Bean tempValue = (Bean) ReflectionHelper.invokeStaticMethod(initMethod, values);
                    beanFactory.setPreparedBean(tempValue);
                } else {
                    beanFactory.setInitMethodBeanDependencies(initMethodBeanDependence);
                }
            }
        }
    }

    private boolean createPreparationByExecutable(Parameter[] parameters, int parameterCount, Object[] values,
                                                  List<BeanExecutableDependence> dependence,
                                                  boolean injectRequired) {
        String[] names = new String[parameterCount];
        for (int i = 0; i < parameterCount; i++) {
            var parameter = parameters[i];

            var type = parameter.getType();
            boolean required = beanInfoManager.isRequired(parameter, injectRequired);
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
            }

            if ((name == null || name.isBlank()) && parameter.isNamePresent()) {
                name = parameter.getName();
            }

            if (name == null || name.isBlank()) {
                name = parameter.getType().getName();
            }

            PropertyInject propertyInject = parameter.getAnnotation(PropertyInject.class);
            if (propertyInject != null) {
                Object property = factoryProperty(type, null, propertyInject);
                dependence.add(new BeanExecutableDependence(i, null, type, name, property));
                continue;
            }

            BeanFactory<?> _beanFactory = beanInfoManager.getBeanFactory(name, type, required);
            if (_beanFactory == null && required) {
                throw new NoBeanException("no bean(" + type + ", " + names[i] + ") found .");
            } else if (_beanFactory != null) {
                boolean flag = singletonBeanFactoryMap.containsKey(_beanFactory);
                if (flag && _beanFactory.isSingleton()) {
                    var beanFactory = singletonBeanFactoryMap.get(_beanFactory);
                    dependence.add(new BeanExecutableDependence(i, beanFactory, type, name));
                    if (beanFactory.isCreated()) {
                        values[i] = beanFactory.factoryBean(applicationContext);
                    }
                } else {
                    if (_beanFactory.isCreated()) {
                        construct(null, _beanFactory);
                    }
                    dependence.add(new BeanExecutableDependence(i, _beanFactory, type, name));
                }
            } else {
                dependence.add(new BeanExecutableDependence(i, null, type, name));
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

    private boolean isInitExecutableInjectRequired(Executable executable, BeanProxyType proxyType) {
        boolean initExecutableInjectRequired;
        if (proxyType == BeanProxyType.ASM || proxyType == BeanProxyType.JAVASSIST) {
            initExecutableInjectRequired = beanInfoManager.isRequired(executable, false);
        } else {
            initExecutableInjectRequired = beanInfoManager.isRequired(executable, true);
        }
        return initExecutableInjectRequired;
    }

    // TODO cache
    private <Bean> void createPreparationByConstructor(DebbieReflectionBeanFactory<Bean> beanFactory) {
        try {
            // get all constructor
            Constructor<Bean>[] constructors = beanFactory.getConstructors();
            Bean bean = createPreparationByDefaultConstructor(constructors);
            // find on params constructor

            // if has no Non params constructor
            // find a constructor its all param has BeanInject or Inject annotation
            if (bean == null && constructors.length > 0) {
                Constructor<Bean> constructor = constructors[0];
                int parameterCount = constructor.getParameterCount();
                if (parameterCount > 0) {
                    Object[] values = new Object[parameterCount];
                    List<BeanExecutableDependence> constructorBeanDependencies = new ArrayList<>();
                    boolean constructorInjectRequired = isInitExecutableInjectRequired(constructor, beanFactory.getBeanProxyType());

                    Parameter[] parameters = constructor.getParameters();
                    boolean allParamsHasValue = createPreparationByExecutable(parameters,
                            parameterCount, values, constructorBeanDependencies, constructorInjectRequired);
                    if (allParamsHasValue) {
                        bean = constructor.newInstance(values);
                        beanFactory.setPreparedBean(bean);
                    } else {
                        beanFactory.setConstructorBeanDependencies(constructorBeanDependencies);
                    }
                }
            } else if (bean != null) {
                beanFactory.setPreparedBean(bean);
            }
        } catch (Exception e) {
            LOGGER.error(() -> "new instance (" + beanFactory.getBeanClass().getName() + ") by constructor error \n");
            BeanCreatedException.throwException(LOGGER, e);
        }
    }

    private <Bean> Bean createPreparationByDefaultConstructor(Constructor<Bean>[] constructors) {
        try {
            // find on params constructor
            for (Constructor<Bean> constructor : constructors) {
                int parameterCount = constructor.getParameterCount();
                if (parameterCount == 0) {
                    constructor.setAccessible(true);
                    return constructor.newInstance();
                }
            }
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            LOGGER.error("new instance by constructor error \n");
            BeanCreatedException.throwException(LOGGER, e);
        }
        return null;
    }

    private <Bean> void createPreparationByInitMethodDependent(DebbieReflectionBeanFactory<Bean> beanFactory) {
        Collection<BeanExecutableDependence> initMethodBeanDependent = beanFactory.getInitMethodBeanDependencies();
        for (BeanExecutableDependence dependence : initMethodBeanDependent) {
            BeanInfo<?> debbieBeanInfo = dependence.getBeanInfo();
            String name = dependence.getName();
            var type = dependence.getType();
            if (debbieBeanInfo == null) {
                if (type == ApplicationContext.class) {
                    dependence.setValue(applicationContext);
                } else if (type == ResourceResolver.class) {
                    dependence.setValue(applicationContext.getResourceResolver());
                }
                continue;
            }
            if (debbieBeanInfo instanceof DebbieReflectionBeanFactory<?> mutableBeanFactory) {
                if (!mutableBeanFactory.isPreparationCreated() || mutableBeanFactory.hasNoVirtualValue()) {
                    mutableBeanFactory.setVirtualValue(true);
                    Object o = mutableBeanFactory.factoryBean(applicationContext);
                    dependence.setValue(o);
                } else if (mutableBeanFactory.isCreated()) {
                    dependence.setValue(mutableBeanFactory.getCreatedBean(applicationContext));
                }
            } else if (debbieBeanInfo instanceof BeanFactory<?> mutableBeanFactory) {
                Object o = mutableBeanFactory.factoryBean(applicationContext);
                dependence.setValue(o);
            }
        }
        if (beanFactory.getPreparedBean() == null &&
                (beanFactory.getInitMethodBeanDependencies().isEmpty() || beanFactory.isInitMethodBeanDependentHasValue())) {
            Bean preparedBean = createRawBeanByInitMethodDependent(beanFactory);
            if (preparedBean != null) {
                beanFactory.setPreparedBean(preparedBean);
                beanFactory.setVirtualValue(false);
            }
        }
    }

    private <Bean> void createPreparationByConstructorDependent(DebbieReflectionBeanFactory<Bean> beanFactory) {
        beanFactory.getCircleDependencyInConstructor();
        Collection<BeanExecutableDependence> constructorBeanDependent = beanFactory.getConstructorBeanDependencies();
        for (BeanExecutableDependence dependence : constructorBeanDependent) {
            var type = dependence.getType();
            String name = dependence.getName();
            var debbieBeanInfo = dependence.getBeanInfo();
            if (debbieBeanInfo == null) {
                if (type == ApplicationContext.class) {
                    dependence.setValue(applicationContext);
                } else if (type == ResourceResolver.class) {
                    dependence.setValue(applicationContext.getResourceResolver());
                }
                continue;
            }
            if (debbieBeanInfo instanceof DebbieReflectionBeanFactory<?> mutableBeanFactory) {
                if (!mutableBeanFactory.isPreparationCreated() && mutableBeanFactory.hasNoVirtualValue()) {
                    mutableBeanFactory.setVirtualValue(true);
                    Object o = mutableBeanFactory.factoryBean(applicationContext);
                    dependence.setValue(o);
                } else if (mutableBeanFactory.isCreated()) {
                    dependence.setValue(mutableBeanFactory.getCreatedBean(applicationContext));
                    mutableBeanFactory.setVirtualValue(false);
                } else if (mutableBeanFactory.isPreparationCreated() && mutableBeanFactory.getPreparedBean() != null) {
                    dependence.setValue(mutableBeanFactory.getPreparedBean());
                    mutableBeanFactory.setVirtualValue(true);
                }
            } else if (debbieBeanInfo instanceof BeanFactory<?> mutableBeanFactory) {
                Object o = mutableBeanFactory.factoryBean(applicationContext);
                dependence.setValue(o);
            }
        }
        if (beanFactory.getPreparedBean() == null &&
                (beanFactory.getConstructorBeanDependencies().isEmpty() || beanFactory.isConstructorBeanDependentHasValue())) {
            Bean preparedBean = createRawBeanByConstructorDependent(beanFactory);
            if (preparedBean != null) {
                beanFactory.setPreparedBean(preparedBean);
                beanFactory.setVirtualValue(false);
            }
        }
    }

    @SuppressWarnings({"unchecked"})
    private <Bean> Bean createRawBeanByInitMethodDependent(DebbieReflectionBeanFactory<Bean> reflectionBeanFactory) {
        List<BeanExecutableDependence> beanDependent = reflectionBeanFactory.getInitMethodBeanDependencies();
        Method initMethod = reflectionBeanFactory.getInitMethod();
        int parameterCount = initMethod.getParameterCount();
        Parameter[] parameters = initMethod.getParameters();

        Object[] values = new Object[parameterCount];

        boolean initMethodInjectRequired = isInitExecutableInjectRequired(initMethod, reflectionBeanFactory.getBeanProxyType());

        for (int i = 0; i < parameterCount; i++) {
            Parameter parameter = parameters[i];
            final boolean required = beanInfoManager.isRequired(parameter, initMethodInjectRequired);
            var dependence = beanDependent.get(i);
            if (dependence.getValue() != null) {
                values[i] = dependence.getValue();
            } else {
                var name = dependence.getName();
                Class<?> type = dependence.getType();
                var bean = dependence.getBeanInfo();
                if (bean != null) {
                    String serviceName = bean.getName();
                    LOGGER.trace(() -> "resolve bean(" + reflectionBeanFactory.getClazz().getName() + ") " +
                            "by initMethod(" + initMethod.getName() + ") " +
                            "dependent " + serviceName);
                    if (bean instanceof DebbieReflectionBeanFactory<?> localBeanFactory) {
                        if (required && localBeanFactory.hasNoVirtualValue()
                                && !(localBeanFactory.isPreparationCreated() || localBeanFactory.isCreated())) {
                            throw new NoBeanException("bean " + serviceName + " value is null .");
                        } else if (localBeanFactory.isCreated()) {
                            values[i] = localBeanFactory.getCreatedBean(applicationContext);
                        } else if (localBeanFactory.isPreparationCreated()) {
                            values[i] = localBeanFactory.getPreparedBean();
                        }
                    } else if (bean instanceof BeanFactory beanFactory) {
                        Object beanValue = beanFactory.factoryBean(applicationContext);
                        LOGGER.trace(() -> serviceName + " hashCode: " + beanValue.hashCode());
                        values[i] = beanValue;
                    }
                } else {
                    values[i] = ReflectionHelper.getDefaultValue(type);
                }
            }
        }
        try {
            return (Bean) ReflectionHelper.invokeStaticMethod(initMethod, values);
        } catch (Exception e) {
            LOGGER.error(() -> "new instance (" + reflectionBeanFactory.getClazz().getName() + ") " +
                    "by initMethod(" + initMethod.getName() + ") error \n");
            if (LOGGER.isTraceEnabled()) {
                LOGGER.trace("", Objects.requireNonNullElse(e.getCause(), e));
            }
        }
        return null;
    }

    private <Bean> Bean createRawBeanByConstructorDependent(DebbieReflectionBeanFactory<Bean> beanFactory) {
        List<BeanExecutableDependence> beanDependent = beanFactory.getConstructorBeanDependencies();
        Constructor<Bean>[] constructors = beanFactory.getConstructors();
        if (constructors != null && constructors.length > 0) {
            Constructor<Bean> constructor = constructors[0];
            int parameterCount = constructor.getParameterCount();
            Parameter[] parameters = constructor.getParameters();

            Object[] values = new Object[parameterCount];

            boolean constructorInjectRequired = isInitExecutableInjectRequired(constructor, beanFactory.getBeanProxyType());

            for (int i = 0; i < parameterCount; i++) {
                Parameter parameter = parameters[i];

                final boolean required = beanInfoManager.isRequired(parameter, constructorInjectRequired);

                var dependence = beanDependent.get(i);
                if (dependence.getValue() != null) {
                    values[i] = dependence.getValue();
                } else {
                    String name = dependence.getName();
                    Class<?> type = dependence.getType();
                    var bean = dependence.getBeanInfo();
                    if (bean != null) {
                        String serviceName = bean.getName();
                        LOGGER.trace(() -> "resolve bean(" + beanFactory.getClazz().getName() + ") " +
                                "constructor dependent " + serviceName);
                        if (bean instanceof DebbieReflectionBeanFactory localBeanFactory) {
                            if (required && localBeanFactory.hasNoVirtualValue()
                                    && !(localBeanFactory.isPreparationCreated() || localBeanFactory.isCreated())) {
                                throw new NoBeanException("bean " + serviceName + " value is null .");
                            } else if (localBeanFactory.isCreated()) {
                                values[i] = localBeanFactory.getCreatedBean(applicationContext);
                            } else if (localBeanFactory.isPreparationCreated()) {
                                values[i] = localBeanFactory.getPreparedBean();
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
                        values[i] = ReflectionHelper.getDefaultValue(type);
                    }
                }
            }
            try {
                return constructor.newInstance(values);
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException | IllegalArgumentException e) {
                LOGGER.error(() -> "new instance (" + beanFactory.getClazz().getName() + ") by constructor error \n");
                if (LOGGER.isTraceEnabled()) {
                    LOGGER.trace("", Objects.requireNonNullElse(e.getCause(), e));
                }
            }
        } else {
            throw new BeanCreatedException("No Constructor can be visited in class(" + beanFactory.getClazz().getName() + ")");
        }
        return null;
    }

    private <Bean> void collectFieldsDependent(DebbieReflectionBeanFactory<Bean> beanFactory, Bean preparedBean) {
        try {
            Class<Bean> beanClass = beanFactory.getClazz();
            // find all field its has BeanInject or Inject annotation
            List<FieldInfo> fields = beanFactory.getFields();
            Map<FieldInfo, BeanFactory<?>> map = new HashMap<>();
            for (FieldInfo fieldInfo : fields) {
                if (fieldInfo.hasValue()) {
                    continue;
                }

                Field field = fieldInfo.getField();
                Class<?> fieldType = field.getType();

                Annotation[] annotations = field.getAnnotations();
                if (annotations == null || annotations.length == 0) {
                    continue;
                }

                Boolean required = beanInfoManager.injectBeanRequiredIfPresent(field, false);
                if (required == null) {
                    continue;
                }

                String name = getFieldName(field);
                var fieldValue = ReflectionHelper.getField(getRealValueFromJdkProxy(preparedBean), field);
                if (fieldValue == null) {
                    var baseBeanFactory = beanInfoManager.getBeanFactory(name, fieldType, required, false);
                    if (required && baseBeanFactory == null) {
                        throw new NoBeanException(beanClass + " need " + fieldType + " with name " + name + " but no bean found .");
                    } else if (baseBeanFactory != null) {
                        boolean contain = singletonBeanFactoryMap.containsKey(baseBeanFactory);
                        if (contain && baseBeanFactory.isSingleton()) {
                            DebbieReflectionBeanFactory<?> reflectionBeanFactory = singletonBeanFactoryMap.get(baseBeanFactory);
                            map.put(fieldInfo, reflectionBeanFactory);
                        } else if (baseBeanFactory.isSingleton()) {
                            construct(null, baseBeanFactory);
                            map.put(fieldInfo, baseBeanFactory);
                        } else {
                            map.put(fieldInfo, baseBeanFactory);
                        }
                    }
                }
            }

            if (!map.isEmpty()) {
                beanFactory.setFieldBeanDependencies(map);
            }
        } catch (Exception e) {
            LOGGER.error("collect " + beanFactory.getBeanClass().getName() + "'s fields dependents error");
            BeanCreatedException.throwException(LOGGER, e);
        }
    }

    private static String getFieldName(Field field) {
        String name = null;
        BeanInject annotation = field.getAnnotation(BeanInject.class);
        if (annotation != null) {
            name = annotation.name();
            if (name.isBlank()) {
                name = annotation.value();
            }
            if (name.isBlank()) {
                name = field.getName();
            }
        }

        if (name == null || name.isBlank()) {
            name = field.getName();
        }
        return name;
    }

    private <Bean> void resolveFieldValue(String profile, String category,DebbieReflectionBeanFactory<Bean> beanFactory, Bean preparedBean) {
        String keyPrefix = null;

        if (beanFactory.containClassAnnotation(PropertiesConfiguration.class)) {
            var beanConfiguration = beanFactory.getClassAnnotation(PropertiesConfiguration.class);
            keyPrefix = beanConfiguration.keyPrefix();
            if (!keyPrefix.isBlank() && !keyPrefix.endsWith(".")) {
                keyPrefix += ".";
            }
        }

        List<FieldInfo> fields = beanFactory.getFields();
        if (fields != null && !fields.isEmpty()) {
            for (FieldInfo field : fields) {
                if (!field.hasValue()) {
                    this.resolveFieldValue(profile, category, preparedBean, beanFactory, field, keyPrefix);
                }
            }
        }
    }

    public void resolveFieldValue(String profile, String category, Object preparedBean, DebbieReflectionBeanFactory<?> beanFactory, FieldInfo field, String keyPrefix) {
        var propertyInject = field.getAnnotation(PropertyInject.class);
        if (propertyInject != null) {
            resolvePropertiesInject(profile, category, preparedBean, field.getField(), keyPrefix, propertyInject);
            return;
        }
        var nestedPropertiesConfiguration = field.getAnnotation(NestedPropertiesConfiguration.class);
        if (nestedPropertiesConfiguration != null) {
            // 1. 拼接 prefix
            var name = field.getField().getName();
            String newKeyPrefix;
            if (keyPrefix.endsWith(".")) {
                newKeyPrefix = keyPrefix + name;
            } else {
                newKeyPrefix = keyPrefix + "." + name;
            }
            var b = ReflectionHelper.getField(preparedBean, field.getField());
            Field[] fields = field.getType().getDeclaredFields();
            for (Field f : fields) {
                // 2. 递归获取@NestedPropertiesConfiguration的field的bean
                resolvePropertyFieldValue(profile, category, b, f, newKeyPrefix);
            }
            return;
        }
        Set<Class<? extends Annotation>> injectTypes = beanInfoManager.getInjectTypes();
        for (Class<? extends Annotation> type : injectTypes) {
            Annotation annotation = field.getAnnotation(type);
            if (annotation != null) {
                resolveFieldDependentBean(preparedBean, beanFactory, field, annotation);
                break;
            }
        }
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private void resolveFieldDependentBean(Object preparedBean, BeanInfo<?> beanInfo, FieldInfo fieldInfo, Annotation inject) {
        String name = null;
        Field field = fieldInfo.getField();
        if (inject instanceof BeanInject) {
            name = ((BeanInject) inject).name();
            if (name.isBlank()) {
                name = ((BeanInject) inject).value();
            }
        }

        if (name == null || name.isBlank()) {
            name = field.getName();
        }

        final String finalName = name;
        fieldInfo.setName(finalName);
        LOGGER.trace(() -> "resolve bean(" + beanInfo.getBeanClass() + ", " + beanInfo.getAllName() + ") field dependent bean(" + field.getType() + ") by name : " + finalName);
        var required = beanInfoManager.injectedRequired(inject, false);
        if (beanInfo instanceof DebbieReflectionBeanFactory reflectionBeanFactory) {
            Map<FieldInfo, BeanFactory> fieldBeanInfoMap = reflectionBeanFactory.getFieldBeanDependencies();
            if (fieldBeanInfoMap == null || fieldBeanInfoMap.isEmpty()) {
                if (required) {
                    throw new NoBeanException("no bean " + name + " found .");
                } else {
                    return;
                }
            }
            BeanFactory fieldBeanFactory = fieldBeanInfoMap.get(fieldInfo);
            if (fieldBeanFactory instanceof DebbieReflectionBeanFactory<?> debbieReflectionBeanFactory) {
                if (!debbieReflectionBeanFactory.isPreparationCreated() || debbieReflectionBeanFactory.getPreparedBean() == null) {
                    debbieReflectionBeanFactory.createPreparedBean(applicationContext);
                    debbieReflectionBeanFactory.setVirtualValue(true);
                }
            }
            Object value = fieldBeanFactory.factoryBean(applicationContext);
            if (required && value == null) {
                if (!(fieldBeanFactory instanceof DebbieReflectionBeanFactory<?>)) {
                    LOGGER.error(() -> "resolve bean(" + beanInfo.getBeanClass() + ", " + beanInfo.getAllName() + ") field dependent bean(" + field.getType() + ") by name : " + finalName);
                    throw new NoBeanException("no bean " + name + " found .");
                }
            } else {
                ReflectionHelper.setField(getRealValueFromJdkProxy(preparedBean), field, value);
            }
        } else if (required) {
            throw new NoBeanException("no bean (" + name + ", " + field.getType() + ") found .");
        }
    }

    private void resolvePropertyFieldValue(String profile, String category, Object configuration, Field field, String keyPrefix) {
        var propertyInject = field.getAnnotation(PropertyInject.class);
        if (propertyInject != null) {
            resolvePropertiesInject(profile, category, configuration, field, keyPrefix, propertyInject);
            return;
        }
        var nestedPropertiesConfiguration = field.getAnnotation(NestedPropertiesConfiguration.class);
        if (nestedPropertiesConfiguration != null) {
            // 1. 拼接 prefix
            var name = field.getName();
            String newKeyPrefix;
            if (keyPrefix.endsWith(".")) {
                newKeyPrefix = keyPrefix + name;
            } else {
                newKeyPrefix = keyPrefix + "." + name;
            }
            Field[] fields = field.getType().getDeclaredFields();
            for (Field f : fields) {
                // 2. 递归获取@NestedPropertiesConfiguration的field的bean
                resolvePropertyFieldValue(profile, category, ReflectionHelper.getField(configuration, f), f, newKeyPrefix);
            }
        } else {
            // 1. 拼接 prefix
            var name = field.getName();
            String newKeyPrefix;
            if (keyPrefix.endsWith(".")) {
                newKeyPrefix = keyPrefix + name;
            } else {
                newKeyPrefix = keyPrefix + "." + name;
            }
            resolvePropertiesInject(profile, category, configuration, field, newKeyPrefix, null);
        }
    }

    private void resolvePropertiesInject(String profile, String category, Object object, Field field, String keyPrefix, PropertyInject propertyInject) {
        Object value = factoryProperty(applicationContext, profile, category, field.getType(), keyPrefix, propertyInject);
        if (value != null) {
            // use setter method to inject filed
            // if setter method not found, inject directly
            ReflectionHelper.invokeFieldBySetMethod(object, field, value);
        }
    }

    public Object factoryProperty(Class<?> valueType, String keyPrefix, PropertyInject propertyInject) {
        return factoryProperty(applicationContext, valueType, keyPrefix, propertyInject);
    }

    public static Object factoryProperty(ApplicationContext applicationContext,
                                         Class<?> valueType, String keyPrefix, PropertyInject propertyInject) {
        return factoryProperty(applicationContext, null, null, valueType, keyPrefix, propertyInject);
    }

    @SuppressWarnings("unchecked")
    public static Object factoryProperty(ApplicationContext applicationContext, String profile, String category,
                                         Class<?> valueType, String keyPrefix, PropertyInject propertyInject) {
        String key = keyPrefix;
        if (propertyInject == null && keyPrefix == null) {
            throw new PropertiesException("Filed has no @PropertyInject or @NestedPropertiesConfiguration");
        }
        if (propertyInject == null) {
            key = keyPrefix;
        } else {
            String propertyInjectProfile = propertyInject.profile();
            String propertyInjectCategory = propertyInject.category();
            if (StringUtils.hasText(propertyInjectProfile)) {
                profile = propertyInjectProfile;
            }
            if (StringUtils.hasText(propertyInjectCategory)) {
                category = propertyInjectCategory;
            }
            String property = propertyInject.value();
            if (!property.isBlank()) {
                if (StringUtils.hasText(category)) {
                    property = category + "." + property;
                }
                if (keyPrefix != null && !keyPrefix.endsWith(".")) {
                    key = keyPrefix + "." + property;
                } else if (keyPrefix != null) {
                    key = keyPrefix + property;
                } else {
                    key = property;
                }
            }
        }
        Environment environment;
        EnvironmentDepositoryHolder environmentHolder = applicationContext.getEnvironmentHolder();
        if (StringUtils.hasText(profile)) {
            environment = environmentHolder.getEnvironmentIfPresent(profile);
            if (environment == null) {
                environment = applicationContext.getDefaultEnvironment();
            }
        } else {
            environment = applicationContext.getDefaultEnvironment();
        }
        String value = environment.getValue(key);
        LOGGER.trace("environment(" + profile + ") --- "  + key + ": " + value);
        if (value == null && propertyInject != null) {
            value = propertyInject.defaultValue();
        }
        if (value == null) {
            return null;
        }
        if (!CharSequence.class.isAssignableFrom(valueType) && value.trim().isEmpty()) {
            return null;
        }
        Object transform = null;
        if (propertyInject != null) {
            Class<? extends DataTransformer<?, String>> transformerClass = propertyInject.transformer();
            try {
                DataTransformer<?, String> transformer = DataTransformerCenter.getTransformer(transformerClass);
                if (transformer != null) {
                    transform = transformer.reverse(value);
                } else if (transformerClass.isEnum()) {
                    DataTransformer<?, String>[] dataTransformers = transformerClass.getEnumConstants();
                    DataTransformer<?, String> dataTransformer = dataTransformers[0];
                    transform = dataTransformer.reverse(value);
                } else {
                    DataTransformer<?, String> dataTransformer = ReflectionHelper.newInstance(transformerClass);
                    if (dataTransformer != null) {
                        transform = dataTransformer.reverse(value);
                    } else {
                        dataTransformer = (DataTransformer<?, String>) ReflectionHelper.invokeStaticMethod("getInstance", transformerClass);
                        if (dataTransformer != null) {
                            transform = dataTransformer.reverse(value);
                        }
                    }
                }
            } catch (Exception e) {
                LOGGER.error("", e);
            }
        }
        if (transform == null || !valueType.isAssignableFrom(transform.getClass())) {
            transform = applicationContext.transform(value, valueType);
        }
        return transform;
    }

    @SuppressWarnings({"unchecked"})
    private Object resolvePropertyValue(ApplicationContext applicationContext,
                                        Class<?> valueType, String keyPrefix, PropertyInject propertyInject) {
        String key = keyPrefix;
        if (propertyInject == null && keyPrefix == null) {
            throw new PropertiesException("Filed has no @PropertyInject or @NestedPropertiesConfiguration");
        }
        if (propertyInject == null) {
            key = keyPrefix;
        } else {
            String property = propertyInject.value();
            if (!property.isBlank()) {
                if (keyPrefix != null && !keyPrefix.endsWith(".")) {
                    key = keyPrefix + "." + property;
                } else if (keyPrefix != null) {
                    key = keyPrefix + property;
                } else {
                    key = property;
                }
            }
        }
        Environment environment = applicationContext.getDefaultEnvironment();
        String value = environment.getValue(key);
        if (value == null && propertyInject != null) {
            value = propertyInject.defaultValue();
        }
        if (value == null) {
            return null;
        }
        if (!CharSequence.class.isAssignableFrom(valueType) && value.trim().isEmpty()) {
            return null;
        }
        Object transform = null;
        if (propertyInject != null) {
            Class<? extends DataTransformer<?, String>> transformerClass = propertyInject.transformer();
            try {
                DataTransformer<?, String> transformer = DataTransformerCenter.getTransformer(transformerClass);
                if (transformer != null) {
                    transform = transformer.reverse(value);
                } else if (transformerClass.isEnum()) {
                    DataTransformer<?, String>[] dataTransformers = transformerClass.getEnumConstants();
                    DataTransformer<?, String> dataTransformer = dataTransformers[0];
                    transform = dataTransformer.reverse(value);
                } else {
                    DataTransformer<?, String> dataTransformer = ReflectionHelper.newInstance(transformerClass);
                    if (dataTransformer != null) {
                        transform = dataTransformer.reverse(value);
                    } else {
                        dataTransformer = (DataTransformer<?, String>) ReflectionHelper.invokeStaticMethod("getInstance", transformerClass);
                        if (dataTransformer != null) {
                            transform = dataTransformer.reverse(value);
                        }
                    }
                }
            } catch (Exception e) {
                getLogger().error("resolvePropertyValue error. ", e);
            }
        }
        if (transform == null || !valueType.isAssignableFrom(transform.getClass())) {
            transform = applicationContext.transform(value, valueType);
        }
        return transform;
    }

    public void resolveMethodValue(Object object, Method method) {
        boolean containInject = beanInfoManager.hasInjectType(method, false);
        if (!containInject) {
            return;
        }
        if (method.getParameterCount() != 1) {
            LOGGER.error("BeanInject method(" + method + ") must have only one parameter");
            return;
        }
        String name = null;
        Parameter[] parameters = method.getParameters();
        Parameter parameter = parameters[0];
        Class<?> beanClass = parameter.getType();
        if (parameter.isNamePresent()) {
            name = parameter.getName();
        }
        boolean required = beanInfoManager.isRequired(method, false);
        Object o = globalBeanFactory.factory(name, beanClass, required);
        if (o != null) {
            ReflectionHelper.invokeMethod(object, method, o);
        }
    }

    @Override
    protected Logger getLogger() {
        return LOGGER;
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(ReflectionBeanLifecycle.class);
}
