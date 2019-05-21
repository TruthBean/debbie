package com.truthbean.debbie.core.bean;

import com.truthbean.debbie.core.data.transformer.DataTransformerFactory;

import com.truthbean.debbie.core.properties.BaseProperties;
import com.truthbean.debbie.core.properties.BeanConfiguration;
import com.truthbean.debbie.core.properties.PropertyInject;
import com.truthbean.debbie.core.proxy.InterfaceDynamicProxy;
import com.truthbean.debbie.core.reflection.ClassInfo;
import com.truthbean.debbie.core.reflection.ReflectionHelper;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author TruthBean
 * @since 0.0.1
 * Created on 2019/3/23 11:32.
 */
public class BeanFactory {

    private static final Set<DebbieBeanInfo> beanServiceInfoSet = new HashSet<>();

    static {
        BeanInitialization beanInitialization = new BeanInitialization();
        var beanServiceInfoList = beanInitialization.getAnnotatedClass(BeanComponent.class);

        beanServiceInfoList.forEach((i) -> {
            var clazz = i.getClazz();
            if (clazz.isAnnotation()) {
                var annotation = (Class<? extends Annotation>) clazz;
                var set = beanInitialization.getAnnotatedClass(annotation);
                beanServiceInfoSet.addAll(set);

            } else {
                if (clazz.isInterface()) {
                    beanServiceInfoSet.addAll(beanInitialization.getBeanByInterface(clazz));
                } else if (Modifier.isAbstract(i.getClazz().getModifiers())) {
                    beanServiceInfoSet.addAll(beanInitialization.getBeanByAbstractSuper(clazz));
                } else {
                    beanServiceInfoSet.add(i);
                }
            }
        });
    }

    public static <T, K extends T> T factory(String serviceName) {
        var list = beanServiceInfoSet.stream().filter(beanServiceInfo -> serviceName.equals(beanServiceInfo.getServiceName())).toArray(DebbieBeanInfo[]::new);

        if (list.length == 0) {
            throw new RuntimeException(serviceName + " not found");
        }
        if (list.length > 1) {
            throw new OneMoreBeanRegisteredException(serviceName + " must be only one");
        }
        var t = list[0];
        Class<K> clazz = t.getBeanClass();
        Class<T> beanInterface = t.getBeanInterface();

        if (beanInterface == null) {
            return factoryWithProxy(clazz);
        }

        return factoryWithProxy(clazz, beanInterface);
    }

    public static <T, K extends T> T factoryWithProxy(Class<T> type) {
        var list = beanServiceInfoSet.stream().filter(beanServiceInfo -> type == beanServiceInfo.getBeanClass() || type == beanServiceInfo.getBeanInterface()).toArray(DebbieBeanInfo[]::new);

        if (list.length == 0) {
            throw new NoBeanException(type.getName() + " not found");
        }
        if (list.length > 1) {
            throw new OneMoreBeanRegisteredException(type.getName() + " must be only one");
        }
        var t = list[0];
        Class<K> clazz = t.getBeanClass();
        Class<T> beanInterface = t.getBeanInterface();

        if (beanInterface == null) {
            return factory(clazz);
        }

        return factoryWithProxy(clazz, beanInterface);
    }

    public static void resolveDependentBean(Object object, ClassInfo<?> classInfo) {
        List<Field> fields = classInfo.getFields();
        String keyPrefix = null;

        Map<Class<? extends Annotation>, Annotation> classAnnotations = classInfo.getClassAnnotations();
        if (classAnnotations.containsKey(BeanConfiguration.class)) {
            var beanConfiguration = (BeanConfiguration) classAnnotations.get(BeanConfiguration.class);
            keyPrefix = beanConfiguration.keyPrefix();
        }

        if (fields != null && !fields.isEmpty()) {
            String finalKeyPrefix = keyPrefix;
            fields.forEach(field -> resolveFieldValue(object, field, finalKeyPrefix));
        }
    }

    private static void resolveFieldValue(Object object, Field field, String keyPrefix) {
        var propertyInject = field.getAnnotation(PropertyInject.class);
        if (propertyInject != null && keyPrefix != null) {
            resolvePropertiesInject(object, field, keyPrefix, propertyInject);
            return;
        }
        var beanInject = field.getAnnotation(BeanInject.class);
        if (beanInject != null) {
            resolveFieldDependentBean(object, field, beanInject);
        }
    }

    private static void resolvePropertiesInject(Object object, Field field, String keyPrefix, PropertyInject propertyInject) {
        String property = propertyInject.value();
        if (keyPrefix != null && !property.isBlank()) {
            String key = keyPrefix + property;
            BaseProperties properties = new BaseProperties();
            String value = properties.getValue(key);
            if (value != null) {
                Class<?> type = field.getType();
                Object transform = DataTransformerFactory.transform(value, type);
                // use setter method to inject filed
                ReflectionHelper.invokeSetMethod(object, field, transform);
            }
        }
    }

    private static void resolveFieldDependentBean(Object object, Field field, BeanInject beanInject) {
        String name = beanInject.name();
        if (!name.isBlank()) {
            var value = factory(name);
            if (value != null) {
                ReflectionHelper.setField(object, field, value);
            } else {
                if (beanInject.require()) {
                    throw new NoBeanException("no bean " + name + " found .");
                }
            }
        } else {
            Class<?> type = field.getType();
            var value = factory(type);
            if (value != null) {
                ReflectionHelper.setField(object, field, value);
            } else {
                if (beanInject.require()) {
                    throw new NoBeanException("no bean " + name + " found .");
                }
            }
        }
    }

    public static Object getParameterBean(Parameter parameter) {
        var beanInject = parameter.getAnnotation(BeanInject.class);
        if (beanInject != null) {
            String name = beanInject.name();
            if (!name.isBlank()) {
                var value = factory(name);
                if (value != null) {
                    return value;
                } else {
                    if (beanInject.require()) {
                        throw new NoBeanException("no bean " + name + " found .");
                    }
                }
            } else {
                Class<?> type = parameter.getType();
                var value = factory(type);
                if (value != null) {
                    return value;
                } else {
                    if (beanInject.require()) {
                        throw new NoBeanException("no bean " + name + " found .");
                    }
                }
            }
        } else {
            // todo if inject is exist
            // parameter.getAnnotation(Inject.class);
        }
        throw new NoBeanException("no bean " + parameter.getName() + " found .");
    }

    public static <Bean> BeanInvoker<Bean> factoryBeanInvoker(Class<Bean> beanClass) {
        return new BeanInvoker<>(beanClass);
    }

    public static <T> T factory(Class<T> type) {
        var beanBeanInvoker = new BeanInvoker<>(type);
        var bean = beanBeanInvoker.getBean();
        var classInfo = beanBeanInvoker.getClassInfo();
        resolveDependentBean(bean, classInfo);
        return bean;
    }

    public static <T> Object factoryAndInvokeMethod(Class<T> type, Method routerMethod, Object[] parameters) {
        T bean = factory(type);
        return BeanInvoker.invokeMethod(bean, routerMethod, parameters);
    }

    public static <T, K extends T> T factoryWithProxy(Class<K> type, Class<T> interfaceType) {
        var beanBeanInvoker = new BeanInvoker<>(type);
        K target = beanBeanInvoker.getBean();
        var classInfo = beanBeanInvoker.getClassInfo();
        resolveDependentBean(target, classInfo);
        return InterfaceDynamicProxy.doJdkProxy(interfaceType, target);
    }

}
