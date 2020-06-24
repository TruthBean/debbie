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
import com.truthbean.debbie.reflection.ReflectionHelper;
import com.truthbean.logger.LoggerFactory;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.HashSet;
import java.util.Set;

/**
 * @author TruthBean/Rogar·Q
 * @since 0.1.0
 * Created on 2020-06-23 22:26.
 */
public class BeanDependenceProcessor implements BeanFactoryHandlerAware, BeanClosure {

    private BeanFactoryHandler beanFactoryHandler;
    private BeanInitialization beanInitialization;

    private final Set<Annotation> parameterInject = new HashSet<>();

    public <A extends Annotation> void registerParameterInject(A inject) {
        parameterInject.add(inject);
    }

    @Override
    public void setBeanFactoryHandler(BeanFactoryHandler beanFactoryHandler) {
        this.beanFactoryHandler = beanFactoryHandler;
        this.beanInitialization = beanFactoryHandler.getBeanInitialization();
    }

    public BeanCreator<?> getParameterBean(Parameter parameter, InjectedBeanFactory injectedBeanFactory) {
        LOGGER.trace(() -> "resolve parameter dependent bean(" + parameter.getType() + ") by type ");
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
            var beanInfo = this.beanFactoryHandler.getBeanInfo(name, type, require);
            if (beanInfo != null) {
                return injectedBeanFactory.factoryBeanPreparation(beanInfo, this);
            } else {
                if (require) {
                    throw new NoBeanException("no bean " + name + " found .");
                }
            }
        } else {
            var beanInfo = this.beanFactoryHandler.getBeanInfo(null, type, require);
            if (beanInfo != null) {
                return injectedBeanFactory.factoryBeanPreparation(beanInfo, this);
            } else {
                if (require) {
                    throw new NoBeanException("no bean " + name + " found .");
                }
            }
        }
        throw new NoBeanException(() -> "no bean " + parameter.getName() + " found .");
    }

    public void resolveFieldValue(InjectedBeanFactory injectedBeanFactory,
                                  Object object, Field field, String keyPrefix) {
        var propertyInject = field.getAnnotation(PropertyInject.class);
        if (propertyInject != null) {
            resolvePropertiesInject(object, field, keyPrefix, propertyInject);
            return;
        }
        var beanInject = field.getAnnotation(BeanInject.class);
        if (beanInject != null) {
            resolveFieldDependentBean(injectedBeanFactory, null, object, field, beanInject);
        } else {
            Class<? extends Annotation> injectClass = beanFactoryHandler.getInjectType();
            if (injectClass == null) return;
            Object inject = field.getAnnotation(injectClass);
            if (inject != null) {
                resolveFieldDependentBean(injectedBeanFactory, null, object, field, inject);
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

        var values = beanFactoryHandler.getBeanList(beanClass);
        if (!values.isEmpty()) {
            aware.setBeans(values);
            if (name != null) {
                var beanInfo = this.beanFactoryHandler.getBeanInfo(name, beanClass, true);
                if (beanInfo != null) {
                    BeanCreator<?> value = injectedBeanFactory.factoryBeanPreparation(beanInfo, this);
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
                    transform = beanInitialization.transform(value, type);
                }
                // use setter method to inject filed
                // if setter method not found, inject directly
                ReflectionHelper.invokeFieldBySetMethod(object, field, transform);
            }
        }
    }

    private void resolveFieldDependentBean(InjectedBeanFactory injectedBeanFactory,
                                           String name, Object object, Field field, Object inject) {
        if (name == null || name.isBlank()) {
            if (inject instanceof BeanInject) {
                name = ((BeanInject) inject).name();
                if (name.isBlank()) {
                    name = ((BeanInject) inject).value();
                }
            }

            if (name == null || name.isBlank()) {
                name = field.getName();
            }
        }

        final String finalName = name;
        LOGGER.trace(() -> "resolve field dependent bean(" + field.getType() + ") by name : " + finalName);
        var required = true;
        if (inject instanceof BeanInject) {
            required = ((BeanInject) inject).require();
        }
        var beanInfo = this.beanFactoryHandler.getBeanInfo(null, field.getType(), required);
        if (beanInfo != null) {
            BeanCreator<?> beanCreator = injectedBeanFactory.factoryBeanPreparation(beanInfo, this);
            ReflectionHelper.setField(object, field, beanCreator.create());
        } else {
            if (required)
                throw new NoBeanException("no bean " + name + " found .");
        }
    }

    @Override
    public void destroy() {
        parameterInject.clear();
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(BeanDependenceProcessor.class);
}
