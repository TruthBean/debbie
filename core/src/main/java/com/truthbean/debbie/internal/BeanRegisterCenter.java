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
import com.truthbean.debbie.annotation.AnnotationInfo;
import com.truthbean.debbie.bean.*;
import com.truthbean.debbie.env.EnvironmentContent;
import com.truthbean.debbie.io.ResourceResolver;
import com.truthbean.debbie.proxy.asm.AsmGenerated;
import com.truthbean.debbie.proxy.javaassist.JavaassistProxyBean;
import com.truthbean.debbie.reflection.ReflectionHelper;
import com.truthbean.debbie.reflection.TypeHelper;
import com.truthbean.LoggerFactory;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author TruthBean
 * @since 0.0.1
 * Created on 2019/3/23 11:17.
 */
final class BeanRegisterCenter {
    private final EnvironmentContent content;
    BeanRegisterCenter(EnvironmentContent content) {
        this.content = content;
    }

    public static final Object value = new Object();

    private static final Map<Class<? extends Annotation>, BeanComponentParser> BEAN_ANNOTATION = new LinkedHashMap<>();

    private static final Map<Class<?>, BeanInfo<?>> BEAN_CLASSES = new HashMap<>();
    private static final Map<BeanInfo<?>, Object> CLASS_INFO_SET = new ConcurrentHashMap<>();

    private static final Set<Class<? extends Annotation>> CLASS_ANNOTATION = new HashSet<>();
    private static final Map<Class<? extends Annotation>, Map<Class<?>, Set<Method>>> BEAN_CLASS_METHOD_MAP = new HashMap<>();

    private static final Set<Class<? extends Annotation>> METHOD_ANNOTATION = new HashSet<>();
    private static final Map<Class<? extends Annotation>, Set<BeanInfo<?>>> ANNOTATION_METHOD_BEANS = new HashMap<>();

    <A extends Annotation> void registerBeanAnnotation(Class<A> annotationType, BeanComponentParser parser) {
        BEAN_ANNOTATION.put(annotationType, parser);
    }

    Set<Class<? extends Annotation>> getBeanAnnotations() {
        return Collections.unmodifiableSet(BEAN_ANNOTATION.keySet());
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    synchronized <Bean> void register(BeanInfo<Bean> beanClassInfo) {
        Set<BeanCondition> conditions = beanClassInfo.getConditions();
        for (BeanCondition condition : conditions) {
            if (!condition.matches(content)) {
                return;
            }
        }
        Class<Bean> beanClass = beanClassInfo.getBeanClass();
        if (BEAN_CLASSES.containsKey(beanClass)) {
            // merge
            BeanInfo info = BEAN_CLASSES.get(beanClass);
            if (info instanceof MutableBeanInfo) {
                MutableBeanInfo beanInfo = (MutableBeanInfo) info;
                beanInfo.addBeanNames(beanClassInfo.getBeanNames());

                // add bean alias name
                // TODO if singleton, set order
                if ((beanInfo.isSingleton() || beanInfo.getBeanType() == null) && beanClassInfo.isSingleton()) {
                    beanInfo.setBeanType(BeanType.SINGLETON);
                    if (beanClassInfo.isPresent() && beanInfo.isEmpty()) {
                        beanInfo.setBean(beanClassInfo.getBean());
                    }
                    if (beanClassInfo.hasBeanFactory() && !beanInfo.hasBeanFactory()) {
                        beanInfo.setBeanFactory(beanClassInfo.getBeanFactory());
                    }
                    return;
                } else if ((beanInfo.getBeanType() == BeanType.NO_LIMIT || beanInfo.getBeanType() == null)
                        && beanClassInfo.getBeanType() == BeanType.NO_LIMIT) {
                    if (beanClassInfo.hasBeanFactory() && !beanInfo.hasBeanFactory()) {
                        beanInfo.setBeanFactory(beanClassInfo.getBeanFactory());
                    }
                    return;
                }
            }
        }

        BeanInfo<?> put = BEAN_CLASSES.put(beanClass, beanClassInfo);
        if (put == null) {
            LOGGER.trace(() -> "register class " + beanClass.getName() + " with bean name " + beanClassInfo.getServiceName());
        }
        CLASS_INFO_SET.put(beanClassInfo, value);

        if (beanClassInfo instanceof ClassDetailedBeanInfo) {
            ClassDetailedBeanInfo detailedBeanInfo = (ClassDetailedBeanInfo) beanClassInfo;
            Map<Method, Set<Annotation>> methodWithAnnotations = detailedBeanInfo.getMethodWithAnnotations();
            methodWithAnnotations.forEach((method, annotations) -> {
                if (annotations != null) {
                    for (Annotation annotation : annotations) {
                        var methodAnnotation = annotation.annotationType();
                        if (methodAnnotation != Override.class && methodAnnotation != Deprecated.class && methodAnnotation != SuppressWarnings.class)
                            METHOD_ANNOTATION.add(methodAnnotation);

                        var annotationMethodBeans = ANNOTATION_METHOD_BEANS.computeIfAbsent(methodAnnotation, k -> new HashSet<>());
                        annotationMethodBeans.add(beanClassInfo);
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
    }

    public static Set<Class<? extends Annotation>> getClassAnnotation() {
        return CLASS_ANNOTATION;
    }

    public static Set<Class<? extends Annotation>> getMethodAnnotation() {
        return METHOD_ANNOTATION;
    }

    @SuppressWarnings("unchecked")
    synchronized <Bean> void refresh(MutableBeanInfo<Bean> beanClassInfo) {
        Class<Bean> beanClass = beanClassInfo.getBeanClass();
        LOGGER.trace(() -> "refresh class " + beanClass.getName());

        BeanInfo<?> beanInfo = BEAN_CLASSES.get(beanClass);
        if (beanInfo == null) {
            throw new NoBeanException("bean " + beanClass + " has not registered. ");
        }
        if (beanInfo instanceof MutableBeanInfo) {
            MutableBeanInfo<Bean> mutableBeanInfo = (MutableBeanInfo<Bean>) BEAN_CLASSES.get(beanClass);
            mutableBeanInfo.setBean(beanClassInfo.getBean());
            mutableBeanInfo.setBeanFactory(beanClassInfo.getBeanFactory());
        }

    }

    public boolean support(Class<?> beanClass) {
        if (beanClass == null)
            return false;
        if (beanClass.isEnum())
            return false;
        if (beanClass.isAnnotation())
            return false;
        return beanClass.getAnnotation(AsmGenerated.class) == null && beanClass.getAnnotation(NonBean.class) == null &&
                !JavaassistProxyBean.class.isAssignableFrom(beanClass);
    }

    @SuppressWarnings({"unchecked"})
    synchronized void register(final Class<?> beanClass) {
        if (beanClass.isAnnotation()) {
            registerBeanComponentAnnotation((Class<? extends Annotation>) beanClass);
            return;
        }
        if (support(beanClass)) {
            try {
                DebbieClassBeanInfo<?> beanClassInfo = new DebbieClassBeanInfo<>(beanClass);
                if (beanClassInfo.getBeanType() == null) {
                    beanClassInfo = new DebbieClassBeanInfo<>(beanClass, BEAN_ANNOTATION);
                    if (beanClassInfo.getBeanType() == null)
                        return;
                }
                register(beanClassInfo);
            } catch (Throwable e) {
                LOGGER.error("register class<" + beanClass + "> error. ", e);
            }
        }
    }

    private void registerBeanComponentAnnotation(Class<? extends Annotation> annotationType) {
        if (annotationType.getAnnotation(BeanComponent.class) != null) {
            registerBeanAnnotation(annotationType, new DefaultBeanComponentParser());
        }
    }

    synchronized void register(Class<? extends Annotation> classAnnotation,
                               String packageName, ClassLoader classLoader,
                               ResourceResolver resourceResolver) {
        var allClass = ReflectionHelper.getAllClassByPackageName(packageName, classLoader, resourceResolver);
        if (!allClass.isEmpty()) {
            allClass.forEach(c -> {
                if (c.isAnnotationPresent(classAnnotation)) {
                    register(c);
                }
            });
        }
    }

    void register(Class<? extends Annotation> classAnnotation,
                  List<String> packageNames, ClassLoader classLoader,
                  ResourceResolver resourceResolver) {
        packageNames.forEach(packageName -> register(classAnnotation, packageName, classLoader, resourceResolver));
    }

    void register(String packageName, ClassLoader classLoader, ResourceResolver resourceResolver) {
        var allClass = ReflectionHelper.getAllClassByPackageName(packageName, classLoader, resourceResolver);
        if (!allClass.isEmpty()) {
            allClass.forEach(this::register);
        }
    }

    void register(List<String> packageNames, ClassLoader classLoader, ResourceResolver resourceResolver) {
        packageNames.forEach(ele -> this.register(ele, classLoader, resourceResolver));
    }

    Set<Method> getBeanMethods(Class<?> beanClass) {
        var classInfoSet = BEAN_CLASSES;
        if (!classInfoSet.containsKey(beanClass)) {
            LOGGER.error(() -> beanClass.getName() + " has not register");
            throw new NoBeanException(beanClass.getName() + " has not register");
        }
        BeanInfo<?> beanInfo = classInfoSet.get(beanClass);
        if (beanInfo instanceof ClassDetailedBeanInfo) {
            return ((ClassDetailedBeanInfo<?>) beanInfo).getMethods();
        }
        return new HashSet<>();
    }

    <T extends Annotation> Set<DebbieClassBeanInfo<?>> getAnnotatedClass(Class<T> annotationClass) {
        Set<BeanInfo<?>> classInfoSet = CLASS_INFO_SET.keySet();

        Set<DebbieClassBeanInfo<?>> result = new HashSet<>();

        classInfoSet.stream()
                .filter(classInfo -> classInfo instanceof DebbieClassBeanInfo
                        && ((DebbieClassBeanInfo<?>) classInfo).getClassAnnotations().containsKey(annotationClass))
                .forEach(info -> result.add((DebbieClassBeanInfo<?>) info));

        return result;
    }

    Set<DebbieClassBeanInfo<?>> getAnnotatedBeans() {
        Set<BeanInfo<?>> classInfoSet = CLASS_INFO_SET.keySet();
        Set<DebbieClassBeanInfo<?>> result = new HashSet<>();

        for (Class<? extends Annotation> annotationType : getBeanAnnotations()) {
            for (BeanInfo<?> classInfo : classInfoSet) {
                if (classInfo instanceof DebbieClassBeanInfo) {
                    DebbieClassBeanInfo<?> beanInfo = (DebbieClassBeanInfo<?>) classInfo;
                    if (beanInfo.getClassAnnotations().containsKey(annotationType)) {
                        result.add(beanInfo);
                    }
                }
            }
        }

        return result;
    }

    @SuppressWarnings("unchecked")
    <Bean> DebbieBeanInfo<Bean> getRegisterRawBean(Class<Bean> bean) {
        BeanInfo<?> info = BEAN_CLASSES.get(bean);
        if (info instanceof DebbieBeanInfo)
            return (DebbieBeanInfo<Bean>) BEAN_CLASSES.get(bean);
        return null;
    }

    Collection<BeanInfo<?>> getRegisteredBeans() {
        Collection<BeanInfo<?>> values = BEAN_CLASSES.values();
        return Collections.unmodifiableCollection(values);
    }

    Collection<MutableBeanInfo<?>> getRegisterRawBeans() {
        Collection<BeanInfo<?>> values = BEAN_CLASSES.values();
        Collection<MutableBeanInfo<?>> result = new HashSet<>();
        for (BeanInfo<?> beanInfo : values) {
            if (beanInfo instanceof MutableBeanInfo) {
                result.add((MutableBeanInfo<?>) beanInfo);
            }
        }
        return Collections.unmodifiableCollection(result);
    }

    Collection<Class<?>> getRegisterRawBeanTypes() {
        return Collections.unmodifiableCollection(BEAN_CLASSES.keySet());
    }

    /*protected static List<Method> getAnnotatedMethodsInAnnotatedBean
            (Class<? extends Annotation> classAnnotation, Class<? extends Annotation> methodAnnotation) {

    }*/

    Set<DebbieClassBeanInfo<?>> getAnnotatedMethodsBean(Class<? extends Annotation> methodAnnotation) {
        Set<DebbieClassBeanInfo<?>> result = new HashSet<>();
        Set<BeanInfo<?>> set = ANNOTATION_METHOD_BEANS.get(methodAnnotation);
        if (set != null) {
            for (BeanInfo<?> beanInfo : set) {
                if (beanInfo instanceof DebbieClassBeanInfo) {
                    result.add((DebbieClassBeanInfo<?>) beanInfo);
                }
            }
        }
        return result;
    }

    Set<DebbieClassBeanInfo<?>> getBeansByInterface(Class<?> interfaceType) {
        if (!BEAN_CLASSES.containsKey(interfaceType)) {
            return null;
        }

        Set<DebbieClassBeanInfo<?>> classInfoSet = new HashSet<>();

        BEAN_CLASSES.forEach((clazz, beanInfo) -> {
            Class<?>[] interfaces = clazz.getInterfaces();
            if (interfaces.length > 0) {
                for (Class<?> i : interfaces) {
                    if (i == interfaceType && beanInfo instanceof DebbieClassBeanInfo) {
                        classInfoSet.add((DebbieClassBeanInfo<?>)beanInfo);
                    }
                }
            }
        });

        return classInfoSet;
    }

    Set<DebbieBeanInfo<?>> getBeanByAbstractSuper(Class<?> abstractType) {
        if (!BEAN_CLASSES.containsKey(abstractType)) {
            return null;
        }

        Set<DebbieBeanInfo<?>> classInfoSet = new HashSet<>();

        BEAN_CLASSES.forEach((clazz, beanInfo) -> {
            Class<?> superclass = clazz.getSuperclass();
            if (superclass != null && superclass != Object.class && superclass != Void.class
                    && beanInfo instanceof DebbieBeanInfo) {
                classInfoSet.add((DebbieBeanInfo<?>) beanInfo);
            }
        });

        return classInfoSet;
    }

    void reset() {
        BEAN_ANNOTATION.clear();
        BEAN_CLASSES.clear();
        CLASS_INFO_SET.clear();
        CLASS_ANNOTATION.clear();
        BEAN_CLASS_METHOD_MAP.clear();
        METHOD_ANNOTATION.clear();
        ANNOTATION_METHOD_BEANS.clear();
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(BeanRegisterCenter.class);

}
