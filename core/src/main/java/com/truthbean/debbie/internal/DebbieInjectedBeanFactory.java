/**
 * Copyright (c) 2021 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 * http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.internal;

import com.truthbean.Logger;
import com.truthbean.debbie.bean.*;
import com.truthbean.debbie.core.ApplicationContextAware;
import com.truthbean.debbie.env.EnvContentAware;
import com.truthbean.debbie.env.EnvironmentContent;
import com.truthbean.transformer.DataTransformer;
import com.truthbean.debbie.event.DebbieEventPublisherAware;
import com.truthbean.debbie.properties.NestedPropertiesConfiguration;
import com.truthbean.debbie.properties.PropertiesException;
import com.truthbean.debbie.properties.PropertyInject;
import com.truthbean.debbie.proxy.BeanProxyType;
import com.truthbean.debbie.reflection.FieldInfo;
import com.truthbean.debbie.reflection.ReflectionHelper;
import com.truthbean.LoggerFactory;
import com.truthbean.transformer.DataTransformerCenter;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author TruthBean/Rogar·Q
 * @since 0.1.0
 * Created on 2020-06-23 22:07.
 */
class DebbieInjectedBeanFactory implements InjectedBeanFactory {
    private DebbieApplicationContext applicationContext;
    private DebbieBeanInfoFactory beanInfoFactory;
    private DebbieGlobalBeanFactory globalBeanFactory;

    final Map<BeanInfo<?>, BeanCreator<?>> singletonBeanCreatorMap = new ConcurrentHashMap<>();
    private final Map<BeanInfo<?>, BeanCreator<?>> preparations = new LinkedHashMap<>();

    private final Set<Class<? extends Annotation>> injectTypes = new HashSet<>(2);
    private Class<? extends Annotation> injectType;

    DebbieInjectedBeanFactory() {
        injectTypes.add(BeanInject.class);
        injectTypes.add(PropertyInject.class);
        registerInjectType("javax.inject.Inject");
        registerInjectType("jakarta.inject.Inject");
    }

    @SuppressWarnings("unchecked")
    private void registerInjectType(String inject) {
        try {
            Class<?> injectType = Class.forName(inject);
            if (Annotation.class.isAssignableFrom(injectType)) {
                this.injectType = (Class<Annotation>) injectType;
                injectTypes.add(this.injectType);
            }
        } catch (ClassNotFoundException e) {
            LOGGER.info("class " + inject + " not found");
        }
    }

    @Override
    public <A extends Annotation> void registerInjectType(Class<A> injectType) {
        injectTypes.add(injectType);
    }

    @Override
    public void registerInjectType(Set<Class<? extends Annotation>> injectTypes) {
        if (injectTypes != null && !injectTypes.isEmpty()) {
            this.injectTypes.addAll(injectTypes);
        }
    }

    @Override
    public boolean containInjectType(Class<? extends Annotation> annotation) {
        return injectTypes.contains(annotation);
    }

    @Override
    public Set<Class<? extends Annotation>> getInjectTypes() {
        return new HashSet<>(injectTypes);
    }

    @Override
    public Class<? extends Annotation> getInjectType() {
        return this.injectType;
    }

    public void setApplicationContext(DebbieApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
        this.beanInfoFactory = applicationContext.getBeanInfoFactory();
    }

    public void setGlobalBeanFactory(DebbieGlobalBeanFactory globalBeanFactory) {
        this.globalBeanFactory = globalBeanFactory;
    }

    @Override
    public <T> T factory(BeanInfo<T> beanInfo) {
        BeanCreator<T> creator = createIfNotExist(beanInfo, false);
        return factory(creator);
    }

    @Override
    public <T> T factory(BeanInfo<T> beanInfo, boolean skipFactory) {
        BeanCreator<T> creator = createIfNotExist(beanInfo, skipFactory);
        return factory(creator);
    }

    @Override
    public <T> T factory(BeanInfo<T> beanInfo, boolean skipFactory, Object firstParamValue) {
        BeanCreator<T> creator = createIfNotExist(beanInfo, skipFactory);
        return factory(creator, firstParamValue);
    }

    @Override
    public <T> T factory(BeanCreator<T> creator) {
        return factory(creator, null);
    }

    @Override
    public <T> T factory(BeanCreator<T> creator, Object firstParamValue) {
        if (creator.isCreated()) {
            return creator.getCreatedBean();
        }

        creator.createPreparation(singletonBeanCreatorMap, firstParamValue);

        creator.createPreparationByDependence(applicationContext);

        creator.postConstructor();
        creator.postPreparation(singletonBeanCreatorMap);
        creator.postCreated();

        var beanInfo = creator.getBeanInfo();

        preparations.remove(beanInfo);
        return creator.getCreatedBean();
    }

    @Override
    @SuppressWarnings({"unchecked", "rawtypes"})
    public <T> BeanCreator<T> factoryBeanPreparation(BeanInfo<T> beanInfo, boolean skipFactory) {
        if (singletonBeanCreatorMap.containsKey(beanInfo)) {
            BeanCreator<T> beanCreator = (BeanCreator<T>) singletonBeanCreatorMap.get(beanInfo);
            if (beanInfo instanceof MutableBeanInfo) {
                ((MutableBeanInfo) beanInfo).setBean(beanCreator.getCreatedBean());
            }
        }
        if (preparations.containsKey(beanInfo)) {
            return (BeanCreator<T>) preparations.get(beanInfo);
        } else {
            BeanCreatorImpl<T> creator = new BeanCreatorImpl<>(beanInfo, this.beanInfoFactory);
            if (beanInfo.isSingleton() && beanInfo.isPresent()) {
                creator.create(beanInfo.getBean());
                return creator;
            }
            if (!skipFactory && beanInfo.hasBeanFactory() && !beanInfo.hasSkipCreatedBeanFactory()) {
                creator.create(beanInfo.getBeanFactory().factoryBean());
                return creator;
            }
            creator.setInjectedBeanFactory(this);
            this.preparations.put(beanInfo, creator);
            return creator;
        }
    }

    @Override
    public void resolveFieldValue(BeanInfo<?> beanInfo, FieldInfo field, String keyPrefix) {
        var propertyInject = field.getAnnotation(PropertyInject.class);
        if (propertyInject != null) {
            resolvePropertiesInject(beanInfo.getBean(), field.getField(), keyPrefix, propertyInject);
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
            var b = ReflectionHelper.getField(beanInfo.getBean(), field.getField());
            Field[] fields = field.getType().getDeclaredFields();
            for (Field f : fields) {
                // 2. 递归获取@NestedPropertiesConfiguration的field的bean
                resolveFieldValue(b, f, newKeyPrefix);
            }
            return;
        }
        for (Class<? extends Annotation> type : injectTypes) {
            Annotation annotation = field.getAnnotation(type);
            if (annotation != null) {
                resolveFieldDependentBean(beanInfo, field, annotation);
                break;
            }
        }
    }

    private void resolveFieldValue(Object configuration, Field field, String keyPrefix) {
        var propertyInject = field.getAnnotation(PropertyInject.class);
        if (propertyInject != null) {
            resolvePropertiesInject(configuration, field, keyPrefix, propertyInject);
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
                resolveFieldValue(ReflectionHelper.getField(configuration, f), f, newKeyPrefix);
            }
        } else {
            resolvePropertiesInject(configuration, field, keyPrefix, null);
        }
    }

    @Override
    @SuppressWarnings({"unchecked", "rawtypes"})
    public void resolveAwareValue(InjectedBeanFactory injectedBeanFactory, Object object, Class<?> clazz) {
        if (object instanceof BeanAware) {
            BeanAware aware = (BeanAware) object;
            Class<?> beanClass = aware.getBeanClass();

            String name = null;
            Set<Method> setBeanMethods = ReflectionHelper.getDeclaredMethods(clazz, "setBean");
            if (setBeanMethods.size() == 1) {
                Method method = setBeanMethods.iterator().next();
                Parameter[] parameters = method.getParameters();
                if (parameters[0].isNamePresent()) {
                    name = parameters[0].getName();
                }
            }
            var values = globalBeanFactory.getBeanList(beanClass);
            if (!values.isEmpty()) {
                aware.setBeans(values);
                if (name != null) {
                    var beanInfo = this.beanInfoFactory.getBeanInfo(name, beanClass, true);
                    if (beanInfo != null) {
                        BeanCreator<?> value = injectedBeanFactory.factoryBeanPreparation(beanInfo, false);
                        aware.setBean(() -> value);
                    }
                } else if (values.size() == 1) {
                    aware.setBean(values.get(0));
                }
            }
        } else if (object instanceof ClassLoaderAware) {
            ((ClassLoaderAware) object).setClassLoader(applicationContext.getClassLoader());
        } else if (object instanceof ApplicationContextAware) {
            ((ApplicationContextAware) object).setApplicationContext(applicationContext);
        } else if (object instanceof GlobalBeanFactoryAware) {
            ((GlobalBeanFactoryAware) object).setGlobalBeanFactory(globalBeanFactory);
        } else if (object instanceof InjectedBeanFactoryAware) {
            ((InjectedBeanFactoryAware) object).setInjectedBeanFactory(applicationContext.getInjectedBeanFactory());
        } else if (object instanceof DebbieEventPublisherAware) {
            ((DebbieEventPublisherAware) object).setEventPublisher(applicationContext.factory("eventPublisher"));
        } else if (object instanceof EnvContentAware) {
            ((EnvContentAware) object).setEnvContent(applicationContext.getEnvContent());
        }

    }

    @Override
    public void resolveMethodValue(Object object, Method method) {
        boolean containInject = false;
        for (Class<? extends Annotation> type : injectTypes) {
            Annotation inject = method.getAnnotation(type);
            if (inject != null) {
                containInject = true;
                break;
            }
        }
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
        Optional<Object> o = globalBeanFactory.factoryIfPresent(name);
        if (o.isPresent()) {
            ReflectionHelper.invokeMethod(object, method, o.get());
            /*try {
                method.invoke(object, o.get());
            } catch (IllegalAccessException e) {
                LOGGER.error("method (" + method + ") have no access. ", e);
            } catch (InvocationTargetException e) {
                LOGGER.error("method (" + method + ") invoke error. ", e);
            }*/
        } else {
            var value = globalBeanFactory.factory(beanClass);
            if (value != null) {
                ReflectionHelper.invokeMethod(object, method, value);
                /*try {
                    method.invoke(object, value);
                } catch (IllegalAccessException e) {
                    LOGGER.error("method (" + method + ") have no access. ", e);
                } catch (InvocationTargetException e) {
                    LOGGER.error("method (" + method + ") invoke error. ", e);
                }*/
            }
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public Object factoryProperty(Class<?> valueType, String keyPrefix, PropertyInject propertyInject) {
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
        EnvironmentContent envContent = applicationContext.getEnvContent();
        String value = envContent.getValue(key);
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
    private <Bean> BeanCreator<Bean> createIfNotExist(BeanInfo<Bean> beanInfo, boolean skipFactory) {
        BeanCreator<Bean> creator = null;
        if (singletonBeanCreatorMap.containsKey(beanInfo)) {
            creator = (BeanCreator<Bean>) singletonBeanCreatorMap.get(beanInfo);
            if (beanInfo.isEmpty() && beanInfo instanceof MutableBeanInfo) {
                ((MutableBeanInfo) beanInfo).setBean(creator.getCreatedBean());
            }
        }
        if (creator == null) {
            if (preparations.containsKey(beanInfo)) {
                creator = (BeanCreator<Bean>) preparations.get(beanInfo);
            } else {
                creator = factoryBeanPreparation(beanInfo, skipFactory);
            }
            if (beanInfo.isSingleton()) {
                singletonBeanCreatorMap.put(beanInfo, creator);
            }
        }
        return creator;
    }

    private void resolvePropertiesInject(Object object, Field field, String keyPrefix, PropertyInject propertyInject) {
        Object value = factoryProperty(field.getType(), keyPrefix, propertyInject);
        if (value != null) {
            // use setter method to inject filed
            // if setter method not found, inject directly
            ReflectionHelper.invokeFieldBySetMethod(object, field, value);
        }
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private void resolveFieldDependentBean(BeanInfo<?> beanInfo, FieldInfo fieldInfo, Annotation inject) {
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
        LOGGER.trace(() -> "resolve field dependent bean(" + field.getType() + ") by name : " + finalName);
        var required = true;
        if (inject instanceof BeanInject) {
            required = ((BeanInject) inject).require();
        }
        if (beanInfo instanceof DebbieClassBeanInfo) {
            DebbieClassBeanInfo<?> classBeanInfo = (DebbieClassBeanInfo<?>) beanInfo;
            var fieldBeanInfoMap = classBeanInfo.getFieldBeanDependent();
            if (fieldBeanInfoMap == null || fieldBeanInfoMap.isEmpty()) {
                if (required)
                    throw new NoBeanException("no bean " + name + " found .");
                else
                    return;
            }
            BeanInfo fieldBeanInfo = fieldBeanInfoMap.get(fieldInfo);
            if (fieldBeanInfo != null) {
                Object value = null;
                if (fieldBeanInfo.isPresent()) {
                    value = fieldBeanInfo.getBean();
                } else if (fieldBeanInfo instanceof MutableBeanInfo) {
                    MutableBeanInfo mutableBeanInfo = (MutableBeanInfo) fieldBeanInfo;
                    BeanCreator<?> creator = createIfNotExist(mutableBeanInfo, false);
                    if (creator.isCreated()) {
                        mutableBeanInfo.setBean(creator.getCreatedBean());
                    } else {
                        mutableBeanInfo.setBean(factory(creator));
                    }
                    value = globalBeanFactory.factoryAfterCreatedByProxy(mutableBeanInfo, BeanProxyType.ASM);
                }
                ReflectionHelper.invokeFieldBySetMethod(beanInfo.getBean(), field, value);
            } else {
                if (required)
                    throw new NoBeanException("no bean " + name + " found .");
            }
        } else if (required) {
            throw new NoBeanException("no bean " + name + " found .");
        } else {
            ReflectionHelper.invokeFieldBySetMethod(beanInfo.getBean(), field, null);
        }
    }

    @Override
    public void destroy() {
        preparations.clear();
        singletonBeanCreatorMap.clear();
        injectTypes.clear();
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(DebbieInjectedBeanFactory.class);
}
