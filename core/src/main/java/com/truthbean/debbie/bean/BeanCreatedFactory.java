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
import com.truthbean.debbie.properties.PropertiesConfiguration;
import com.truthbean.debbie.properties.PropertyInject;
import com.truthbean.debbie.proxy.BeanProxyType;
import com.truthbean.debbie.proxy.jdk.JdkDynamicProxy;
import com.truthbean.debbie.reflection.ClassInfo;
import com.truthbean.debbie.reflection.FieldInfo;
import com.truthbean.debbie.reflection.ReflectionHelper;
import com.truthbean.logger.LoggerFactory;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.security.AccessControlContext;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author TruthBean/Rogar·Q
 * @since 0.1.0
 * Created on 2020-06-28 14:42.
 */
public class BeanCreatedFactory {

    private final Map<DebbieBeanInfo<?>, BeanCreatedResolver<?>> singletonBeanInvokerMap = new ConcurrentHashMap<>();

    private final DebbieApplicationContext applicationContext;
    private final DebbieBeanInfoFactory beanInfoFactory;
    private final BeanInitialization beanInitialization;
    private final GlobalBeanFactory globalBeanFactory;

    BeanCreatedFactory(DebbieApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
        this.beanInfoFactory = applicationContext.getDebbieBeanInfoFactory();
        this.beanInitialization = applicationContext.getBeanInitialization();
        this.globalBeanFactory = applicationContext.getGlobalBeanFactory();
    }

    public <T, K extends T> T factory(DebbieBeanInfo<K> beanInfo) {
        if (beanInfo.isSingleton() && beanInfo.isPresent()) {
            return factoryByProxy(beanInfo);
        }

        var beanFactory = beanInfo.getBeanFactory();
        if (beanFactory != null) {
            if (System.getSecurityManager() != null) {
                // TODO to set securityContextProvider
                try {
                    AccessControlContext securityContextProvider = AccessController.getContext();
                    AccessController.doPrivileged((PrivilegedAction<T>) beanFactory::getBean, securityContextProvider);
                } catch (Exception e) {
                    LOGGER.error("getBean from factory via securityContextProvider error", e);
                }
            } else
                return beanFactory.getBean();
        }

        factoryNoLimit(beanInfo);
        resolveFieldBeans(beanInfo);

        Class<T> beanInterface = beanInfo.getBeanInterface();
        if (beanInterface == null) {
            return factoryByProxy(beanInfo);
        }

        Class<K> clazz = beanInfo.getBeanClass();
        return factoryWithProxy(clazz, beanInterface, beanInfo);
    }

    public <T> T factoryNoLimit(Class<T> type) {
        LOGGER.trace(() -> "resolve bean(" + type + ") by type ");
        var beanBeanInvoker = new BeanCreatedResolver<>(type, this.applicationContext, this);
        var bean = beanBeanInvoker.getBean();
        var classInfo = beanBeanInvoker.getBeanInfo();
        resolveDependentBean(bean, classInfo);
        return bean;
    }

    public <T> T factoryByProxy(DebbieBeanInfo<T> beanInfo) {
        return this.globalBeanFactory.factoryAfterCreatedByProxy(beanInfo, BeanProxyType.NO);
    }

    public <T, K extends T> T factoryWithProxy(Class<K> type, Class<T> interfaceType, DebbieBeanInfo<K> beanInfo) {
        LOGGER.trace(() -> "resolve field dependent bean(" + interfaceType + ") by implement class " + type);
        JdkDynamicProxy<T, K> dynamicProxy = new JdkDynamicProxy<>();
        return dynamicProxy.invokeJdkProxy(this.applicationContext, interfaceType, beanInfo.getBean());
    }

    public <T> T factoryNoLimit(DebbieBeanInfo<T> beanInfo) {
        BeanCreatedResolver<T> beanInvoker = resolveSecondLevelBeans(beanInfo);
        var bean = beanInvoker.getBean();
        if (bean == null) {
            // resolve third level beans
            Collection<DebbieBeanInfo<?>> constructorBeanDependent = beanInfo.getConstructorBeanDependent().values();
            for (DebbieBeanInfo<?> debbieBeanInfo : constructorBeanDependent) {
                if ((debbieBeanInfo.optional().isEmpty() || !debbieBeanInfo.isHasVirtualValue())) {
                    beanInfo.setHasVirtualValue(true);
                    factoryNoLimit(debbieBeanInfo);
                }
            }
            if (beanInfo.getConstructorBeanDependent().isEmpty() || beanInfo.isConstructorBeanDependentHasValue()) {
                beanInvoker.createBeanByConstructorDependent();
                bean = beanInvoker.getBean();
                if (bean != null) {
                    beanInfo = beanInvoker.getBeanInfo();
                    beanInfo.setBean(bean);
                    beanInfo.setHasVirtualValue(false);
                }
            }
        }
        if (bean == null) {
            beanInvoker.createBeanByConstructorDependent();
            bean = beanInvoker.getBean();
            if (bean != null) {
                beanInfo = beanInvoker.getBeanInfo();
                beanInfo.setBean(bean);
                if (beanInfo.isHasVirtualValue()) {
                    beanInfo.setHasVirtualValue(false);
                }
            }
        }

        bean = beanInvoker.getBean();
        if (bean != null) {
            beanInvoker.getFieldsDependent(this.applicationContext);
            beanInfo.setBean(bean);
            resolvePropertyFieldValue(beanInfo);
        }

        return bean;
    }

    @SuppressWarnings("unchecked")
    public <T> T getBeanByInitMethod(Method initMethod, DebbieApplicationContext handler) {
        if (Modifier.isStatic(initMethod.getModifiers())) {
            Parameter[] parameters = initMethod.getParameters();
            if (parameters == null || parameters.length == 0) {
                return (T) ReflectionHelper.invokeStaticMethod(initMethod);
            } else {
                // todo
            }
        }
        return null;
    }

    void resolveDependentBean(Object object, ClassInfo<?> classInfo) {
        String keyPrefix = null;

        Map<Class<? extends Annotation>, Annotation> classAnnotations = classInfo.getClassAnnotations();
        if (classAnnotations.containsKey(PropertiesConfiguration.class)) {
            var beanConfiguration = (PropertiesConfiguration) classAnnotations.get(PropertiesConfiguration.class);
            keyPrefix = beanConfiguration.keyPrefix();
            if (!keyPrefix.isBlank() && !keyPrefix.endsWith(".")) {
                keyPrefix += ".";
            }
        }

        List<FieldInfo> fields = classInfo.getFields();
        if (fields != null && !fields.isEmpty()) {
            String finalKeyPrefix = keyPrefix;
            fields.forEach(field -> resolveFieldValue(object, field.getField(), finalKeyPrefix));
        }

        Class<?> clazz = classInfo.getClazz();
        if (BeanAware.class.isAssignableFrom(clazz)) {
            resolveAwareValue(object, clazz);
        }
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private void resolveAwareValue(Object object, Class<?> clazz) {
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

        var values = this.globalBeanFactory.getBeanList(beanClass);
        if (!values.isEmpty()) {
            aware.setBeans(values);
            if (name != null) {
                Object factory = this.globalBeanFactory.factory(name, beanClass, true);
                aware.setBean(factory);
            } else if (values.size() == 1) {
                aware.setBean(values.get(0));
            }
        }

    }

    private void resolveFieldValue(Object object, Field field, String keyPrefix) {
        var propertyInject = field.getAnnotation(PropertyInject.class);
        if (propertyInject != null) {
            resolvePropertiesInject(object, field, keyPrefix, propertyInject);
            return;
        }
        var beanInject = field.getAnnotation(BeanInject.class);
        if (beanInject != null) {
            resolveFieldDependentBean(object, field, beanInject);
        } else {
            /*Class<? extends Annotation> injectClass = this.applicationContext.getInjectType();
            if (injectClass == null) return;
            Object inject = field.getAnnotation(injectClass);
            if (inject != null) {
                resolveFieldDependentBean(null, object, field, inject);
            }*/
        }
    }

    private void resolvePropertyFieldValue(DebbieBeanInfo<?> beanInfo) {
        List<FieldInfo> fields = beanInfo.getFields();
        String keyPrefix = null;

        Map<Class<? extends Annotation>, Annotation> classAnnotations = beanInfo.getClassAnnotations();
        if (classAnnotations.containsKey(PropertiesConfiguration.class)) {
            var beanConfiguration = (PropertiesConfiguration) classAnnotations.get(PropertiesConfiguration.class);
            keyPrefix = beanConfiguration.keyPrefix();
            if (!keyPrefix.isBlank() && !keyPrefix.endsWith(".")) {
                keyPrefix += ".";
            }
        }

        if (fields != null && !fields.isEmpty()) {
            String finalKeyPrefix = keyPrefix;
            fields.forEach(field -> resolvePropertyFieldValue(beanInfo.getBean(), field.getField(), finalKeyPrefix));
        }
    }

    private void resolvePropertyFieldValue(Object object, Field field, String keyPrefix) {
        var propertyInject = field.getAnnotation(PropertyInject.class);
        if (propertyInject != null) {
            resolvePropertiesInject(object, field, keyPrefix, propertyInject);
        }
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
                    e.printStackTrace();
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

    private void resolveFieldDependentBean(String name, Object object, Field field, BeanInject beanInject) {
        if (name == null || name.isBlank()) {
            name = field.getName();
        }
        String finalName = name;
        LOGGER.trace(() -> "resolve field dependent bean(" + field.getType() + ") by name : " + finalName);
        var value = this.globalBeanFactory.factory(name, field.getType(), beanInject.require());
        if (value != null) {
            ReflectionHelper.setField(object, field, value);
        } else {
            if (beanInject.require()) {
                throw new NoBeanException("no bean " + name + " found .");
            }
        }
    }

    private void resolveFieldDependentBean(String name, Object object, Field field, Object inject) {
        if (name == null || name.isBlank()) {
            name = field.getName();
        }
        final String finalName = name;
        LOGGER.trace(() -> "resolve field dependent bean(" + field.getType() + ") by name : " + finalName);
        var value = this.globalBeanFactory.factory(name, field.getType(), true);
        if (value != null) {
            ReflectionHelper.setField(object, field, value);
        } else {
            throw new NoBeanException("no bean " + name + " found .");
        }
    }

    private void resolveFieldDependentBean(Object object, Field field, BeanInject beanInject) {
        LOGGER.trace(() -> "resolve field dependent bean(" + field.getType() + ") by type ");
        String name = beanInject.name();
        if (name.isBlank()) {
            name = beanInject.value();
        }
        resolveFieldDependentBean(name, object, field, beanInject);
    }

    public Object getParameterBean(Parameter parameter) {
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
            var value = this.globalBeanFactory.factory(name, type, require, false);
            if (value != null) {
                return value;
            } else {
                if (require) {
                    throw new NoBeanException("no bean " + name + " found .");
                }
            }
        } else {
            var value = this.globalBeanFactory.factory(null, type, require, false);
            if (value != null) {
                return value;
            } else {
                if (require) {
                    throw new NoBeanException("no bean " + name + " found .");
                }
            }
        }
        throw new NoBeanException(() -> "no bean " + parameter.getName() + " found .");
    }

    public <Bean> BeanCreatedResolver<Bean> factoryBeanInvoker(Class<Bean> beanClass) {
        return new BeanCreatedResolver<>(beanClass, this.applicationContext, this);
    }

    @SuppressWarnings("unchecked")
    private <T> BeanCreatedResolver<T> setIfNotExist(DebbieBeanInfo<T> beanInfo) {
        if (beanInfo.getBeanType() == BeanType.SINGLETON) {
            if (singletonBeanInvokerMap.containsKey(beanInfo)) {
                return (BeanCreatedResolver<T>) singletonBeanInvokerMap.get(beanInfo);
            } else {
                BeanCreatedResolver<T> beanInvoker = new BeanCreatedResolver<>(beanInfo, this.applicationContext, this);
                singletonBeanInvokerMap.put(beanInfo, beanInvoker);
                return beanInvoker;
            }
        } else {
            return BeanCreatedResolver.create(beanInfo, this.applicationContext, singletonBeanInvokerMap, this);
        }
    }

    public <T> BeanCreatedResolver<T> resolveSecondLevelBeans(DebbieBeanInfo<T> beanInfo) {
        BeanCreatedResolver<T> beanInvoker = setIfNotExist(beanInfo);
        var bean = beanInvoker.getBean();
        // NOTE: if constructor has circulated dependence, the Bean will create twice!!!
        if (beanInfo.getBeanType() == BeanType.SINGLETON && bean == null) {
            // resolve second level beans
            beanInvoker.resolveConstructor(this.applicationContext, singletonBeanInvokerMap, this);
        }
        return beanInvoker;
    }

    public <T> Object factoryAndInvokeMethod(Class<T> type, Method method, Object[] parameters) {
        T bean = factoryNoLimit(type);
        return ReflectionHelper.invokeMethod(bean, method, parameters);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private <T> void resolveFieldBeans(DebbieBeanInfo<T> beanInfo) {
        Map<FieldInfo, DebbieBeanInfo<?>> fieldBeanDependents = beanInfo.getFieldBeanDependent();
        if (fieldBeanDependents != null && !fieldBeanDependents.isEmpty()) {
            Collection<DebbieBeanInfo<?>> fieldBeanDependent = fieldBeanDependents.values();
            for (DebbieBeanInfo debbieBeanInfo : fieldBeanDependent) {
                if ((debbieBeanInfo.isEmpty() || debbieBeanInfo.isHasVirtualValue())) {
                    Object object = this.beanInfoFactory.getBeanInfo(debbieBeanInfo.getServiceName(),
                            (Class<T>) debbieBeanInfo.getBeanClass(), true).getBean();
                    if (object != null) {
                        debbieBeanInfo.setBean(object);
                    }
                }
            }

            BeanCreatedResolver<T> beanInvoker;
            if (beanInfo.getBeanType() == BeanType.SINGLETON) {
                beanInvoker = (BeanCreatedResolver<T>) singletonBeanInvokerMap.get(beanInfo);
            } else {
                beanInvoker = BeanCreatedResolver.create(beanInfo, this.applicationContext, singletonBeanInvokerMap, this);
            }
            beanInvoker.resolveFieldsDependent(this.applicationContext, this);
            T bean = beanInvoker.getBean();
            beanInfo.setBean(bean);
        }
    }

    public void destroy() {
        this.beanInfoFactory.destroyBeans(singletonBeanInvokerMap.keySet());
        singletonBeanInvokerMap.clear();
    }

    public void remove(DebbieBeanInfo<?> beanInfo) {
        this.singletonBeanInvokerMap.remove(beanInfo);
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(BeanCreatedFactory.class);
}
