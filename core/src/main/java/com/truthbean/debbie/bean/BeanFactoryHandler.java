package com.truthbean.debbie.bean;

import com.truthbean.debbie.data.transformer.DataTransformer;
import com.truthbean.debbie.io.ResourceResolver;
import com.truthbean.debbie.properties.BaseProperties;
import com.truthbean.debbie.properties.DebbieConfigurationFactory;
import com.truthbean.debbie.properties.PropertiesConfiguration;
import com.truthbean.debbie.properties.PropertyInject;
import com.truthbean.debbie.proxy.*;
import com.truthbean.debbie.proxy.asm.AsmProxy;
import com.truthbean.debbie.reflection.ClassInfo;
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

    private final Set<DebbieBeanInfo<?>> beanServiceInfoSet = new HashSet<>();

    private final Map<DebbieBeanInfo<?>, BeanInvoker<?>> singletonBeanInvokerMap = new HashMap<>();

    private final BeanInitialization beanInitialization;
    private final DebbieConfigurationFactory configurationFactory;
    private final MethodProxyHandlerRegister methodProxyHandlerRegister;

    private final ClassLoader classLoader;
    private final ResourceResolver resourceResolver;

    private Class<?> injectType;

    private static final Object object = new Object();

    protected BeanFactoryHandler(ClassLoader classLoader) {
        synchronized (object) {
            resourceResolver = new ResourceResolver();
            beanInitialization = BeanInitialization.getInstance(classLoader, resourceResolver);
            configurationFactory = new DebbieConfigurationFactory(this);
            methodProxyHandlerRegister = new MethodProxyHandlerRegister();

            this.classLoader = classLoader;

            try {
                this.injectType = Class.forName("javax.inject.Inject");
            } catch (ClassNotFoundException e) {
                LOGGER.info("class javax.inject.Inject not found");
            }
        }
    }

    public ClassLoader getClassLoader() {
        return classLoader;
    }

    public ResourceResolver getResourceResolver() {
        return resourceResolver;
    }

    public Class<?> getInjectType() {
        return injectType;
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
                @SuppressWarnings("unchecked") var annotation = (Class<? extends Annotation>) clazz;
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

    public void autoCreateSingletonBeans() {
        beanServiceInfoSet.forEach(i -> {
            Boolean lazyCreate = i.getLazyCreate();
            if (lazyCreate != null && !lazyCreate && i.getBeanType() == BeanType.SINGLETON) {
                i.setBean(factory(i.getServiceName()));
                beanInitialization.refreshBean(i);
            }
        });
    }

    public void autoCreateBeans() {
        for (DebbieBeanInfo<?> beanInfo : beanServiceInfoSet) {
            Boolean lazyCreate = beanInfo.getLazyCreate();
            if (lazyCreate != null && !lazyCreate) {
                beanInfo.setBean(factory(beanInfo.getServiceName()));
                beanInitialization.refreshBean(beanInfo);
            }
        }
    }

    public void destroy(DebbieBeanInfo<?> beanInfo) {
        beanServiceInfoSet.remove(beanInfo);
        singletonBeanInvokerMap.remove(beanInfo);
    }

    public void release(String... args) {
        // must do nothing
    }

    private synchronized void destroyBeans(Collection<DebbieBeanInfo<?>> beans) {
        if (beans != null && !beans.isEmpty()) {
            for (DebbieBeanInfo<?> bean : beans) {
                LOGGER.trace("release bean " + bean.getBeanClass() + " with name " + bean.getServiceName());
                bean.release();
            }
        }
    }

    protected void releaseBeans() {
        destroyBeans(beanServiceInfoSet);
        destroyBeans(singletonBeanInvokerMap.keySet());
        beanServiceInfoSet.clear();
        singletonBeanInvokerMap.clear();
        beanInitialization.reset();
        resourceResolver.cleanResources();
        LOGGER.info("release all bean.");
    }

    private <T> DebbieBeanInfo<T> getBeanInfo(String serviceName, Class<T> type, boolean require, Set<DebbieBeanInfo<?>> beanInfoSet) {
        List<DebbieBeanInfo<?>> list = new ArrayList<>();
        if (serviceName != null && !serviceName.isBlank()) {
            for (DebbieBeanInfo<?> debbieBeanInfo : beanInfoSet) {
                if (serviceName.equals(debbieBeanInfo.getServiceName())) {
                    list.add(debbieBeanInfo);
                }
            }
        }

        if (list.isEmpty()) {
            if (type != null) {
                for (DebbieBeanInfo<?> debbieBeanInfo : beanInfoSet) {
                    var flag = type.getName().equals(debbieBeanInfo.getBeanClass().getName()) || (debbieBeanInfo.getBeanInterface() != null && type.getName().equals(debbieBeanInfo.getBeanInterface().getName()));
                    if (flag) {
                        list.add(debbieBeanInfo);
                    }
                }

                if (list.isEmpty()) {
                    for (DebbieBeanInfo<?> debbieBeanInfo : beanInfoSet) {
                        var flag = type.isAssignableFrom(debbieBeanInfo.getBeanClass()) || (debbieBeanInfo.getBeanInterface() != null && type.isAssignableFrom(debbieBeanInfo.getBeanInterface()));
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

        @SuppressWarnings("unchecked") DebbieBeanInfo<T> beanInfo = (DebbieBeanInfo<T>) list.get(0);
        if (type == null || type.isAssignableFrom(beanInfo.getBeanClass())) {
            if (beanInfo.getBeanType() == BeanType.SINGLETON) {
                return beanInfo;
            } else {
                return beanInfo.copy();
            }
        }
        throw new NoBeanException("bean " + type + " not found");
    }

    private <T, K extends T> List<DebbieBeanInfo<K>> getBeanInfoList(Class<T> type, boolean require, Set<DebbieBeanInfo<?>> beanInfoSet) {
        List<DebbieBeanInfo<?>> list = new ArrayList<>();

        if (type != null) {
            for (DebbieBeanInfo<?> debbieBeanInfo : beanInfoSet) {
                var flag = type.getName().equals(debbieBeanInfo.getBeanClass().getName()) || (debbieBeanInfo.getBeanInterface() != null && type.getName().equals(debbieBeanInfo.getBeanInterface().getName()));
                if (flag) {
                    list.add(debbieBeanInfo);
                }
            }

            if (list.isEmpty()) {
                for (DebbieBeanInfo<?> debbieBeanInfo : beanInfoSet) {
                    var flag = type.isAssignableFrom(debbieBeanInfo.getBeanClass()) || (debbieBeanInfo.getBeanInterface() != null && type.isAssignableFrom(debbieBeanInfo.getBeanInterface()));
                    if (flag) {
                        list.add(debbieBeanInfo);
                    }
                }
            }

            if (list.size() == 0) {
                if (require) {
                    throw new NoBeanException(type.getName() + " not found");
                } else {
                    return null;
                }
            }

            List<DebbieBeanInfo<K>> result = new ArrayList<>();
            for (DebbieBeanInfo<?> beanInfo : list) {
                if (type.isAssignableFrom(beanInfo.getBeanClass())) {
                    @SuppressWarnings("unchecked")
                    DebbieBeanInfo<K> ele = (DebbieBeanInfo<K>) beanInfo;
                    if (beanInfo.getBeanType() == BeanType.SINGLETON) {
                        result.add(ele);
                    } else {
                        result.add(ele.copy());
                    }
                }
            }
            return result;
        }

        return null;
    }

    public <T> DebbieBeanInfo<T> getBeanInfo(String serviceName, Class<T> type, boolean require) {
        try {
            return getBeanInfo(serviceName, type, require, beanServiceInfoSet);
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
        return null;
    }

    protected <T> T factory(String serviceName, Class<T> type, boolean require) {
        var beanInfo = getBeanInfo(serviceName, type, require, beanServiceInfoSet);
        if (!require && beanInfo == null)
            return null;
        assert beanInfo != null;
        if (beanInfo.getBeanType() == BeanType.SINGLETON) {
            resolveFieldBeans(beanInfo);
            T bean = beanInfo.getBean();
            if (bean == null) {
                bean = factory(beanInfo);
                beanInfo.setBean(bean);
            }
            return bean;
        }

        return factory(beanInfo);
    }

    public <T> T factory(String serviceName) {
        LOGGER.trace("factory bean with name " + serviceName);
        return factory(serviceName, null, true);
    }

    public <T> T factoryByProxy(DebbieBeanInfo<T> beanInfo) {
        return beanInfo.getBean();
        /*MethodProxyHandlerHandler handler = new MethodProxyHandlerHandler(LOGGER);
        Map<Method, Set<Annotation>> methodWithAnnotations = beanInfo.getMethodWithAnnotations();
        if (methodWithAnnotations == null || methodWithAnnotations.isEmpty()) {
            return beanInfo.getBean();
        }
        MethodProxyResolver methodProxyResolver = new MethodProxyResolver(this, beanInfo);
        methodWithAnnotations.forEach((method, annotations) -> {
            List<MethodProxyHandler> methodProxyHandler = methodProxyResolver.getMethodProxyHandler(method, annotations);
            handler.addInterceptors(methodProxyHandler);
        });
        if (!handler.hasInterceptor()) {
            return beanInfo.getBean();
        }

        AsmProxy<T> asmProxy = new AsmProxy<>((Class<T>) beanInfo.getBeanClass(), handler, MethodProxy.class);
        return asmProxy.proxy(() -> beanInfo.getBean());*/
    }

    public <T, K extends T> T factory(DebbieBeanInfo<K> beanInfo) {
        if (beanInfo.getBeanType() == BeanType.SINGLETON) {
            resolveFieldBeans(beanInfo);
            var bean = beanInfo.getBean();
            if (bean != null) {
                return factoryByProxy(beanInfo);
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
            return factoryByProxy(beanInfo);
        }

        return factoryWithProxy(clazz, beanInterface, beanInfo);
    }

    public <T, K extends T> T factory(Class<T> type) {
        LOGGER.trace("factory bean with type " + type.getName());
        return factory(null, type, true);
    }

    public <T, K extends T> DebbieBeanInfo<T> getBeanInfo(Class<T> type) {
        LOGGER.trace("factory bean with type " + type.getName());
        var beanInfo = getBeanInfo(null, type, true, beanServiceInfoSet);
        if (beanInfo != null) {
            T bean = factory(beanInfo);
            DebbieBeanInfo<T> result = new DebbieBeanInfo<>(beanInfo.getBeanClass());
            result.setBean(bean);
            return result;
        }
        return null;
    }

    public <T, K extends T> List<K> getBeanList(Class<T> superType) {
        List<K> result = new ArrayList<>();
        LOGGER.trace("factory bean with type " + superType.getName());
        List<DebbieBeanInfo<K>> beanInfoList = getBeanInfoList(superType, false, beanServiceInfoSet);
        if (beanInfoList != null) {
            for (DebbieBeanInfo<K> beanInfo : beanInfoList) {
                K bean = factory(beanInfo);
                result.add(bean);
            }
        }
        return result;
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private <T> void resolveFieldBeans(DebbieBeanInfo<T> beanInfo) {
        Map<Field, DebbieBeanInfo<?>> fieldBeanDependents = beanInfo.getFieldBeanDependent();
        if (fieldBeanDependents != null && !fieldBeanDependents.isEmpty()) {
            Collection<DebbieBeanInfo<?>> fieldBeanDependent = fieldBeanDependents.values();
            for (DebbieBeanInfo debbieBeanInfo : fieldBeanDependent) {
                if ((debbieBeanInfo.getBean() == null || debbieBeanInfo.isHasVirtualValue())) {
                    Object object = getBeanInfo(debbieBeanInfo.getServiceName(), (Class<T>) debbieBeanInfo.getBeanClass(), true, beanServiceInfoSet).getBean();
                    if (object != null) {
                        debbieBeanInfo.setBean(object);
                    }
                }
            }

            BeanInvoker<T> beanInvoker;
            if (beanInfo.getBeanType() == BeanType.SINGLETON) {
                beanInvoker = (BeanInvoker<T>) singletonBeanInvokerMap.get(beanInfo);
            } else {
                beanInvoker = BeanInvoker.create(beanInfo, this, singletonBeanInvokerMap);
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

    @SuppressWarnings("unchecked")
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
            Class injectClass = getInjectType();
            if (injectClass == null) return;
            Object inject = field.getAnnotation(injectClass);
            if (inject != null) {
                resolveFieldDependentBean(null, object, field, inject);
            }
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
        LOGGER.trace("resolve field dependent bean(" + field.getType() + ") by name : " + name);
        var value = factory(name, field.getType(), beanInject.require());
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
        LOGGER.trace("resolve field dependent bean(" + field.getType() + ") by name : " + name);
        var value = factory(name, field.getType(), true);
        if (value != null) {
            ReflectionHelper.setField(object, field, value);
        } else {
            throw new NoBeanException("no bean " + name + " found .");
        }
    }

    private void resolveFieldDependentBean(Object object, Field field, BeanInject beanInject) {
        LOGGER.trace("resolve field dependent bean(" + field.getType() + ") by type ");
        String name = beanInject.name();
        if (name.isBlank()) {
            name = beanInject.value();
        }
        resolveFieldDependentBean(name, object, field, beanInject);
    }

    public Object getParameterBean(Parameter parameter) {
        LOGGER.trace("resolve parameter dependent bean(" + parameter.getType() + ") by type ");
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
        }
        throw new NoBeanException("no bean " + parameter.getName() + " found .");
    }

    public <Bean> BeanInvoker<Bean> factoryBeanInvoker(Class<Bean> beanClass) {
        return new BeanInvoker<>(beanClass, this);
    }

    public <T> T factoryNoLimit(Class<T> type) {
        LOGGER.trace("resolve bean(" + type + ") by type ");
        var beanBeanInvoker = new BeanInvoker<>(type, this);
        var bean = beanBeanInvoker.getBean();
        var classInfo = beanBeanInvoker.getBeanInfo();
        resolveDependentBean(bean, classInfo);
        return bean;
    }

    public <T> T getBeanByFactory(DebbieBeanInfo<T> beanInfo) {
        Class<T> beanClass = beanInfo.getBeanClass();
        BeanFactory<T> beanFactory = beanInfo.getBeanFactory();
        return getBeanByFactory(beanClass, beanFactory);
    }

    public <T> T getBeanByFactory(Class<T> beanClass, BeanFactory<T> beanFactory) {
        if (beanClass.isInterface() || Modifier.isAbstract(beanClass.getModifiers())) {
            if (beanFactory != null) {
                return beanFactory.getBean();
            } else {
                return factory(beanClass);
            }
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    public <T> T getBeanByInitMethod(Method initMethod, BeanFactoryHandler handler) {
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

    @SuppressWarnings("unchecked")
    private <T> BeanInvoker<T> setIfNotExist(DebbieBeanInfo<T> beanInfo) {
        if (beanInfo.getBeanType() == BeanType.SINGLETON) {
            if (singletonBeanInvokerMap.containsKey(beanInfo)) {
                return (BeanInvoker<T>) singletonBeanInvokerMap.get(beanInfo);
            } else {
                BeanInvoker<T> beanInvoker = new BeanInvoker<>(beanInfo, this);
                singletonBeanInvokerMap.put(beanInfo, beanInvoker);
                return beanInvoker;
            }
        } else {
            return BeanInvoker.create(beanInfo, this, singletonBeanInvokerMap);
        }
    }

    public <T> BeanInvoker<T> resolveSecondLevelBeans(DebbieBeanInfo<T> beanInfo) {
        BeanInvoker<T> beanInvoker = setIfNotExist(beanInfo);
        var bean = beanInvoker.getBean();
        if (beanInfo.getBeanType() == BeanType.SINGLETON && bean == null) {
            // resolve second level beans
            beanInvoker.resolveConstructor(this, singletonBeanInvokerMap);
        }
        return beanInvoker;
    }

    public <T> T factoryNoLimit(DebbieBeanInfo<T> beanInfo) {
        BeanInvoker<T> beanInvoker = resolveSecondLevelBeans(beanInfo);
        var bean = beanInvoker.getBean();
        if (bean == null) {
            // resolve third level beans
            Collection<DebbieBeanInfo<?>> constructorBeanDependent = beanInfo.getConstructorBeanDependent().values();
            for (DebbieBeanInfo<?> debbieBeanInfo : constructorBeanDependent) {
                if ((debbieBeanInfo.getBean() == null || !debbieBeanInfo.isHasVirtualValue())) {
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
            beanInvoker.getFieldsDependent(this);
            beanInfo.setBean(bean);
            resolvePropertyFieldValue(beanInfo);
        }

        return bean;
    }

    public <T> Object factoryAndInvokeMethod(Class<T> type, Method routerMethod, Object[] parameters) {
        T bean = factoryNoLimit(type);
        return ReflectionHelper.invokeMethod(bean, routerMethod, parameters);
    }

    public <T, K extends T> T factoryWithProxy(Class<K> type, Class<T> interfaceType, DebbieBeanInfo<K> beanInfo) {
        LOGGER.trace("resolve field dependent bean(" + interfaceType + ") by implement class " + type);
        factoryNoLimit(beanInfo);
        resolveFieldBeans(beanInfo);
        JdkDynamicProxy<T, K> dynamicProxy = new JdkDynamicProxy<>();
        return dynamicProxy.invokeJdkProxy(this, interfaceType, beanInfo.getBean());
    }

    public static final Logger LOGGER = LoggerFactory.getLogger(BeanFactoryHandler.class);

}
