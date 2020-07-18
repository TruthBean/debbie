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
import com.truthbean.debbie.data.transformer.DataTransformer;
import com.truthbean.debbie.properties.BaseProperties;
import com.truthbean.debbie.properties.PropertyInject;
import com.truthbean.debbie.proxy.BeanProxyType;
import com.truthbean.debbie.reflection.FieldInfo;
import com.truthbean.debbie.reflection.ReflectionHelper;
import com.truthbean.logger.LoggerFactory;

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
public class InjectedBeanFactory implements BeanFactoryContextAware, GlobalBeanFactoryAware {
    private DebbieApplicationContext applicationContext;
    private DebbieBeanInfoFactory beanInfoFactory;
    private GlobalBeanFactory globalBeanFactory;

    final Map<DebbieBeanInfo<?>, BeanCreator<?>> singletonBeanCreatorMap = new ConcurrentHashMap<>();
    private final Map<DebbieBeanInfo<?>, BeanCreator<?>> preparations = new LinkedHashMap<>();

    private final Set<Class<? extends Annotation>> injectTypes = new HashSet<>(2);
    private Class<? extends Annotation> injectType;

    @SuppressWarnings("unchecked")
    InjectedBeanFactory() {
        injectTypes.add(BeanInject.class);
        injectTypes.add(PropertyInject.class);
        try {
            Class<?> injectType = Class.forName("javax.inject.Inject");
            if (Annotation.class.isAssignableFrom(injectType)) {
                this.injectType = (Class<Annotation>) injectType;
                injectTypes.add(this.injectType);
            }
        } catch (ClassNotFoundException e) {
            LOGGER.info("class javax.inject.Inject not found");
        }
    }

    public <A extends Annotation> void registerInjectType(Class<A> injectType) {
        injectTypes.add(injectType);
    }

    public void registerInjectType(Set<Class<? extends Annotation>> injectTypes) {
        if (injectTypes != null && !injectTypes.isEmpty()) {
            this.injectTypes.addAll(injectTypes);
        }
    }

    public boolean containInjectType(Class<? extends Annotation> annotation) {
        return injectTypes.contains(annotation);
    }

    public Set<Class<? extends Annotation>> getInjectTypes() {
        return injectTypes;
    }

    public Class<? extends Annotation> getInjectType() {
        return this.injectType;
    }

    @Override
    public void setBeanFactoryContext(DebbieApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
        this.beanInfoFactory = applicationContext.getDebbieBeanInfoFactory();
    }

    @Override
    public void setGlobalBeanFactory(GlobalBeanFactory globalBeanFactory) {
        this.globalBeanFactory = globalBeanFactory;
    }

    public <T> T factory(DebbieBeanInfo<T> beanInfo) {
        BeanCreator<T> creator = createIfNotExist(beanInfo, false);
        return factory(creator);
    }

    public <T> T factory(DebbieBeanInfo<T> beanInfo, boolean skipFactory) {
        BeanCreator<T> creator = createIfNotExist(beanInfo, skipFactory);
        return factory(creator);
    }

    public <T> T factory(DebbieBeanInfo<T> beanInfo, boolean skipFactory, Object firstParamValue) {
        BeanCreator<T> creator = createIfNotExist(beanInfo, skipFactory);
        return factory(creator, firstParamValue);
    }

    public <T> T factory(BeanCreator<T> creator) {
        return factory(creator, null);
    }

    public <T> T factory(BeanCreator<T> creator, Object firstParamValue) {
        if (creator.isCreated()) {
            return creator.getCreatedBean();
        }

        creator.createPreparation(singletonBeanCreatorMap, firstParamValue);

        creator.createPreparationByDependence();

        creator.postConstructor();
        creator.postPreparation(singletonBeanCreatorMap);
        creator.postCreated();

        var beanInfo = creator.getBeanInfo();

        preparations.remove(beanInfo);
        return creator.getCreatedBean();
    }

    @SuppressWarnings({"unchecked"})
    public <T> BeanCreator<T> factoryBeanPreparation(DebbieBeanInfo<T> beanInfo, boolean skipFactory) {
        if (singletonBeanCreatorMap.containsKey(beanInfo)) {
            BeanCreator<T> beanCreator = (BeanCreator<T>) singletonBeanCreatorMap.get(beanInfo);
            beanInfo.setBean(beanCreator.getCreatedBean());
        }
        if (preparations.containsKey(beanInfo)) {
            return (BeanCreator<T>) preparations.get(beanInfo);
        } else {
            BeanCreator<T> creator = new BeanCreatorImpl<>(beanInfo, this.beanInfoFactory);
            if (beanInfo.isSingleton() && beanInfo.isPresent()) {
                creator.create(beanInfo.getBean());
                return creator;
            }
            if (!skipFactory && beanInfo.hasBeanFactory()) {
                creator.create(beanInfo.getBeanFactory().factoryBean());
                return creator;
            }
            creator.setInjectedBeanFactory(this);
            this.preparations.put(beanInfo, creator);
            return creator;
        }
    }

    public void resolveFieldValue(DebbieBeanInfo<?> beanInfo, FieldInfo field, String keyPrefix) {
        var propertyInject = field.getAnnotation(PropertyInject.class);
        if (propertyInject != null) {
            resolvePropertiesInject(beanInfo.getBean(), field.getField(), keyPrefix, propertyInject);
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

    @SuppressWarnings({"unchecked", "rawtypes"})
    public void resolveAwareValue(InjectedBeanFactory injectedBeanFactory, Object object, Class<?> clazz) {
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

    }

    public void resolveMethodValue(Object object, Method method) {
        // todo
    }

    public Object factoryProperty(Class<?> valueType, String keyPrefix, PropertyInject propertyInject) {
        String property = propertyInject.value();
        if (!property.isBlank()) {
            String key;
            if (keyPrefix != null) {
                key = keyPrefix + property;
            } else {
                key = property;
            }
            BaseProperties properties = new BaseProperties();
            String value = properties.getValue(key);
            if (value != null) {
                Class<? extends DataTransformer<?, String>> transformer = propertyInject.transformer();
                Object transform = null;
                try {
                    DataTransformer<?, String> dataTransformer = ReflectionHelper.newInstance(transformer);
                    transform = dataTransformer.reverse(value);
                } catch (Exception e) {
                    LOGGER.error("", e);
                }
                if (transform == null || transform.getClass() != valueType) {
                    transform = applicationContext.transform(value, valueType);
                }
                return transform;
            }
        }
        return null;
    }

    @SuppressWarnings({"unchecked"})
    private <Bean> BeanCreator<Bean> createIfNotExist(DebbieBeanInfo<Bean> beanInfo, boolean skipFactory) {
        BeanCreator<Bean> creator = null;
        if (singletonBeanCreatorMap.containsKey(beanInfo)) {
            creator = (BeanCreator<Bean>) singletonBeanCreatorMap.get(beanInfo);
            beanInfo.setBean(creator.getCreatedBean());
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
    private void resolveFieldDependentBean(DebbieBeanInfo<?> beanInfo, FieldInfo fieldInfo, Annotation inject) {
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
        var fieldBeanInfoMap = beanInfo.getFieldBeanDependent();
        if (fieldBeanInfoMap == null || fieldBeanInfoMap.isEmpty()) {
            if (required)
                throw new NoBeanException("no bean " + name + " found .");
            else
                return;
        }
        DebbieBeanInfo fieldBeanInfo = fieldBeanInfoMap.get(fieldInfo);
        if (fieldBeanInfo != null) {
            Object value;
            if (fieldBeanInfo.isPresent()) {
                value = fieldBeanInfo.getBean();
            } else {
                BeanCreator<?> creator = createIfNotExist(fieldBeanInfo, false);
                if (creator.isCreated()) {
                    fieldBeanInfo.setBean(creator.getCreatedBean());
                } else {
                    fieldBeanInfo.setBean(factory(creator));
                }
                value = globalBeanFactory.factoryAfterCreatedByProxy(fieldBeanInfo, BeanProxyType.ASM);
            }
            ReflectionHelper.setField(beanInfo.getBean(), field, value);
        } else {
            if (required)
                throw new NoBeanException("no bean " + name + " found .");
        }
    }

    public void destroy() {
        preparations.clear();
        singletonBeanCreatorMap.clear();
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(InjectedBeanFactory.class);
}
