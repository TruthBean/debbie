/**
 * Copyright (c) 2022 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 * http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.internal;

import com.truthbean.Console;
import com.truthbean.Logger;
import com.truthbean.LoggerFactory;
import com.truthbean.debbie.annotation.AnnotationInfo;
import com.truthbean.debbie.bean.*;
import com.truthbean.debbie.core.ApplicationContext;
import com.truthbean.debbie.core.ApplicationContextAware;
import com.truthbean.debbie.env.EnvContentAware;
import com.truthbean.debbie.env.EnvironmentContent;
import com.truthbean.debbie.event.DebbieEventPublisherAware;
import com.truthbean.debbie.properties.DebbieConfiguration;
import com.truthbean.debbie.properties.DebbieProperties;
import com.truthbean.debbie.properties.PropertyInject;
import com.truthbean.debbie.proxy.javaassist.JavaassistProxyBean;
import com.truthbean.debbie.reflection.ClassInfo;
import com.truthbean.debbie.reflection.TypeHelper;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * @author TruthBean
 * @since 0.0.1
 * Created on 2019/3/23 21:17.
 */
final class DebbieBeanCenter implements BeanInfoManager {
    private final EnvironmentContent content;

    DebbieBeanCenter(EnvironmentContent content) {
        this.content = content;

        injectTypes.add(BeanInject.class);
        injectTypes.add(PropertyInject.class);
        registerInjectType("javax.inject.Inject");
        registerInjectType("jakarta.inject.Inject");
        registerInjectType("javax.annotation.Resource");
        registerInjectType("jakarta.annotation.Resource");

        beanRegisters.add(new DefaultBeanRegister());
        this.ignoredInterfaces.add(DebbieProperties.class);
        this.ignoredInterfaces.add(DebbieConfiguration.class);
        this.ignoredInterfaces.add(ApplicationContextAware.class);
        this.ignoredInterfaces.add(ClassLoaderAware.class);
        this.ignoredInterfaces.add(JavaassistProxyBean.class);
        this.ignoredInterfaces.add(DebbieEventPublisherAware.class);
        this.ignoredInterfaces.add(GlobalBeanFactoryAware.class);
        this.ignoredInterfaces.add(EnvContentAware.class);
    }

    public static final Object VALUE = new Object();

    private final Set<Class<? extends Annotation>> injectTypes = new HashSet<>(2);
    private final SortedSet<BeanRegister> beanRegisters = new TreeSet<>();

    private static final Map<Class<? extends Annotation>, BeanComponentParser> BEAN_ANNOTATION = new LinkedHashMap<>();

    private static final Map<Class<?>, Set<BeanInfo>> BEAN_CLASSES = new ConcurrentHashMap<>();
    private static final ConcurrentMap<BeanInfo, Object> CLASS_INFO_SET = new ConcurrentHashMap<>();

    private static final Set<Class<? extends Annotation>> CLASS_ANNOTATION = new HashSet<>();
    private static final Map<Class<? extends Annotation>, Map<Class<?>, Set<Method>>> BEAN_CLASS_METHOD_MAP = new HashMap<>();

    private static final Set<Class<? extends Annotation>> METHOD_ANNOTATION = new HashSet<>();
    private static final Map<Class<? extends Annotation>, Set<BeanInfo>> ANNOTATION_METHOD_BEANS = new HashMap<>();

    private final ConcurrentMap<BeanInfo, Object> handledBeanInfoSet = new ConcurrentHashMap<>();

    private final Set<BeanLifecycle> beanLifecycles = new HashSet<>();

    private final Set<Class<?>> ignoredInterfaces = new HashSet<>();
    private final Set<Class<? extends Annotation>> ignoredAnnotations = new HashSet<>();

    private Class<? extends Annotation> injectType;

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
    public void addIgnoreInterface(Class<?> ignoreInterface) {
        this.ignoredInterfaces.add(ignoreInterface);
    }

    @Override
    public Set<Class<?>> getIgnoreInterface() {
        return ignoredInterfaces;
    }

    @Override
    public void addIgnoreAnnotation(Class<? extends Annotation> annotation) {
        ignoredAnnotations.add(annotation);
    }

    @Override
    public Set<Class<? extends Annotation>> getIgnoredAnnotations() {
        return ignoredAnnotations;
    }

    @Override
    public Set<Class<? extends Annotation>> getInjectTypes() {
        return new HashSet<>(injectTypes);
    }

    @Override
    public Class<? extends Annotation> getInjectType() {
        return this.injectType;
    }

    @Override
    public boolean hasInjectType(AnnotatedElement annotatedElement, boolean another) {
        for (Class<? extends Annotation> injectType : injectTypes) {
            Annotation parameterAnnotation = annotatedElement.getAnnotation(injectType);
            if (parameterAnnotation != null) {
                return true;
            }
        }
        return another;
    }

    @Override
    public boolean injectedRequired(Annotation annotation, boolean another) {
        for (Class<? extends Annotation> injectType : injectTypes) {
            if (annotation instanceof BeanInject) {
                return ((BeanInject) annotation).require();
            }
            if (injectType.isInstance(annotation)) {
                return true;
            }
        }
        return another;
    }

    @Override
    public Boolean injectBeanRequiredIfPresent(AnnotatedElement annotatedElement, boolean another) {
        for (Class<? extends Annotation> injectType : injectTypes) {
            Annotation parameterAnnotation = annotatedElement.getAnnotation(injectType);
            if (parameterAnnotation instanceof BeanInject) {
                return ((BeanInject) parameterAnnotation).require();
            }
            if (injectType.isInstance(parameterAnnotation) && injectType != PropertyInject.class) {
                return true;
            }
        }
        Annotation[] annotations = annotatedElement.getAnnotations();
        if (annotations == null || annotations.length == 0 || annotations[0] == null) {
            return another;
        }
        return null;
    }

    @Override
    public <A extends Annotation> void registerBeanAnnotation(Class<A> annotationType, BeanComponentParser parser) {
        BEAN_ANNOTATION.put(annotationType, parser);
    }

    @Override
    public void registerBeanRegister(BeanRegister beanRegister) {
        beanRegisters.add(beanRegister);
    }

    @Override
    public Set<Class<? extends Annotation>> getBeanAnnotations() {
        return Collections.unmodifiableSet(BEAN_ANNOTATION.keySet());
    }

    @Override
    @SuppressWarnings({"unchecked", "rawtypes"})
    public synchronized boolean registerBeanInfo(BeanInfo<?> baseBeanInfo) {
        if (baseBeanInfo == null) {
            return false;
        }
        Set<BeanCondition> conditions = baseBeanInfo.getConditions();
        for (BeanCondition condition : conditions) {
            if (!condition.matches(content)) {
                return false;
            }
        }
        Class<?> beanClass = baseBeanInfo.getBeanClass();
        /*Set<BeanInfo> list = new HashSet<>();

        if (BEAN_CLASSES.containsKey(beanClass)) {
            // merge
            Set<BeanInfo> set = BEAN_CLASSES.get(beanClass);
            for (BeanInfo info : set) {
                *//*if (info instanceof MutableFactoryBeanInfo beanInfo) {

                    // add bean alias name
                    // TODO if singleton, set order
                    if (baseBeanInfo.isSingleton() &&
                            (beanInfo.isSingleton() || beanInfo.getBeanType() == null)
                            && (beanInfo.isEmpty() && beanInfo.getBeanFactory() == null)) {
                        beanInfo.addBeanName(baseBeanInfo.getBeanNames());
                        beanInfo.setBeanType(BeanType.SINGLETON);
                        if (baseBeanInfo instanceof FactoryBeanInfo localFactoryBeanInfo) {
                            if (localFactoryBeanInfo.isPresent() && beanInfo.isEmpty()) {
                                beanInfo.setBean(localFactoryBeanInfo.getBean());
                            }
                            if (localFactoryBeanInfo.hasBeanFactory() && !beanInfo.hasBeanFactory()) {
                                beanInfo.setBeanFactory(localFactoryBeanInfo.getBeanFactory());
                            }
                        }
                        return;
                    } else if ((beanInfo.getBeanType() == BeanType.NO_LIMIT || beanInfo.getBeanType() == null)
                            && baseBeanInfo.getBeanType() == BeanType.NO_LIMIT) {
                        beanInfo.addBeanName(baseBeanInfo.getBeanNames());
                        if (baseBeanInfo instanceof FactoryBeanInfo localFactoryBeanInfo) {
                            if (localFactoryBeanInfo.hasBeanFactory() && !beanInfo.hasBeanFactory()) {
                                beanInfo.setBeanFactory(localFactoryBeanInfo.getBeanFactory());
                            }
                        }
                        return;
                    }
                } else *//*if (info instanceof RegistrableBeanInfo beanFactory) {
                    // add bean alias name
                    // TODO if singleton, set order
                    if (baseBeanInfo.isSingleton() &&
                            (beanFactory.isSingleton() || beanFactory.getBeanType() == null)
                            && !beanFactory.isCreated()) {
                        beanFactory.addBeanName(baseBeanInfo.getBeanNames());
                        beanFactory.setBeanType(BeanType.SINGLETON);
                        if (baseBeanInfo instanceof MutableBeanFactory localBeanFactory) {
                            if (localBeanFactory.isCreated() && !localBeanFactory.isCreated()) {
                                // todo
                                localBeanFactory.setBean(localBeanFactory.getCreatedBean());
                            }
                        }
                        return;
                    } else if ((baseBeanInfo.getBeanType() == BeanType.NO_LIMIT || baseBeanInfo.getBeanType() == null)
                            && beanFactory.getBeanType() == BeanType.NO_LIMIT) {
                        beanFactory.addBeanName(baseBeanInfo.getBeanNames());
                    }
                }
                // 过滤重复项
                if (!list.contains(info)) {
                    list.add(info);
                }
            }
        } else {
            list = new HashSet<>();
        }*/

        Set<BeanInfo> set = BEAN_CLASSES.get(beanClass);
        if (set != null) {
            for (BeanInfo info : set) {
                if (info.containAllName(baseBeanInfo.getBeanNames())) {
                    LOGGER.warn(() -> "class " + beanClass.getName() + " with bean name " + baseBeanInfo.getBeanNames() + " has bean registered.");
                    return false;
                }
                String s = info.containOneName(baseBeanInfo.getBeanNames());
                if (s != null) {
                    LOGGER.warn(() -> "class " + beanClass.getName() + " with bean name " + baseBeanInfo.getBeanNames() + " conflict with registered bean " + s);
                    return false;
                }
            }
            if (!set.contains(baseBeanInfo)) {
                LOGGER.trace(() -> "register class " + beanClass.getName() + " with bean name " + baseBeanInfo.getBeanNames());
                set.add(baseBeanInfo);
            }
        } else {
            set = new HashSet<>();
            LOGGER.trace(() -> "register class " + beanClass.getName() + " with bean name " + baseBeanInfo.getBeanNames());
            set.add(baseBeanInfo);
        }
        BEAN_CLASSES.put(beanClass, set);

        CLASS_INFO_SET.put(baseBeanInfo, VALUE);

        if (baseBeanInfo instanceof ClassDetailedBeanInfo detailedBeanInfo) {
            Map<Method, Set<Annotation>> methodWithAnnotations = detailedBeanInfo.getMethodWithAnnotations();
            methodWithAnnotations.forEach((method, annotations) -> {
                if (annotations != null) {
                    for (Annotation annotation : annotations) {
                        var methodAnnotation = annotation.annotationType();
                        if (methodAnnotation != Override.class && methodAnnotation != Deprecated.class && methodAnnotation != SuppressWarnings.class) {
                            METHOD_ANNOTATION.add(methodAnnotation);
                        }

                        var annotationMethodBeans = ANNOTATION_METHOD_BEANS.computeIfAbsent(methodAnnotation, k -> new HashSet<>());
                        annotationMethodBeans.add(baseBeanInfo);
                    }
                }
            });

            Map<Class<? extends Annotation>, AnnotationInfo> classAnnotation = detailedBeanInfo.getClassAnnotations();
            if (!classAnnotation.isEmpty()) {
                var annotations = classAnnotation.keySet();

                classAnnotation.forEach((type, annotation) -> {
                    if (TypeHelper.filterAnnotation(type)) {
                        CLASS_ANNOTATION.add(type);
                    }
                });

                annotations.stream().filter(beanClass::isAnnotationPresent).forEach(annotation -> {
                    var classMethodMap = BEAN_CLASS_METHOD_MAP.get(annotation);
                    if (classMethodMap == null) {
                        classMethodMap = new HashMap<>();
                    }
                    classMethodMap.put(beanClass, methodWithAnnotations.keySet());
                    BEAN_CLASS_METHOD_MAP.put(annotation, classMethodMap);
                });
            }
        }

        handledBeanInfoSet.put(baseBeanInfo, VALUE);
        return true;
    }

    @Override
    public void registerBeanLifecycle(BeanLifecycle beanLifecycle) {
        this.beanLifecycles.add(beanLifecycle);
    }

    @Override
    public Set<BeanLifecycle> getBeanLifecycles() {
        // return new HashSet<>(beanLifecycles);
        return beanLifecycles;
    }

    @Override
    public Set<Class<? extends Annotation>> getClassAnnotation() {
        return CLASS_ANNOTATION;
    }

    @Override
    public Set<Class<? extends Annotation>> getMethodAnnotation() {
        return METHOD_ANNOTATION;
    }

    @Override
    @SuppressWarnings("unchecked")
    public synchronized <Bean> void refresh(BeanInfo<Bean> info) {
        Class<Bean> beanClass = (Class<Bean>) info.getBeanClass();
        LOGGER.trace(() -> "refresh class " + beanClass.getName());

        final Set<BeanInfo> list = BEAN_CLASSES.get(beanClass);
        if (list == null || list.isEmpty()) {
            throw new NoBeanException("bean " + beanClass + " has not registered. ");
        }
        /*if (info instanceof MutableFactoryBeanInfo<Bean> mutableBeanInfo1) {
            for (BeanInfo beanInfo : list) {
                if (beanInfo instanceof MutableFactoryBeanInfo) {
                    MutableFactoryBeanInfo<Bean> mutableBeanInfo = (MutableFactoryBeanInfo<Bean>) beanInfo;
                    mutableBeanInfo.setBean(mutableBeanInfo1.getBean());
                    mutableBeanInfo.setBeanFactory(mutableBeanInfo1.getBeanFactory());
                }
            }
        } else*/ if (info instanceof MutableBeanFactory<Bean> mutableBeanFactory) {
            for (BeanInfo beanInfo : list) {
                if (beanInfo instanceof MutableBeanFactory) {
                    MutableBeanFactory<Bean> mutableBeanInfo = (MutableBeanFactory<Bean>) beanInfo;
                    if (mutableBeanFactory.isCreated()) {
                        // todo
                        mutableBeanInfo.setBean(mutableBeanFactory.getCreatedBean());
                    }
                }
            }
        }
    }

    // @Override
    public void refreshBeans() {
        // 重新计算hashcode，因为map的key存的是最开始put进去的值的hashcode，但是key更新的话，hashcode并没有更新
        Map<BeanInfo, Object> copy = new ConcurrentHashMap<>(handledBeanInfoSet);

        var registeredBeans = this.getRegisteredBeans();
        for (BeanInfo registeredBean : registeredBeans) {
            copy.put(registeredBean, VALUE);
        }

        var beanServiceInfoList = this.getAnnotatedBeans();

        Set<Class<? extends Annotation>> beanAnnotations = new CopyOnWriteArraySet<>(this.getBeanAnnotations());
        beanAnnotations.forEach((annotationType) -> {
            var annotatedClass = this.getAnnotatedClass(annotationType);
            for (BeanInfo<?> beanInfo : annotatedClass) {
                copy.put(beanInfo, VALUE);
            }
        });

        beanServiceInfoList.forEach((i) -> {
            /*if (i instanceof DebbieClassFactoryBeanInfo classBeanInfo) {
                var clazz = classBeanInfo.getClazz();
                if (clazz.isAnnotation()) {
                    @SuppressWarnings("unchecked") var annotation = (Class<? extends Annotation>) clazz;
                    var set = this.getAnnotatedClass(annotation);
                    for (BeanInfo<?> beanInfo : set) {
                        copy.put(beanInfo, VALUE);
                    }

                } else {
                    var beanFactory = classBeanInfo.getBeanFactory();
                    if (beanFactory != null) {
                        copy.put(i, VALUE);
                    } else if (clazz.isInterface()) {
                        var beans = this.getBeansByInterface(clazz);
                        for (BeanInfo beanInfo : beans) {
                            copy.put(beanInfo, VALUE);
                        }
                    } else if (Modifier.isAbstract(classBeanInfo.getClazz().getModifiers())) {
                        var beans = this.getBeanByAbstractSuper(clazz);
                        for (BeanInfo<?> beanInfo : beans) {
                            copy.put(beanInfo, VALUE);
                        }
                    } else {
                        copy.put(i, VALUE);
                    }
                }
            } else*/ if (i instanceof ClassBeanInfo classBeanInfo) {
                Class<?> clazz = classBeanInfo.getClazz();
                if (clazz.isAnnotation()) {
                    @SuppressWarnings("unchecked") var annotation = (Class<? extends Annotation>) clazz;
                    var set = this.getAnnotatedClass(annotation);
                    for (BeanInfo<?> beanInfo : set) {
                        copy.put(beanInfo, VALUE);
                    }

                } else if (clazz.isInterface()) {
                    var beans = this.getBeansByInterface(clazz);
                    for (BeanInfo<?> beanInfo : beans) {
                        copy.put(beanInfo, VALUE);
                    }
                } else if (Modifier.isAbstract(classBeanInfo.getClazz().getModifiers())) {
                    var beans = this.getBeanByAbstractSuper(clazz);
                    for (BeanInfo<?> beanInfo : beans) {
                        copy.put(beanInfo, VALUE);
                    }
                } else {
                    copy.put(i, VALUE);
                }
            }
        });
        synchronized (handledBeanInfoSet) {
            // 重新刷入set集合，更新hashcode
            handledBeanInfoSet.clear();
            handledBeanInfoSet.putAll(copy);
        }
    }

    @Override
    public boolean support(Class<?> beanClass) {
        if (beanClass == null) {
            return false;
        }
        if (beanClass.isEnum()) {
            return false;
        }
        if (beanClass.isAnnotation()) {
            return false;
        }
        for (Class<?> ignoredInterface : ignoredInterfaces) {
            if (beanClass == ignoredInterface) {
                return false;
            }
        }
        for (Class<? extends Annotation> ignoredAnnotation : ignoredAnnotations) {
            if (beanClass.isAnnotationPresent(ignoredAnnotation)) {
                return false;
            }
        }
        return beanClass.getAnnotation(NonBean.class) == null && !JavaassistProxyBean.class.isAssignableFrom(beanClass);
    }

    @Override
    @SuppressWarnings({"unchecked"})
    public synchronized <Bean> void register(final Class<Bean> clazz) {
        // todo resovle
        DataTransformerFactory.registerDataTransformer(clazz);

        if (clazz.isAnnotation()) {
            registerBeanComponentAnnotation((Class<? extends Annotation>) clazz);
            return;
        }
        if (support(clazz)) {
            try {
                ClassBeanInfo<?> beanInfo = new ClassBeanInfo<>(clazz, BEAN_ANNOTATION);
                if (beanInfo.getBeanType() == null) {
                    if (beanInfo.getBeanType() == null) {
                        return;
                    }
                }
                for (BeanRegister beanRegister : beanRegisters) {
                    if (beanRegister.support(beanInfo)) {
                        registerBeanInfo(beanRegister.getBeanFactory(beanInfo));
                        break;
                    }
                }
            } catch (Throwable e) {
                LOGGER.error("register class<" + clazz + "> error. ", e);
            }
        }
    }

    public static class DefaultBeanRegister implements BeanRegister {

        @Override
        public <Bean> boolean support(ClassBeanInfo<Bean> beanInfo) {
            final Map<Class<? extends Annotation>, AnnotationInfo> classAnnotations = beanInfo.getClassAnnotations();
            var value = classAnnotations.get(BeanComponent.class);
            if (value != null) {
                return true;
            }
            boolean hasComponentAnnotation = false;
            // resolve customize component annotation
            for (Map.Entry<Class<? extends Annotation>, BeanComponentParser> entry : BEAN_ANNOTATION.entrySet()) {
                var type = entry.getKey();
                if (classAnnotations.containsKey(type)) {
                    hasComponentAnnotation = true;
                    break;
                }
            }
            return hasComponentAnnotation;
        }

        @Override
        public <Bean> BeanFactory<Bean> getBeanFactory(ClassBeanInfo<Bean> beanInfo) {
            // TODO 根据 BeanComponentInfo 的 factory 创建 BeanFactory
            return new DebbieReflectionBeanFactory<>(beanInfo);
        }

        @Override
        public int getOrder() {
            return Integer.MAX_VALUE - 10;
        }

        @Override
        public int hashCode() {
            return super.hashCode();
        }

        @Override
        public boolean equals(Object obj) {
            return super.equals(obj);
        }
    }

    @Override
    public void registerClass(Class<?> beanClass) {
        try {
            if (beanClass.isAnonymousClass()) {
                if ("".equals(beanClass.getSimpleName())) {
                    Class<?>[] interfaces = beanClass.getInterfaces();
                    if (interfaces.length == 1) {
                        this.register(interfaces[0]);
                    } else {
                        Class<?> superclass = beanClass.getSuperclass();
                        if (superclass != Object.class) {
                            this.register(superclass);
                        }
                    }
                }
            } else {
                this.register(beanClass);
            }
        } catch (Throwable e) {
            if (e instanceof NoClassDefFoundError) {
                LOGGER.debug(e.getMessage());
            } else {
                LOGGER.error("", e);
            }
        }
    }

    private void registerBeanComponentAnnotation(Class<? extends Annotation> annotationType) {
        if (annotationType.getAnnotation(BeanComponent.class) != null) {
            registerBeanAnnotation(annotationType, new DefaultBeanComponentParser());
        }
    }

    @Override
    public Set<BeanInfo> getAllBeanInfo() {
        return Set.copyOf(handledBeanInfoSet.keySet());
    }

    @Override
    public Set<Method> getBeanMethods(Class<?> beanClass) {
        var classInfoSet = BEAN_CLASSES;
        if (!classInfoSet.containsKey(beanClass)) {
            LOGGER.error(() -> beanClass.getName() + " has not register");
            throw new NoBeanException(beanClass.getName() + " has not register");
        }
        Set<BeanInfo> list = classInfoSet.get(beanClass);
        for (BeanInfo beanInfo : list) {
            if (beanInfo instanceof ClassDetailedBeanInfo) {
                return ((ClassDetailedBeanInfo) beanInfo).getMethods();
            }
        }
        return new HashSet<>();
    }

    @Override
    public <T extends Annotation> Set<BeanInfo<?>> getAnnotatedClass(Class<T> annotationClass) {
        Set<BeanInfo> classInfoSet = CLASS_INFO_SET.keySet();

        Set<BeanInfo<?>> result = new HashSet<>();

        classInfoSet.stream()
                .filter(classInfo -> classInfo instanceof ClassInfo
                        && ((ClassInfo<?>) classInfo).getClassAnnotations().containsKey(annotationClass))
                .forEach(result::add);

        return result;
    }

    @Override
    public Set<BeanInfo<?>> getAnnotatedBeans() {
        Set<BeanInfo> classInfoSet = CLASS_INFO_SET.keySet();
        Set<BeanInfo<?>> result = new HashSet<>();

        for (Class<? extends Annotation> annotationType : getBeanAnnotations()) {
            for (BeanInfo classInfo : classInfoSet) {
                if (classInfo instanceof ClassInfo<?> beanInfo) {
                    if (beanInfo.getClassAnnotations().containsKey(annotationType)) {
                        result.add(classInfo);
                    }
                }
            }
        }

        return result;
    }

    /*@Override
    @SuppressWarnings("unchecked")
    public <Bean> BeanInfo<Bean> getRegisterRawBean(Class<Bean> bean) {
        Set<BeanInfo> list = BEAN_CLASSES.get(bean);
        if (list != null && !list.isEmpty()) {
            for (BeanInfo info : list) {
                if (info instanceof MutableFactoryBeanInfo) {
                    return (MutableFactoryBeanInfo<Bean>) info;
                }
            }
        }
        return null;
    }*/

    @Override
    public Collection<BeanInfo> getRegisteredBeans() {
        final Collection<Set<BeanInfo>> values = BEAN_CLASSES.values();
        List<BeanInfo> result = new ArrayList<>();
        for (Set<BeanInfo> list : values) {
            result.addAll(list);
        }
        return Collections.unmodifiableCollection(result);
    }

    /*Collection<BeanInfo<?>> getRegisterRawBeans() {
        Collection<Set<BeanInfo>> values = BEAN_CLASSES.values();
        Collection<BeanInfo<?>> result = new HashSet<>();
        for (Set<BeanInfo> list : values) {
            for (BeanInfo beanInfo : list) {
                if (beanInfo instanceof MutableFactoryBeanInfo) {
                    result.add((MutableFactoryBeanInfo<?>) beanInfo);
                }
            }
        }
        return Collections.unmodifiableCollection(result);
    }*/

    /*@Override
    public Set<Class<?>> getRegisteredRawBeanType() {
        Set<Class<?>> result = new HashSet<>();
        Collection<Class<?>> registerRawBeans = this.getRegisterRawBeanTypes();
        if (!registerRawBeans.isEmpty()) {
            result.addAll(registerRawBeans);
        }
        return result;
    }*/

    Collection<Class<?>> getRegisterRawBeanTypes() {
        return Collections.unmodifiableCollection(BEAN_CLASSES.keySet());
    }

    /*protected static List<Method> getAnnotatedMethodsInAnnotatedBean
            (Class<? extends Annotation> classAnnotation, Class<? extends Annotation> methodAnnotation) {

    }*/

    @Override
    public Set<BeanInfo<?>> getAnnotatedMethodsBean(Class<? extends Annotation> methodAnnotation) {
        Set<BeanInfo<?>> result = new HashSet<>();
        Set<BeanInfo> set = ANNOTATION_METHOD_BEANS.get(methodAnnotation);
        if (set != null) {
            for (BeanInfo beanInfo : set) {
                result.add(beanInfo);
            }
        }
        return result;
    }

    @Override
    public Set<BeanInfo> getBeansByInterface(Class interfaceType) {
        Set<BeanInfo> classInfoSet = new HashSet<>();

        BEAN_CLASSES.forEach((clazz, list) -> {
            Class<?>[] interfaces = clazz.getInterfaces();
            if (interfaces.length > 0) {
                for (Class<?> i : interfaces) {
                    for (BeanInfo beanInfo : list) {
                        if (i == interfaceType) {
                            classInfoSet.add(beanInfo);
                        }
                    }
                }
            }
        });

        return classInfoSet;
    }

    @Override
    public Set<BeanInfo<?>> getBeanByAbstractSuper(Class<?> abstractType) {
        Set<BeanInfo<?>> classInfoSet = new HashSet<>();

        BEAN_CLASSES.forEach((clazz, list) -> {
            Class<?> superclass = clazz.getSuperclass();
            if (superclass == abstractType) {
                for (BeanInfo beanInfo : list) {
                    classInfoSet.add(beanInfo);
                }
            }
        });

        return classInfoSet;
    }

    @Override
    // @SuppressWarnings({"unchecked", "rawtypes"})
    public void autoCreateSingletonBeans(ApplicationContext applicationContext) {
        GlobalBeanFactory globalBeanFactory = applicationContext.getGlobalBeanFactory();
        handledBeanInfoSet.forEach((i, value) -> {
            boolean lazyCreate = i.isLazyCreate();
            /*if (!lazyCreate && i.getBeanType() == BeanType.SINGLETON && i instanceof MutableFactoryBeanInfo beanInfo) {

                beanInfo.setBean(() -> {
                    if (beanInfo.isEmpty()) {
                        return globalBeanFactory.factory((FactoryBeanInfo<? extends Object>) i);
                    } else {
                        return beanInfo.getBean();
                    }
                });
                this.refresh(beanInfo);
            } else if (!lazyCreate && i.isSingleton()) {
                if (i instanceof FactoryBeanInfo) {
                    ((FactoryBeanInfo<?>) i).create(applicationContext);
                } else*/ if (i instanceof BeanFactory) {
                if (!applicationContext.isExiting()) {
                        Set<String> names = i.getBeanNames();
                        for (String name : names) {
                            ((BeanFactory<?>) i).factoryNamedBean(name, applicationContext);
                        }
                    }
                }

            /*}*/
        });
    }

    @Override
    public Set<BeanInfo> getLazyCreateBean() {
        Set<BeanInfo> result = new HashSet<>();
        for (BeanInfo beanInfo : handledBeanInfoSet.keySet()) {
            boolean lazyCreate = beanInfo.isLazyCreate();
            if (!lazyCreate) {
                result.add(beanInfo);
            }
        }
        return result;
    }

    @Override
    public <Bean> List<BeanInfo<? extends Bean>> getBeanInfoList(Class<Bean> type, boolean require) {
        return getBeanInfoList(type, require, handledBeanInfoSet.keySet());
    }

    @Override
    public <Bean> BeanInfo<Bean> getBeanInfo(String serviceName, Class<Bean> type, boolean require) {
        try {
            return getBeanInfo(serviceName, type, require, handledBeanInfoSet.keySet(), true);
        } catch (Exception e) {
            if (e instanceof OneMoreBeanRegisteredException) {
                throw e;
            }
            LOGGER.error(e.getMessage());
        }
        return null;
    }

    @Override
    public <Bean> BeanFactory<Bean> getBeanFactory(String serviceName, Class<Bean> type, boolean require) {
        try {
            if (serviceName == null && type == Object.class) {
                if (require) {
                    throw new NoBeanException("bean(" + Object.class + ") need name but null");
                } else {
                    return null;
                }
            }
            return getBeanFactory(serviceName, type, require, handledBeanInfoSet.keySet(), true);
        } catch (Exception e) {
            if (e instanceof OneMoreBeanRegisteredException) {
                throw e;
            }
            LOGGER.error("", e);
        }
        return null;
    }

    @Override
    public <T> BeanInfo<T> getBeanInfo(String serviceName, Class<T> type, boolean require, boolean throwException) {
        if (serviceName == null && type == Object.class) {
            if (require && throwException) {
                throw new NoBeanException("bean(" + Object.class + ") need name but null");
            } else {
                return null;
            }
        }
        return getBeanInfo(serviceName, type, require, handledBeanInfoSet.keySet(), throwException);
    }

    @Override
    public <T> BeanFactory<T> getBeanFactory(String serviceName, Class<T> type, boolean require, boolean throwException) {
        if (serviceName == null && type == Object.class) {
            if (require && throwException) {
                throw new NoBeanException("bean(" + Object.class + ") need name but null");
            } else {
                return null;
            }
        }
        return getBeanFactory(serviceName, type, require, handledBeanInfoSet.keySet(), throwException);
    }

    /*@Override
    @SuppressWarnings("unchecked")
    public <T> SimpleMutableBeanFactory<T, T> getBeanInfoWithBean(Class<T> type, GlobalBeanFactory globalBeanFactory) {
        synchronized (this) {
            LOGGER.trace(() -> "factory bean with type " + type.getName());
            BeanInfo<T> beanInfo = this.getBeanInfo(null, type, true, true);
            if (beanInfo != null) {
                T bean = globalBeanFactory.factory(beanInfo);
                return new SimpleMutableBeanFactory<T, T>(bean, (Class<T>) beanInfo.getBeanClass());
            }
            return null;
        }
    }*/

    /*@Override
    public Set<BeanInfo<?>> getRegisteredRawBeans() {
        Set<BeanInfo<?>> result = new HashSet<>();
        Collection<BeanInfo<?>> registerRawBeans = this.getRegisterRawBeans();
        if (!registerRawBeans.isEmpty()) {
            result.addAll(registerRawBeans);
        }
        return result;
    }*/

    @Override
    public boolean isBeanRegistered(Class<?> beanClass) {
        return BEAN_CLASSES.get(beanClass) != null;
    }

    @Override
    public <T> boolean containsBean(Class<T> beanType) {
        synchronized (this) {
            List<BeanInfo<? extends T>> list = this.getBeanInfoList(beanType, false);
            return list != null && !list.isEmpty();
        }
    }

    @Override
    public boolean containsBean(String beanName) {
        synchronized (this) {
            return getBeanInfo(beanName, null, false, false) != null;
        }
    }

    @Override
    public void printGraalvmConfig(ApplicationContext context) {
        List<BeanInfo<? extends BeanScanConfiguration>> list = this.getBeanInfoList(BeanScanConfiguration.class, true);
        Set<String> packages = new HashSet<>();
        for (BeanInfo<? extends BeanScanConfiguration> info : list) {
            Set<String> names = info.getBeanNames();
            for (String name : names) {
                if (info instanceof BeanFactory<?> beanFactory) {
                    BeanScanConfiguration bean = (BeanScanConfiguration) beanFactory.factoryNamedBean(name, context);
                    packages.addAll(bean.getScanBasePackages());
                }
            }
        }
        StringBuilder scanClasses = new StringBuilder("debbie.core.scan.classes=");
        StringBuilder reflectConfig = new StringBuilder("[\n");
        StringBuilder proxyConfig = new StringBuilder("[\n  [],\n");
        Set<BeanInfo> set = this.getAllBeanInfo();
        for (BeanInfo info : set) {
            Class beanClass = info.getBeanClass();
            String name = beanClass.getName();
            if (beanClass.isInterface()) {
                proxyConfig.append("  [\"").append(name).append("\"],\n");
            }
            for (String s : packages) {
                if (name.startsWith(s + ".")) {
                    scanClasses.append(name).append(",");
                }
                reflectConfig.append("  {\n    \"name\": \"")
                        .append(name)
                        .append("\",\n    \"methods\": [\n");
                Constructor[] constructors = beanClass.getConstructors();
                if (constructors.length > 0) {
                    for (Constructor constructor : constructors) {
                        reflectConfig.append("      {\"name\": \"<init>\", \"parameterTypes\": [");
                        Class<?>[] types = constructor.getParameterTypes();
                        for (Class<?> type : types) {
                            reflectConfig.append("\"").append(type.getName()).append("\", ");
                        }
                        reflectConfig.append("] },\n");
                    }
                }
                if (info instanceof DebbieReflectionBeanFactory beanFactory) {
                    Map<Method, Set<Annotation>> map = beanFactory.getMethodWithAnnotations();
                    Set<Method> methods = map.keySet();
                    for (Method method : methods) {
                        reflectConfig.append("      {\"name\":\"").append(method.getName()).append("\", \"parameterTypes\": [");
                        Class<?>[] types = method.getParameterTypes();
                        for (Class<?> type : types) {
                            reflectConfig.append("\"").append(type.getName()).append("\", ");
                        }
                        reflectConfig.append("] },\n");
                    }
                }
                if (reflectConfig.lastIndexOf(",\n") == reflectConfig.length() - 2) {
                    reflectConfig.deleteCharAt(reflectConfig.length() - 1);
                    reflectConfig.deleteCharAt(reflectConfig.length() - 1);
                }
                reflectConfig.append("\n    ],\n")
                        .append("    \"allDeclaredConstructors\": true,\n")
                        .append("    \"allPublicConstructors\": true,\n")
                        .append("    \"allDeclaredMethods\": true,\n")
                        .append("    \"allPublicMethods\": true,\n")
                        .append("    \"allDeclaredFields\": true,\n")
                        .append("    \"allPublicFields\": true\n")
                        .append("  },\n");
            }
        }
        if (reflectConfig.lastIndexOf(",\n") == reflectConfig.length() - 2) {
            reflectConfig.deleteCharAt(reflectConfig.length() - 1);
            reflectConfig.deleteCharAt(reflectConfig.length() - 1);
        }
        reflectConfig.append("\n]");

        if (proxyConfig.lastIndexOf(",\n") == proxyConfig.length() - 2) {
            proxyConfig.deleteCharAt(proxyConfig.length() - 1);
            proxyConfig.deleteCharAt(proxyConfig.length() - 1);
        }
        proxyConfig.append("\n]");
        LOGGER.info("scanned classes: \n" + scanClasses);
        LOGGER.info("graalvm reflect config: \n" + reflectConfig);
        LOGGER.info("graalvm proxy config: \n" + proxyConfig);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private <T> void getDebbieBeanInfoList(final Class<T> type, final Set<BeanInfo> beanInfoSet,
                                           final List<BeanInfo<? extends T>> list) {
        for (BeanInfo<?> debbieBeanInfo : beanInfoSet) {
            var flag = type.isAssignableFrom(debbieBeanInfo.getBeanClass())
                    || (debbieBeanInfo instanceof ClassDetailedBeanInfo
                    && ((ClassDetailedBeanInfo) debbieBeanInfo).getInterface(ignoredInterfaces) != null
                    && type.isAssignableFrom(((ClassDetailedBeanInfo) debbieBeanInfo).getInterface(ignoredInterfaces))
            );
            if (flag) {
                list.add((BeanInfo<? extends T>) debbieBeanInfo);
            }
        }
    }

    /*private <T> void getBeanFactoryListByType(final Class<T> type, final Set<BeanInfo> beanInfoSet,
                                              final List<BeanFactory> list) {
        for (BeanInfo debbieBeanInfo : beanInfoSet) {
            var flag = type.isAssignableFrom(debbieBeanInfo.getBeanClass())
                    || (debbieBeanInfo instanceof ClassDetailedBeanInfo
                    && ((ClassDetailedBeanInfo) debbieBeanInfo).getBeanInterface() != null
                    && type.isAssignableFrom(((ClassDetailedBeanInfo) debbieBeanInfo).getBeanInterface())
            );
            if (flag && debbieBeanInfo instanceof BeanFactory) {
                list.add((BeanFactory) debbieBeanInfo);
            }
        }
    }*/

    private <Info extends BeanInfo<Bean>, Bean extends I, I>
    void getBeanFactoryListByType(final Class<I> type, final Set<BeanInfo> beanInfoSet, final List<Info> list) {
        for (BeanInfo debbieBeanInfo : beanInfoSet) {
            var flag = type.isAssignableFrom(debbieBeanInfo.getBeanClass())
                    || (debbieBeanInfo instanceof ClassDetailedBeanInfo
                    && ((ClassDetailedBeanInfo) debbieBeanInfo).getInterface(ignoredInterfaces) != null
                    && type.isAssignableFrom(((ClassDetailedBeanInfo) debbieBeanInfo).getInterface(ignoredInterfaces))
            );
            if (flag && debbieBeanInfo instanceof BeanFactory) {
                list.add((Info) debbieBeanInfo);
            }
        }
    }

    @SuppressWarnings("rawtypes")
    private <T> List<BeanInfo<? extends T>> getBeanInfoList(Class<T> type, boolean require,
                                                            final Set<BeanInfo> beanInfoSet) {
        List<BeanInfo<? extends T>> list = new ArrayList<>();

        if (type != null) {
            getDebbieBeanInfoList(type, beanInfoSet, list);

            if (list.isEmpty()) {
                if (require) {
                    throw new NoBeanException(type.getName() + " not found");
                } else {
                    return null;
                }
            }

            List<BeanInfo<? extends T>> result = new ArrayList<>();
            for (BeanInfo<? extends T> beanInfo : list) {
                if (type.isAssignableFrom(beanInfo.getBeanClass())) {
                    if (beanInfo.getBeanType() == BeanType.SINGLETON) {
                        result.add(beanInfo);
                    }/* else if (beanInfo instanceof MutableFactoryBeanInfo) {
                        result.add(((MutableFactoryBeanInfo<K>) beanInfo).copy());
                    }*/
                }
            }
            return result;
        }

        return null;
    }

    private <Bean extends I, I> BeanInfo<Bean> getBeanInfo(String serviceName, final Class<I> type, boolean require,
                                                           final Set<BeanInfo> beanInfoSet, boolean throwException) {
        return getBeanInfo(BeanInfo.class, serviceName, type, require, beanInfoSet, throwException);
    }

    private <Bean extends I, I> BeanFactory<Bean> getBeanFactory(String serviceName, final Class<I> type, boolean require,
                                                                 final Set<BeanInfo> beanInfoSet, boolean throwException) {
        return getBeanInfo(BeanFactory.class, serviceName, type, require, beanInfoSet, throwException);
    }

    private <Info extends BeanInfo<Bean>, Bean extends I, I>
    Info getBeanInfo(Class<Info> infoClass, String serviceName, final Class<I> type, boolean require,
                                  final Set<BeanInfo> beanInfoSet, boolean throwException) {
        List<Info> list = new ArrayList<>();
        if (serviceName != null && !serviceName.isBlank()) {
            for (BeanInfo debbieBeanInfo : beanInfoSet) {
                if (debbieBeanInfo.containName(serviceName)) {
                    if (type != null) {
                        var flag = type.isAssignableFrom(debbieBeanInfo.getBeanClass())
                                || (debbieBeanInfo instanceof ClassDetailedBeanInfo
                                && ((ClassDetailedBeanInfo) debbieBeanInfo).getInterface(ignoredInterfaces) != null
                                && type.isAssignableFrom(((ClassDetailedBeanInfo) debbieBeanInfo).getInterface(ignoredInterfaces))
                        );
                        if (flag && infoClass.isInstance(debbieBeanInfo)) {
                            list.add((Info) debbieBeanInfo);
                        }
                    } else if (debbieBeanInfo instanceof BeanFactory) {
                        list.add((Info) debbieBeanInfo);
                    }
                }
            }
        }

        if (list.isEmpty()) {
            if (type != null) {
                getBeanFactoryListByType(type, beanInfoSet, list);
            }

            if (list.isEmpty()) {
                if (require) {
                    if ((serviceName == null || serviceName.isBlank()) && type != null) {
                        serviceName = type.getName();
                    }
                    if (throwException) {
                        throw new NoBeanException("bean(" + serviceName + ") not found");
                    } else {
                        return null;
                    }
                } else {
                    return null;
                }
            }
        }

        if (list.size() > 1) {
            String name = serviceName;
            if ((name == null || name.isBlank()) && type != null) {
                name = type.getName();
            }
            List<Info> copy = new ArrayList<>(list);
            for (Info next : copy) {
                if (next.getBeanClass() != type || !next.getBeanNames().contains(name)) {
                    list.remove(next);
                }
            }
            if (list.isEmpty()) {
                list = new ArrayList<>(copy);
                for (Info next : copy) {
                    if (serviceName != null && !next.getBeanNames().contains(name)) {
                        list.remove(next);
                    }
                }
                if (list.size() != 1) {
                    list = new ArrayList<>(copy);
                    for (Info next : copy) {
                        if (next.getBeanClass() != type) {
                            list.remove(next);
                        }
                    }
                }
                if (list.size() != 1) {
                    list = new ArrayList<>(copy);
                    for (Info next : copy) {
                        if (!(next.getBeanNames().contains("default")
                                || (type != null && next.getBeanNames().contains("default" + type.getSimpleName())))) {
                            list.remove(next);
                        }
                    }
                }
            }
            if (list.size() != 1) {
                throw new OneMoreBeanRegisteredException("bean(" + type + ", " + serviceName + ") must be only one");
            }
        }

        var ele = list.get(0);
        if (type == null || type.isAssignableFrom(ele.getBeanClass())) {
            if (ele.getBeanType() == BeanType.SINGLETON) {
                return ele;
            } else {
                return (Info) ele.copy();
            }
        }
        if (throwException) {
            throw new NoBeanException("bean " + type + " not found");
        } else {
            return null;
        }
    }

    @Override
    public void destroy(BeanInfo<?> beanInfo) {
        handledBeanInfoSet.remove(beanInfo);
    }

    private void releaseBeans(ApplicationContext applicationContext) {
        synchronized (DebbieBeanCenter.class) {
            destroyBeans(applicationContext, handledBeanInfoSet.keySet());

            handledBeanInfoSet.clear();
            beanLifecycles.clear();
        }
    }

    private synchronized void destroyBeans(ApplicationContext applicationContext, Collection<BeanInfo> beans) {
        if (beans != null && !beans.isEmpty()) {
            for (BeanInfo bean : beans) {
                LOGGER.trace(() -> "destruct bean " + bean.getBeanClass() + " with name " + bean.getServiceName());
                /*if (bean instanceof FactoryBeanInfo) {
                    ((FactoryBeanInfo<?>) bean).destruct(applicationContext);
                } else*/ if (bean instanceof BeanFactory) {
                    ((BeanFactory<?>) bean).destruct(applicationContext);
                }
            }
        }
    }

    @Override
    public void reset(ApplicationContext applicationContext) {
        synchronized (DebbieBeanCenter.class) {
            releaseBeans(applicationContext);
            BEAN_ANNOTATION.clear();
            BEAN_CLASSES.clear();
            CLASS_INFO_SET.clear();
            CLASS_ANNOTATION.clear();
            BEAN_CLASS_METHOD_MAP.clear();
            METHOD_ANNOTATION.clear();
            ANNOTATION_METHOD_BEANS.clear();
            injectTypes.clear();
            beanRegisters.clear();
        }
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(DebbieBeanCenter.class);

}
