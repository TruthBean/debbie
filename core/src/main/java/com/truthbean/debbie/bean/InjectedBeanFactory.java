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
public class InjectedBeanFactory implements BeanFactoryContextAware {
    private BeanFactoryContext applicationContext;

    final Map<DebbieBeanInfo<?>, BeanCreator<?>> singletonBeanCreatorMap = new ConcurrentHashMap<>();
    private final Map<DebbieBeanInfo<?>, BeanCreator<?>> preparations = new LinkedHashMap<>();

    private final Set<Annotation> parameterInject = new HashSet<>();

    public <A extends Annotation> void registerParameterInject(A inject) {
        parameterInject.add(inject);
    }

    @Override
    public void setBeanFactoryContext(BeanFactoryContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    public <T> T factory(DebbieBeanInfo<T> beanInfo) {
        BeanCreator<T> creator = createIfNotExist(beanInfo);
        return factory(creator);
    }

    public <T> T factory(BeanCreator<T> creator) {
        if (creator.isCreated()) {
            return creator.getCreatedBean();
        }

        creator.createPreparation(singletonBeanCreatorMap);

        creator.createPreparationByDependence();

        creator.postConstructor();
        creator.postPreparation(singletonBeanCreatorMap);
        creator.postCreated();

        var beanInfo = creator.getBeanInfo();

        preparations.remove(beanInfo);
        return creator.getCreatedBean();
    }

    @SuppressWarnings({"unchecked"})
    public <T> BeanCreator<T> factoryBeanPreparation(DebbieBeanInfo<T> beanInfo) {
        if (singletonBeanCreatorMap.containsKey(beanInfo)) {
            BeanCreator<T> beanCreator = (BeanCreator<T>) singletonBeanCreatorMap.get(beanInfo);
            beanInfo.setBean(beanCreator.getCreatedBean());
        }
        if (preparations.containsKey(beanInfo)) {
            return (BeanCreator<T>) preparations.get(beanInfo);
        } else {
            BeanCreator<T> creator = new BeanCreatorImpl<>(beanInfo, this.applicationContext);
            if (beanInfo.isSingleton() && beanInfo.isPresent()) {
                creator.create(beanInfo.getBean());
                return creator;
            }
            if (beanInfo.hasBeanFactory()) {
                creator.create(beanInfo.getBeanFactory().factoryBean());
                return creator;
            }
            creator.setInjectedBeanFactory(this);
            this.preparations.put(beanInfo, creator);
            return creator;
        }
    }

    public BeanCreator<?> getParameterBean(Parameter parameter, Class<?> beanClass) {
        LOGGER.trace(() -> "resolve " + beanClass + "'s parameter dependent bean(" + parameter.getType() + ") by type");
        var beanInject = parameter.getAnnotation(BeanInject.class);
        String name = "";
        boolean require = false;
        if (beanInject != null) {
            name = beanInject.name();
            if (name.isBlank()) {
                name = beanInject.value();
            }
            require = beanInject.require();
        }
        if (name.isBlank() && parameter.isNamePresent()) {
            name = parameter.getName();
        }
        Class<?> type = parameter.getType();
        if (!name.isBlank()) {
            var beanInfo = this.applicationContext.getBeanInfo(name, type, require);
            if (beanInfo != null) {
                return factoryBeanPreparation(beanInfo);
            } else {
                if (require) {
                    throw new NoBeanException("no bean " + name + " found .");
                }
            }
        } else {
            var beanInfo = this.applicationContext.getBeanInfo(null, type, require);
            if (beanInfo != null) {
                return factoryBeanPreparation(beanInfo);
            } else {
                if (require) {
                    throw new NoBeanException("no bean " + name + " found .");
                }
            }
        }
        throw new NoBeanException(() -> "no bean " + parameter.getName() + " found .");
    }

    public void resolveFieldValue(DebbieBeanInfo<?> beanInfo, FieldInfo field, String keyPrefix) {
        var propertyInject = field.getAnnotation(PropertyInject.class);
        if (propertyInject != null) {
            resolvePropertiesInject(beanInfo.getBean(), field.getField(), keyPrefix, propertyInject);
            return;
        }
        var beanInject = field.getAnnotation(BeanInject.class);
        if (beanInject != null) {
            resolveFieldDependentBean(beanInfo, field, beanInject);
        } else {
            Class<? extends Annotation> injectClass = applicationContext.getInjectType();
            if (injectClass == null) return;
            Object inject = field.getAnnotation(injectClass);
            if (inject != null) {
                resolveFieldDependentBean(beanInfo, field, inject);
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

        var values = applicationContext.getBeanList(beanClass);
        if (!values.isEmpty()) {
            aware.setBeans(values);
            if (name != null) {
                var beanInfo = this.applicationContext.getBeanInfo(name, beanClass, true);
                if (beanInfo != null) {
                    BeanCreator<?> value = injectedBeanFactory.factoryBeanPreparation(beanInfo);
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

    @SuppressWarnings({"unchecked"})
    private <Bean> BeanCreator<Bean> createIfNotExist(DebbieBeanInfo<Bean> beanInfo) {
        BeanCreator<Bean> creator = null;
        if (singletonBeanCreatorMap.containsKey(beanInfo)) {
            creator = (BeanCreator<Bean>) singletonBeanCreatorMap.get(beanInfo);
            beanInfo.setBean(creator.getCreatedBean());
        }
        if (creator == null) {
            if (preparations.containsKey(beanInfo)) {
                creator = (BeanCreator<Bean>) preparations.get(beanInfo);
            } else {
                creator = factoryBeanPreparation(beanInfo);
            }
            if (beanInfo.isSingleton()) {
                singletonBeanCreatorMap.put(beanInfo, creator);
            }
        }
        return creator;
    }

    private void resolvePropertiesInject(Object object, Field field, String keyPrefix, PropertyInject propertyInject) {
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
                Class<?> type = field.getType();
                if (transform == null || transform.getClass() != type) {
                    transform = applicationContext.transform(value, type);
                }
                // use setter method to inject filed
                // if setter method not found, inject directly
                ReflectionHelper.invokeFieldBySetMethod(object, field, transform);
            }
        }
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private void resolveFieldDependentBean(DebbieBeanInfo<?> beanInfo, FieldInfo fieldInfo, Object inject) {
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
                BeanCreator<?> creator = createIfNotExist(fieldBeanInfo);
                if (creator.isCreated()) {
                    value = creator.getCreatedBean();
                    fieldBeanInfo.setBean(value);
                } else {
                    value = factory(creator);
                }
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
