/**
 * Copyright (c) 2022 TruthBean(Rogar·Q)
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
import com.truthbean.debbie.core.ApplicationContextAware;
import com.truthbean.debbie.env.EnvContentAware;
import com.truthbean.debbie.env.EnvironmentContent;
import com.truthbean.debbie.proxy.BeanProxyHandler;
import com.truthbean.transformer.DataTransformer;
import com.truthbean.debbie.event.DebbieEventPublisherAware;
import com.truthbean.debbie.properties.NestedPropertiesConfiguration;
import com.truthbean.debbie.properties.PropertiesException;
import com.truthbean.debbie.properties.PropertyInject;
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

*/
/**
 * @author TruthBean/Rogar·Q
 * @since 0.1.0
 * Created on 2020-06-23 22:07.
 *//*

class DebbieInjectedBeanFactory implements InjectedBeanFactory {
    private ApplicationContext applicationContext;
    private BeanInfoManager beanInfoManager;
    // private DebbieGlobalBeanFactory globalBeanFactory;
    private BeanProxyHandler beanProxyHandler;

    final Map<FactoryBeanInfo<?>, ReflectionBeanCreator<?>> singletonBeanCreatorMap = new ConcurrentHashMap<>();
    private final Map<FactoryBeanInfo<?>, ReflectionBeanCreator<?>> preparations = new LinkedHashMap<>();

    public void setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
        this.beanInfoManager = applicationContext.getBeanInfoManager();
    }

    */
/*public void setGlobalBeanFactory(DebbieGlobalBeanFactory globalBeanFactory) {
        this.globalBeanFactory = globalBeanFactory;
    }*//*


    public void setBeanProxyHandler(BeanProxyHandler beanProxyHandler) {
        this.beanProxyHandler = beanProxyHandler;
    }

    @Override
    public <T> T factory(FactoryBeanInfo<T> factoryBeanInfo) {
        ReflectionBeanCreator<T> creator = createIfNotExist(factoryBeanInfo, false);
        return factory(creator);
    }

    @Override
    public <T> T factory(FactoryBeanInfo<T> factoryBeanInfo, boolean skipFactory) {
        ReflectionBeanCreator<T> creator = createIfNotExist(factoryBeanInfo, skipFactory);
        return factory(creator);
    }

    @Override
    public <T> T factory(FactoryBeanInfo<T> factoryBeanInfo, boolean skipFactory, Object firstParamValue) {
        ReflectionBeanCreator<T> creator = createIfNotExist(factoryBeanInfo, skipFactory);
        return factory(creator, firstParamValue);
    }

    @Override
    public <T> T factory(ReflectionBeanCreator<T> creator) {
        return factory(creator, null);
    }

    @Override
    public <T> T factory(ReflectionBeanCreator<T> creator, Object firstParamValue) {
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
    public <T> ReflectionBeanCreator<T> factoryBeanPreparation(FactoryBeanInfo<T> factoryBeanInfo, boolean skipFactory) {
        if (singletonBeanCreatorMap.containsKey(factoryBeanInfo)) {
            ReflectionBeanCreator<T> beanCreator = (ReflectionBeanCreator<T>) singletonBeanCreatorMap.get(factoryBeanInfo);
            if (factoryBeanInfo instanceof MutableFactoryBeanInfo) {
                ((MutableFactoryBeanInfo) factoryBeanInfo).setBean(beanCreator.getCreatedBean());
            }
        }
        if (preparations.containsKey(factoryBeanInfo)) {
            return (ReflectionBeanCreator<T>) preparations.get(factoryBeanInfo);
        } else if (factoryBeanInfo instanceof DebbieClassFactoryBeanInfo) {
            DebbieClassFactoryBeanInfo classBeanInfo = (DebbieClassFactoryBeanInfo) factoryBeanInfo;
            BeanCreatorImpl<T> creator = new BeanCreatorImpl<>(classBeanInfo, this.beanInfoManager, beanProxyHandler);
            creator.setApplicationContext(applicationContext);
            if (factoryBeanInfo.isSingleton() && factoryBeanInfo.isPresent()) {
                creator.create(factoryBeanInfo.getBean());
                return creator;
            }
            if (!skipFactory && factoryBeanInfo.hasBeanFactory() && !factoryBeanInfo.hasSkipCreatedBeanFactory()) {
                BeanFactory<T> beanFactory = factoryBeanInfo.getBeanFactory();
                creator.create(beanFactory.factoryBean(applicationContext));
                return creator;
            }
            creator.setInjectedBeanFactory(this);
            this.preparations.put(factoryBeanInfo, creator);
            return creator;
        } else {
            // todo
            return null;
        }
    }

    @Override
    public void resolveFieldValue(FactoryBeanInfo<?> factoryBeanInfo, FieldInfo field, String keyPrefix) {
        var propertyInject = field.getAnnotation(PropertyInject.class);
        if (propertyInject != null) {
            resolvePropertiesInject(factoryBeanInfo.getBean(), field.getField(), keyPrefix, propertyInject);
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
            var b = ReflectionHelper.getField(factoryBeanInfo.getBean(), field.getField());
            Field[] fields = field.getType().getDeclaredFields();
            for (Field f : fields) {
                // 2. 递归获取@NestedPropertiesConfiguration的field的bean
                resolveFieldValue(b, f, newKeyPrefix);
            }
            return;
        }
        Set<Class<? extends Annotation>> injectTypes = beanInfoManager.getInjectTypes();
        for (Class<? extends Annotation> type : injectTypes) {
            Annotation annotation = field.getAnnotation(type);
            if (annotation != null) {
                resolveFieldDependentBean(factoryBeanInfo, field, annotation);
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
    public void resolveAwareValue(ApplicationContext applicationContext, Object object, Class<?> clazz) {
        if (object instanceof ClassLoaderAware) {
            ((ClassLoaderAware) object).setClassLoader(applicationContext.getClassLoader());
        } else if (object instanceof ApplicationContextAware) {
            ((ApplicationContextAware) object).setApplicationContext(applicationContext);
        } if (object instanceof DebbieEventPublisherAware) {
            ((DebbieEventPublisherAware) object).setEventPublisher(applicationContext.factory("eventPublisher"));
        } else if (object instanceof EnvContentAware) {
            ((EnvContentAware) object).setEnvContent(applicationContext.getEnvContent());
        }

    }

    @Override
    public void resolveMethodValue(Object object, Method method) {
        boolean containInject = false;
        Set<Class<? extends Annotation>> injectTypes = beanInfoManager.getInjectTypes();
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
        */
/*Optional<Object> o = globalBeanFactory.factoryIfPresent(name);
        if (o.isPresent()) {
            ReflectionHelper.invokeMethod(object, method, o.get());
            *//*
*/
/*try {
                method.invoke(object, o.get());
            } catch (IllegalAccessException e) {
                LOGGER.error("method (" + method + ") have no access. ", e);
            } catch (InvocationTargetException e) {
                LOGGER.error("method (" + method + ") invoke error. ", e);
            }*//*
*/
/*
        } else {
            var value = globalBeanFactory.factory(beanClass);
            if (value != null) {
                ReflectionHelper.invokeMethod(object, method, value);
                *//*
*/
/*try {
                    method.invoke(object, VALUE);
                } catch (IllegalAccessException e) {
                    LOGGER.error("method (" + method + ") have no access. ", e);
                } catch (InvocationTargetException e) {
                    LOGGER.error("method (" + method + ") invoke error. ", e);
                }*//*
*/
/*
            }
        }*//*

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
    private <Bean> ReflectionBeanCreator<Bean> createIfNotExist(FactoryBeanInfo<Bean> factoryBeanInfo, boolean skipFactory) {
        ReflectionBeanCreator<Bean> creator = null;
        if (singletonBeanCreatorMap.containsKey(factoryBeanInfo)) {
            creator = (ReflectionBeanCreator<Bean>) singletonBeanCreatorMap.get(factoryBeanInfo);
            if (factoryBeanInfo.isEmpty() && factoryBeanInfo instanceof MutableFactoryBeanInfo) {
                ((MutableFactoryBeanInfo) factoryBeanInfo).setBean(creator.getCreatedBean());
            }
        }
        if (creator == null) {
            if (preparations.containsKey(factoryBeanInfo)) {
                creator = (ReflectionBeanCreator<Bean>) preparations.get(factoryBeanInfo);
            } else {
                creator = factoryBeanPreparation(factoryBeanInfo, skipFactory);
            }
            if (factoryBeanInfo.isSingleton()) {
                singletonBeanCreatorMap.put(factoryBeanInfo, creator);
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
    private void resolveFieldDependentBean(FactoryBeanInfo<?> factoryBeanInfo, FieldInfo fieldInfo, Annotation inject) {
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
        if (factoryBeanInfo instanceof DebbieClassFactoryBeanInfo<?> classBeanInfo) {
            var fieldBeanInfoMap = classBeanInfo.getFieldBeanDependent();
            if (fieldBeanInfoMap == null || fieldBeanInfoMap.isEmpty()) {
                if (required) {
                    throw new NoBeanException("no bean " + name + " found .");
                } else {
                    return;
                }
            }
            BeanInfo fieldBeanInfo = fieldBeanInfoMap.get(fieldInfo);
            if (fieldBeanInfo instanceof FactoryBeanInfo factoryBeanInfo1) {
                Object value = null;
                if (factoryBeanInfo1.isPresent()) {
                    value = factoryBeanInfo1.getBean();
                } else if (fieldBeanInfo instanceof MutableFactoryBeanInfo mutableBeanInfo) {
                    ReflectionBeanCreator<?> creator = createIfNotExist(mutableBeanInfo, false);
                    if (creator.isCreated()) {
                        mutableBeanInfo.setBean(creator.getCreatedBean());
                    } else {
                        mutableBeanInfo.setBean(factory(creator));
                    }
                    value = beanProxyHandler.proxyCreatedBean(mutableBeanInfo, mutableBeanInfo.getBean(), mutableBeanInfo.getBeanProxyType());
                }
                ReflectionHelper.invokeFieldBySetMethod(factoryBeanInfo.getBean(), field, value);
            } else if (fieldBeanInfo instanceof BeanFactory beanFactory) {
                Object value = beanFactory.factoryBean(applicationContext);
                ReflectionHelper.invokeFieldBySetMethod(factoryBeanInfo.getBean(), field, value);
            } else {
                if (required) {
                    throw new NoBeanException("no bean " + name + " found .");
                }
            }
        } else if (required) {
            throw new NoBeanException("no bean " + name + " found .");
        } else {
            ReflectionHelper.invokeFieldBySetMethod(factoryBeanInfo.getBean(), field, null);
        }
    }

    @Override
    public void destroy() {
        preparations.clear();
        singletonBeanCreatorMap.clear();
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(DebbieInjectedBeanFactory.class);
}
*/
