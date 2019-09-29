package com.truthbean.debbie.bean;

import com.truthbean.debbie.data.transformer.DataTransformer;
import com.truthbean.debbie.data.transformer.DataTransformerFactory;
import com.truthbean.debbie.properties.BaseProperties;
import com.truthbean.debbie.properties.DebbieConfigurationFactory;
import com.truthbean.debbie.properties.PropertiesConfiguration;
import com.truthbean.debbie.properties.PropertyInject;
import com.truthbean.debbie.proxy.InterfaceDynamicProxy;
import com.truthbean.debbie.proxy.MethodProxyHandlerRegister;
import com.truthbean.debbie.reflection.ClassInfo;
import com.truthbean.debbie.reflection.ClassLoaderUtils;
import com.truthbean.debbie.reflection.ReflectionHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.security.AccessControlContext;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.*;

/**
 * @author TruthBean
 * @since 0.0.1
 * Created on 2019/3/23 11:32.
 */
public class BeanFactoryHandler {

    private final Set<DebbieBeanInfo> beanServiceInfoSet = new HashSet<>();

    private final Map<DebbieBeanInfo, BeanInvoker> singletonBeanInvokerMap = new HashMap<>();

    private final BeanInitialization beanInitialization;
    private final DebbieConfigurationFactory configurationFactory;
    private final MethodProxyHandlerRegister methodProxyHandlerRegister;

    protected BeanFactoryHandler() {
        beanInitialization = BeanInitialization.getInstance();
        configurationFactory = new DebbieConfigurationFactory(this);
        methodProxyHandlerRegister = new MethodProxyHandlerRegister();
    }

    public BeanInitialization getBeanInitialization() {
        return beanInitialization;
    }

    public DebbieConfigurationFactory getConfigurationFactory() {
        return configurationFactory;
    }

    public MethodProxyHandlerRegister getMethodProxyHandlerRegister() {
        return methodProxyHandlerRegister;
    }

    public void refreshBeans() {
        beanServiceInfoSet.addAll(beanInitialization.getRegisteredBeans());
        var beanServiceInfoList = beanInitialization.getAnnotatedBeans();

        beanServiceInfoList.forEach((i) -> {
            var clazz = i.getClazz();
            if (clazz.isAnnotation()) {
                @SuppressWarnings("unchecked")
                var annotation = (Class<? extends Annotation>) clazz;
                var set = beanInitialization.getAnnotatedClass(annotation);
                beanServiceInfoSet.addAll(set);

            } else {
                var beanFactory = i.getBeanFactory();
                if (beanFactory != null) {
                    beanServiceInfoSet.add(i);
                } else if (clazz.isInterface()) {
                    beanServiceInfoSet.addAll(beanInitialization.getBeanByInterface(clazz));
                } else if (Modifier.isAbstract(i.getClazz().getModifiers())) {
                    beanServiceInfoSet.addAll(beanInitialization.getBeanByAbstractSuper(clazz));
                } else {
                    beanServiceInfoSet.add(i);
                }
            }
        });

    }

    @SuppressWarnings("unchecked")
    public void autoCreateSingletonBeans() {
        beanServiceInfoSet.forEach(i -> {
            Boolean lazyCreate = i.getLazyCreate();
            if (lazyCreate != null && !lazyCreate && i.getBeanType() == BeanType.SINGLETON) {
                i.setBean(factory(i.getServiceName()));
                beanInitialization.refreshBean(i);
            }
        });
    }

    @SuppressWarnings("unchecked")
    public void autoCreateBeans() {
        beanServiceInfoSet.forEach(i -> {
            Boolean lazyCreate = i.getLazyCreate();
            if (lazyCreate != null && !lazyCreate) {
                i.setBean(factory(i.getServiceName()));
                beanInitialization.refreshBean(i);
            }
        });
    }

    public void destroy(DebbieBeanInfo beanInfo) {
        beanServiceInfoSet.remove(beanInfo);
        singletonBeanInvokerMap.remove(beanInfo);
    }

    public void destroy() {
        beanServiceInfoSet.clear();
        singletonBeanInvokerMap.clear();
        beanInitialization.reset();
    }

    private <T> DebbieBeanInfo<T> getBeanInfo(String serviceName, Class<T> type, boolean require, Set<DebbieBeanInfo> beanInfoSet) {
        List<DebbieBeanInfo> list = new ArrayList<>();
        if (serviceName != null && !serviceName.isBlank()) {
            for (DebbieBeanInfo debbieBeanInfo : beanInfoSet) {
                if (serviceName.equals(debbieBeanInfo.getServiceName())) {
                    list.add(debbieBeanInfo);
                }
            }
        }

        if (list.isEmpty()) {
            if (type != null) {
                for (DebbieBeanInfo debbieBeanInfo : beanInfoSet) {
                    var flag = type.getName().equals(debbieBeanInfo.getBeanClass().getName())
                            || (debbieBeanInfo.getBeanInterface() != null && type.getName().equals(debbieBeanInfo.getBeanInterface().getName()));
                    if (flag) {
                        list.add(debbieBeanInfo);
                    }
                }

                if (list.isEmpty()) {
                    for (DebbieBeanInfo debbieBeanInfo : beanInfoSet) {
                        var flag = type.isAssignableFrom(debbieBeanInfo.getBeanClass())
                                || (debbieBeanInfo.getBeanInterface() != null && type.isAssignableFrom(debbieBeanInfo.getBeanInterface()));
                        if (flag) {
                            list.add(debbieBeanInfo);
                        }
                    }
                }
            }

            if (list.size() == 0) {
                if (require) {
                    if ((serviceName == null || serviceName.isBlank()) && type != null) {
                        serviceName = type.getName();
                    }
                    throw new NoBeanException(serviceName + " not found");
                } else {
                    return null;
                }
            }
        }

        if (list.size() > 1) {
            if ((serviceName == null || serviceName.isBlank()) && type != null) {
                serviceName = type.getName();
            }
            throw new OneMoreBeanRegisteredException(serviceName + " must be only one");
        }

        @SuppressWarnings("unchecked")
        DebbieBeanInfo<T> beanInfo = list.get(0);
        if (type == null || type.isAssignableFrom(beanInfo.getBeanClass())) {
            if (beanInfo.getBeanType() == BeanType.SINGLETON) {
                return beanInfo;
            } else {
                return beanInfo.copy();
            }
        }
        throw new NoBeanException("bean " + type + " not found");
    }

    <T> DebbieBeanInfo<T> getBeanInfo(String serviceName, Class<T> type, boolean require) {
        try {
            return getBeanInfo(serviceName, type, require, beanServiceInfoSet);
        } catch (Exception e) {
            LOGGER.debug(e.getMessage());
        }
        return null;
    }

    protected <T> T factory(String serviceName, Class<T> type, boolean require) {
        var beanInfo = getBeanInfo(serviceName, type, require, beanServiceInfoSet);
        if (!require && beanInfo == null) return null;
        assert beanInfo != null;
        if (beanInfo.getBeanType() == BeanType.SINGLETON) {
            resolveFieldBeans(beanInfo);
            T bean = beanInfo.getBean();
            if (bean != null) {
                return bean;
            } else {
                bean = factory(beanInfo);
                beanInfo.setBean(bean);
                return bean;
            }
        }

        return factory(beanInfo);
    }

    public <T> T factory(String serviceName) {
        LOGGER.debug("factory bean with name " + serviceName);
        return factory(serviceName, null, true);
    }

    public <T, K extends T> T factory(DebbieBeanInfo<T> beanInfo) {
        if (beanInfo.getBeanType() == BeanType.SINGLETON) {
            resolveFieldBeans(beanInfo);
            var bean = beanInfo.getBean();
            if (bean != null) {
                return bean;
            }
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
        Class<K> clazz = beanInfo.getBeanClass();
        Class<T> beanInterface = beanInfo.getBeanInterface();

        if (beanInterface == null) {
            factoryNoLimit(beanInfo);
            resolveFieldBeans(beanInfo);
            return beanInfo.getBean();
        }

        return factoryWithProxy(clazz, beanInterface, beanInfo);
    }

    public <T, K extends T> T factory(Class<T> type) {
        LOGGER.debug("factory bean with type " + type.getName());
        return factory(null, type, true);
    }

    @SuppressWarnings("unchecked")
    private <T> void resolveFieldBeans(DebbieBeanInfo<T> beanInfo) {
        Map<Field, DebbieBeanInfo> fieldBeanDependents = beanInfo.getFieldBeanDependent();
        if (fieldBeanDependents != null && !fieldBeanDependents.isEmpty()) {
            Collection<DebbieBeanInfo> fieldBeanDependent = fieldBeanDependents.values();
            for (DebbieBeanInfo debbieBeanInfo : fieldBeanDependent) {
                if ((debbieBeanInfo.getBean() == null || debbieBeanInfo.isHasVirtualValue())) {
                    Object object = getBeanInfo(debbieBeanInfo.getServiceName(), debbieBeanInfo.getBeanClass(), true, beanServiceInfoSet).getBean();
                    if (object != null) {
                        debbieBeanInfo.setBean(object);
                    }
                }
            }

            BeanInvoker<T> beanInvoker;
            if (beanInfo.getBeanType() == BeanType.SINGLETON) {
                beanInvoker = singletonBeanInvokerMap.get(beanInfo);
            } else {
                beanInvoker = new BeanInvoker<>(beanInfo, this);
            }
            beanInvoker.resolveFieldsDependent(this);
            T bean = beanInvoker.getBean();
            beanInfo.setBean(bean);
        }
    }

    void resolveDependentBean(Object object, ClassInfo<?> classInfo) {
        List<Field> fields = classInfo.getFields();
        String keyPrefix = null;

        Map<Class<? extends Annotation>, Annotation> classAnnotations = classInfo.getClassAnnotations();
        if (classAnnotations.containsKey(PropertiesConfiguration.class)) {
            var beanConfiguration = (PropertiesConfiguration) classAnnotations.get(PropertiesConfiguration.class);
            keyPrefix = beanConfiguration.keyPrefix();
            if (!keyPrefix.isBlank() && !keyPrefix.endsWith(".")) {
                keyPrefix += ".";
            }
        }

        if (fields != null && !fields.isEmpty()) {
            String finalKeyPrefix = keyPrefix;
            fields.forEach(field -> resolveFieldValue(object, field, finalKeyPrefix));
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
        }
    }

    private void resolvePropertyFieldValue(DebbieBeanInfo<?> beanInfo) {
        Object object = beanInfo.getBean();
        List<Field> fields = beanInfo.getFields();
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
            fields.forEach(field -> resolvePropertyFieldValue(object, field, finalKeyPrefix));
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
        LOGGER.debug("resolve field dependent bean(" + field.getType() + ") by name : " + name);
        var value = factory(name, field.getType(), beanInject.require());
        if (value != null) {
            ReflectionHelper.setField(object, field, value);
        } else {
            if (beanInject.require()) {
                throw new NoBeanException("no bean " + name + " found .");
            }
        }
    }

    private void resolveFieldDependentBean(Object object, Field field, BeanInject beanInject) {
        LOGGER.debug("resolve field dependent bean(" + field.getType() + ") by type ");
        String name = beanInject.name();
        if (name.isBlank()) {
            name = beanInject.value();
        }
        resolveFieldDependentBean(name, object, field, beanInject);
    }

    public Object getParameterBean(Parameter parameter) {
        LOGGER.debug("resolve parameter dependent bean(" + parameter.getType() + ") by type ");
        var beanInject = parameter.getAnnotation(BeanInject.class);
        if (beanInject != null) {
            String name = beanInject.name();
            if (name.isBlank()) {
                name = beanInject.value();
            }
            Class<?> type = parameter.getType();
            if (!name.isBlank()) {
                var value = factory(name, type, beanInject.require());
                if (value != null) {
                    return value;
                } else {
                    if (beanInject.require()) {
                        throw new NoBeanException("no bean " + name + " found .");
                    }
                }
            } else {
                var value = factory(null, type, beanInject.require());
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
            /*try {
                Class injectClass = Class.forName("javax.inject.Inject");
                var inject = parameter.getAnnotation(injectClass);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }*/
        }
        throw new NoBeanException("no bean " + parameter.getName() + " found .");
    }

    public <Bean> BeanInvoker<Bean> factoryBeanInvoker(Class<Bean> beanClass) {
        return new BeanInvoker<>(beanClass, this);
    }

    public <T> T factoryNoLimit(Class<T> type) {
        LOGGER.debug("resolve bean(" + type + ") by type ");
        var beanBeanInvoker = new BeanInvoker<>(type, this);
        var bean = beanBeanInvoker.getBean();
        var classInfo = beanBeanInvoker.getBeanInfo();
        resolveDependentBean(bean, classInfo);
        return bean;
    }

    @SuppressWarnings("unchecked")
    private <T> BeanInvoker<T> setIfNotExist(DebbieBeanInfo<T> beanInfo) {
        if (beanInfo.getBeanType() == BeanType.SINGLETON) {
            if (singletonBeanInvokerMap.containsKey(beanInfo)) {
                return singletonBeanInvokerMap.get(beanInfo);
            } else {
                BeanInvoker<T> beanInvoker = new BeanInvoker<>(beanInfo, this);
                singletonBeanInvokerMap.put(beanInfo, beanInvoker);
                return beanInvoker;
            }
        } else {
            return new BeanInvoker<>(beanInfo, this);
        }

    }

    public <T> T factoryNoLimit(DebbieBeanInfo<T> beanInfo) {
        BeanInvoker<T> beanInvoker = setIfNotExist(beanInfo);
        var bean = beanInvoker.getBean();
        if (bean == null) {
            // resolve second level beans
            Collection<DebbieBeanInfo> constructorBeanDependent = beanInfo.getConstructorBeanDependent().values();
            for (DebbieBeanInfo<?> debbieBeanInfo : constructorBeanDependent) {
                if ((debbieBeanInfo.getBean() == null || !debbieBeanInfo.isHasVirtualValue())) {
                    beanInfo.setHasVirtualValue(true);
                    factoryNoLimit(debbieBeanInfo);
                }
            }
            if (beanInfo.getConstructorBeanDependent().isEmpty() || beanInfo.isConstructorBeanDependentHasValue()) {
                beanInvoker.createBeanByConstructorDependent();
                bean = beanInvoker.getBean();
                beanInfo = beanInvoker.getBeanInfo();
                if (bean != null) {
                    beanInfo.setBean(bean);
                    beanInfo.setHasVirtualValue(false);
                }
            }
        }
        if (bean == null) {
            beanInvoker.createBeanByConstructorDependent();
            bean = beanInvoker.getBean();
            beanInfo = beanInvoker.getBeanInfo();
            if (bean != null) {
                beanInfo.setBean(bean);
                if (beanInfo.isHasVirtualValue()) {
                    beanInfo.setHasVirtualValue(false);
                }
            }
        }

        bean = beanInvoker.getBean();
        if (bean != null) {
            beanInvoker.getFieldsDependent(this);
            beanInfo.setBean(bean);
            resolvePropertyFieldValue(beanInfo);
        }

        // bean = beanInvoker.getBean();
        // beanInfo.setBean(bean);

        return bean;
    }

    public <T> Object factoryAndInvokeMethod(Class<T> type, Method routerMethod, Object[] parameters) {
        T bean = factoryNoLimit(type);
        return BeanInvoker.invokeMethod(bean, routerMethod, parameters);
    }

    public <T, K extends T> T factoryWithProxy(Class<K> type, Class<T> interfaceType, DebbieBeanInfo<T> beanInfo) {
        LOGGER.debug("resolve field dependent bean(" + interfaceType + ") by implement class " + type);
        factoryNoLimit(beanInfo);
        resolveFieldBeans(beanInfo);
        return InterfaceDynamicProxy.doJdkProxy(this, interfaceType, beanInfo.getBean());
    }

    public static final Logger LOGGER = LoggerFactory.getLogger(BeanFactoryHandler.class);

}
