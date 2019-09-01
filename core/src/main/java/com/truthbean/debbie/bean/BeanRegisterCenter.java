package com.truthbean.debbie.bean;

import com.truthbean.debbie.reflection.ReflectionHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.*;

/**
 * @author TruthBean
 * @since 0.0.1
 * Created on 2019/3/23 11:17.
 */
final class BeanRegisterCenter {
    private BeanRegisterCenter() {
    }

    private static final Map<Class<?>, DebbieBeanInfo> BEAN_CLASSES = new HashMap<>();
    private static final Set<DebbieBeanInfo<?>> CLASS_INFO_SET = new HashSet<>();

    private static final Set<Class<? extends Annotation>> CLASS_ANNOTATION = new HashSet<>();
    private static final Map<Class<? extends Annotation>, Map<Class<?>, List<Method>>> BEAN_CLASS_METHOD_MAP = new HashMap<>();

    private static final Set<Class<? extends Annotation>> METHOD_ANNOTATION = new HashSet<>();
    private static final Map<Class<? extends Annotation>, Set<DebbieBeanInfo>> ANNOTATION_METHOD_BEANS = new HashMap<>();

    static <Bean> void register(DebbieBeanInfo<Bean> beanClassInfo) {
        Class<Bean> beanClass = beanClassInfo.getBeanClass();
        LOGGER.debug("register class " + beanClass.getName());

        BEAN_CLASSES.put(beanClass, beanClassInfo);
        CLASS_INFO_SET.add(beanClassInfo);

        List<Method> declaredMethods = beanClassInfo.getMethods();
        declaredMethods.forEach(method -> {
            var annotations = method.getDeclaredAnnotations();
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
            CLASS_ANNOTATION.addAll(annotations);

            annotations.stream().filter(beanClass::isAnnotationPresent).forEach(annotation -> {
                var classMethodMap = BEAN_CLASS_METHOD_MAP.get(annotation);
                if (classMethodMap == null) {
                    classMethodMap = new HashMap<>();
                }
                classMethodMap.put(beanClass, declaredMethods);
                BEAN_CLASS_METHOD_MAP.put(annotation, classMethodMap);
            });
        }
    }

    static <Bean> void refresh(DebbieBeanInfo<Bean> beanClassInfo) {
        Class<Bean> beanClass = beanClassInfo.getBeanClass();
        LOGGER.debug("refresh class " + beanClass.getName());

        DebbieBeanInfo beanInfo = BEAN_CLASSES.get(beanClass);
        if (beanInfo == null) {
            throw new NoBeanException("bean " + beanClass + " has not registered. ");
        }
        beanInfo.setBean(beanClassInfo.getBean());
        beanInfo.setBeanFactory(beanClassInfo.getBeanFactory());
    }

    static void register(Class<?> beanClass) {
        var beanClassInfo = new DebbieBeanInfo<>(beanClass);
        register(beanClassInfo);
    }

    static void register(Class<? extends Annotation> classAnnotation, String packageName) {
        var allClass = ReflectionHelper.getAllClassByPackageName(packageName);
        if (!allClass.isEmpty()) {
            allClass.forEach(c -> {
                if (c.isAnnotationPresent(classAnnotation)) {
                    register(c);
                }
            });
        }
    }

    static void register(Class<? extends Annotation> classAnnotation, List<String> packageNames) {
        packageNames.forEach(packageName -> register(classAnnotation, packageName));
    }

    static void register(String packageName) {
        var allClass = ReflectionHelper.getAllClassByPackageName(packageName);
        if (!allClass.isEmpty()) {
            allClass.forEach(BeanRegisterCenter::register);
        }
    }

    static void register(List<String> packageNames) {
        packageNames.forEach(BeanRegisterCenter::register);
    }

    static List<Method> getBeanMethods(Class<?> beanClass) {
        var classInfoSet = BEAN_CLASSES;
        if (!classInfoSet.containsKey(beanClass)) {
            LOGGER.error(beanClass.getName() + " has not register");
            throw new RuntimeException(beanClass.getName() + " has not register");
        }
        return classInfoSet.get(beanClass).getMethods();
    }

    static <T extends Annotation> Set<DebbieBeanInfo> getAnnotatedClass(Class<T> annotationClass) {
        var classInfoSet = CLASS_INFO_SET;

        Set<DebbieBeanInfo> result = new HashSet<>();

        classInfoSet.stream()
                .filter(classInfo -> classInfo.getClassAnnotations().containsKey(annotationClass))
                .forEach(result::add);

        return result;
    }

    static <Bean> DebbieBeanInfo<Bean> getRegisterRawBean(Class<Bean> bean) {
        return BEAN_CLASSES.get(bean);
    }

    static Collection<DebbieBeanInfo> getRegisterRawBeans() {
        return Collections.unmodifiableCollection(BEAN_CLASSES.values());
    }

    /*protected static List<Method> getAnnotatedMethodsInAnnotatedBean
            (Class<? extends Annotation> classAnnotation, Class<? extends Annotation> methodAnnotation) {

    }*/

    static Set<DebbieBeanInfo> getAnnotatedMethodsBean(Class<? extends Annotation> methodAnnotation) {
        return ANNOTATION_METHOD_BEANS.get(methodAnnotation);
    }

    static Set<DebbieBeanInfo> getBeansByInterface(Class<?> interfaceType) {
        if (!BEAN_CLASSES.containsKey(interfaceType)) {
            return null;
        }

        Set<DebbieBeanInfo> classInfoSet = new HashSet<>();

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

    static Set<DebbieBeanInfo> getBeanByAbstractSuper(Class<?> abstractType) {
        if (!BEAN_CLASSES.containsKey(abstractType)) {
            return null;
        }

        Set<DebbieBeanInfo> classInfoSet = new HashSet<>();

        BEAN_CLASSES.forEach((clazz, beanInfo) -> {
            Class<?> superclass = clazz.getSuperclass();
            if (superclass != null && superclass != Object.class && superclass != Void.class) {
                classInfoSet.add(beanInfo);
            }
        });

        return classInfoSet;
    }

    static Set<Class<? extends Annotation>> getBeanAnnotations() {
        return Collections.unmodifiableSet(CLASS_ANNOTATION);
    }

    static void reset() {
        BEAN_CLASSES.clear();
        CLASS_INFO_SET.clear();
        CLASS_ANNOTATION.clear();
        BEAN_CLASS_METHOD_MAP.clear();
        METHOD_ANNOTATION.clear();
        ANNOTATION_METHOD_BEANS.clear();
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(BeanRegisterCenter.class);

}
