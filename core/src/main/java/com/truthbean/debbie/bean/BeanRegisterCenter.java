/**
 * Copyright (c) 2020 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *         http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.bean;

import com.truthbean.debbie.io.ResourceResolver;
import com.truthbean.debbie.proxy.asm.AsmGenerated;
import com.truthbean.debbie.proxy.javaassist.JavaassistProxyBean;
import com.truthbean.debbie.reflection.ReflectionHelper;
import com.truthbean.Logger;
import com.truthbean.logger.LoggerFactory;

import java.lang.annotation.Annotation;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.lang.reflect.Method;
import java.util.*;

/**
 * @author TruthBean
 * @since 0.0.1
 * Created on 2019/3/23 11:17.
 */
final class BeanRegisterCenter {
    BeanRegisterCenter() {
    }

    private static final Set<Class<? extends Annotation>> BEAN_ANNOTATION = new LinkedHashSet<>();

    private static final Map<Class<?>, DebbieBeanInfo<?>> BEAN_CLASSES = new HashMap<>();
    private static final Set<DebbieBeanInfo<?>> CLASS_INFO_SET = new HashSet<>();

    private static final Set<Class<? extends Annotation>> CLASS_ANNOTATION = new HashSet<>();
    private static final Map<Class<? extends Annotation>, Map<Class<?>, Set<Method>>> BEAN_CLASS_METHOD_MAP = new HashMap<>();

    private static final Set<Class<? extends Annotation>> METHOD_ANNOTATION = new HashSet<>();
    private static final Map<Class<? extends Annotation>, Set<DebbieBeanInfo<?>>> ANNOTATION_METHOD_BEANS = new HashMap<>();

    <A extends Annotation> void registerBeanAnnotation(Class<A> annotationType) {
        BEAN_ANNOTATION.add(annotationType);
    }

    Set<Class<? extends Annotation>> getBeanAnnotations() {
        return Collections.unmodifiableSet(BEAN_ANNOTATION);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    <Bean> void register(DebbieBeanInfo<Bean> beanClassInfo) {
        Class<Bean> beanClass = beanClassInfo.getBeanClass();
         if (BEAN_CLASSES.containsKey(beanClass)) {
             // merge
             DebbieBeanInfo beanInfo = BEAN_CLASSES.get(beanClass);
             beanInfo.addBeanNames(beanClassInfo.getBeanNames());

             // add bean alias name
             // if singleton, set order
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

        DebbieBeanInfo<?> put = BEAN_CLASSES.put(beanClass, beanClassInfo);
        if (put == null) {
            LOGGER.trace(() -> "register class " + beanClass.getName() + " with bean name " + beanClassInfo.getServiceName());
        }
        CLASS_INFO_SET.add(beanClassInfo);

        Map<Method, Set<Annotation>> methodWithAnnotations = beanClassInfo.getMethodWithAnnotations();
        methodWithAnnotations.forEach((method, annotations) -> {
            if (annotations != null) {
                for (Annotation annotation : annotations) {
                    var methodAnnotation = annotation.annotationType();
                    METHOD_ANNOTATION.add(methodAnnotation);

                    var annotationMethodBeans = ANNOTATION_METHOD_BEANS.computeIfAbsent(methodAnnotation, k -> new HashSet<>());
                    annotationMethodBeans.add(beanClassInfo);
                }
            }
        });

        Map<Class<? extends Annotation>, Annotation> classAnnotation = beanClassInfo.getClassAnnotations();
        if (!classAnnotation.isEmpty()) {
            var annotations = classAnnotation.keySet();

            classAnnotation.forEach((type, annotation) -> {
                if (filterAnnotation(type)) {
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

    boolean filterAnnotation(Class<? extends Annotation> annotationType) {
        return annotationType != FunctionalInterface.class
                && annotationType != Documented.class && annotationType != Retention.class && annotationType != Target.class
                && annotationType != Override.class;
    }

    @SuppressWarnings("unchecked")
    <Bean> void refresh(DebbieBeanInfo<Bean> beanClassInfo) {
        Class<Bean> beanClass = beanClassInfo.getBeanClass();
        LOGGER.trace(() -> "refresh class " + beanClass.getName());

        DebbieBeanInfo<Bean> beanInfo = (DebbieBeanInfo<Bean>) BEAN_CLASSES.get(beanClass);
        if (beanInfo == null) {
            throw new NoBeanException("bean " + beanClass + " has not registered. ");
        }
        beanInfo.setBean(beanClassInfo.getBean());
        beanInfo.setBeanFactory(beanClassInfo.getBeanFactory());
    }

    public boolean support(Class<?> beanClass) {
        if (beanClass == null)
            return false;
        if (beanClass.isEnum())
            return false;
        if (beanClass.isAnnotation())
            return false;
        if (beanClass.getAnnotation(AsmGenerated.class) != null ||
                JavaassistProxyBean.class.isAssignableFrom(beanClass)) {
            return false;
        }
        return true;
    }

    void register(Class<?> beanClass) {
        if (support(beanClass)) {
            var beanClassInfo = new DebbieBeanInfo<>(beanClass);
            if (beanClassInfo.getBeanType() == null)
                return;
            register(beanClassInfo);
        }
    }

    void register(Class<? extends Annotation> classAnnotation,
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
        return classInfoSet.get(beanClass).getMethods();
    }

    <T extends Annotation> Set<DebbieBeanInfo<?>> getAnnotatedClass(Class<T> annotationClass) {
        var classInfoSet = CLASS_INFO_SET;

        Set<DebbieBeanInfo<?>> result = new HashSet<>();

        classInfoSet.stream()
                .filter(classInfo -> classInfo.getClassAnnotations().containsKey(annotationClass))
                .forEach(result::add);

        return result;
    }

    <T extends Annotation> Set<DebbieBeanInfo<?>> getAnnotatedBeans() {
        var classInfoSet = CLASS_INFO_SET;
        var beanAnnotations = BEAN_ANNOTATION;

        Set<DebbieBeanInfo<?>> result = new HashSet<>();

        for (Class<? extends Annotation> annotationType : beanAnnotations) {
            for (DebbieBeanInfo<?> classInfo : classInfoSet) {
                if (classInfo.getClassAnnotations().containsKey(annotationType)) {
                    result.add(classInfo);
                }
            }
        }

        return result;
    }

    @SuppressWarnings("unchecked")
    <Bean> DebbieBeanInfo<Bean> getRegisterRawBean(Class<Bean> bean) {
        return (DebbieBeanInfo<Bean>) BEAN_CLASSES.get(bean);
    }

    Collection<DebbieBeanInfo<?>> getRegisterRawBeans() {
        return Collections.unmodifiableCollection(BEAN_CLASSES.values());
    }

    Collection<Class<?>> getRegisterRawBeanTypes() {
        return Collections.unmodifiableCollection(BEAN_CLASSES.keySet());
    }

    /*protected static List<Method> getAnnotatedMethodsInAnnotatedBean
            (Class<? extends Annotation> classAnnotation, Class<? extends Annotation> methodAnnotation) {

    }*/

    Set<DebbieBeanInfo<?>> getAnnotatedMethodsBean(Class<? extends Annotation> methodAnnotation) {
        return ANNOTATION_METHOD_BEANS.get(methodAnnotation);
    }

    Set<DebbieBeanInfo<?>> getBeansByInterface(Class<?> interfaceType) {
        if (!BEAN_CLASSES.containsKey(interfaceType)) {
            return null;
        }

        Set<DebbieBeanInfo<?>> classInfoSet = new HashSet<>();

        BEAN_CLASSES.forEach((clazz, beanInfo) -> {
            Class<?>[] interfaces = clazz.getInterfaces();
            if (interfaces != null && interfaces.length > 0) {
                for (Class<?> i : interfaces) {
                    if (i == interfaceType) {
                        classInfoSet.add(beanInfo);
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
            if (superclass != null && superclass != Object.class && superclass != Void.class) {
                classInfoSet.add(beanInfo);
            }
        });

        return classInfoSet;
    }

    void reset() {
        BEAN_CLASSES.clear();
        CLASS_INFO_SET.clear();
        CLASS_ANNOTATION.clear();
        BEAN_CLASS_METHOD_MAP.clear();
        METHOD_ANNOTATION.clear();
        ANNOTATION_METHOD_BEANS.clear();
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(BeanRegisterCenter.class);

}
