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

    private static final Set<Class<? extends Annotation>> BEAN_ANNOTATION = new LinkedHashSet<>();

    private static final Map<Class<?>, DebbieBeanInfo<?>> BEAN_CLASSES = new HashMap<>();
    private static final Set<DebbieBeanInfo<?>> CLASS_INFO_SET = new HashSet<>();

    private static final Set<Class<? extends Annotation>> CLASS_ANNOTATION = new HashSet<>();
    private static final Map<Class<? extends Annotation>, Map<Class<?>, List<Method>>> BEAN_CLASS_METHOD_MAP = new HashMap<>();

    private static final Set<Class<? extends Annotation>> METHOD_ANNOTATION = new HashSet<>();
    private static final Map<Class<? extends Annotation>, Set<DebbieBeanInfo<?>>> ANNOTATION_METHOD_BEANS = new HashMap<>();

    static <A extends Annotation> void registerBeanAnnotation(Class<A> annotationType) {
        BEAN_ANNOTATION.add(annotationType);
    }

    static Set<Class<? extends Annotation>> getBeanAnnotations() {
        return Collections.unmodifiableSet(BEAN_ANNOTATION);
    }

    static <Bean> void register(DebbieBeanInfo<Bean> beanClassInfo) {
        Class<Bean> beanClass = beanClassInfo.getBeanClass();

        DebbieBeanInfo<?> put = BEAN_CLASSES.put(beanClass, beanClassInfo);
        if (put == null) {
            LOGGER.trace("register class " + beanClass.getName() + " with bean name " + beanClassInfo.getServiceName());
        }
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

    @SuppressWarnings("unchecked")
    static <Bean> void refresh(DebbieBeanInfo<Bean> beanClassInfo) {
        Class<Bean> beanClass = beanClassInfo.getBeanClass();
        LOGGER.debug("refresh class " + beanClass.getName());

        DebbieBeanInfo<Bean> beanInfo = (DebbieBeanInfo<Bean>) BEAN_CLASSES.get(beanClass);
        if (beanInfo == null) {
            throw new NoBeanException("bean " + beanClass + " has not registered. ");
        }
        beanInfo.setBean(beanClassInfo.getBean());
        beanInfo.setBeanFactory(beanClassInfo.getBeanFactory());
    }

    public static boolean support(Class<?> beanClass) {
        if (beanClass == null)
            return false;
        if (beanClass.isEnum())
            return false;
        if (beanClass.isAnnotation())
            return false;
        return true;
    }

    static void register(Class<?> beanClass) {
        if (support(beanClass)) {
            var beanClassInfo = new DebbieBeanInfo<>(beanClass);
            register(beanClassInfo);
        }
    }

    static void register(Class<? extends Annotation> classAnnotation, String packageName, ClassLoader classLoader) {
        var allClass = ReflectionHelper.getAllClassByPackageName(packageName, classLoader);
        if (!allClass.isEmpty()) {
            allClass.forEach(c -> {
                if (c.isAnnotationPresent(classAnnotation)) {
                    register(c);
                }
            });
        }
    }

    static void register(Class<? extends Annotation> classAnnotation, List<String> packageNames, ClassLoader classLoader) {
        packageNames.forEach(packageName -> register(classAnnotation, packageName, classLoader));
    }

    static void register(String packageName, ClassLoader classLoader) {
        var allClass = ReflectionHelper.getAllClassByPackageName(packageName, classLoader);
        if (!allClass.isEmpty()) {
            allClass.forEach(BeanRegisterCenter::register);
        }
    }

    static void register(List<String> packageNames, ClassLoader classLoader) {
        packageNames.forEach(ele -> BeanRegisterCenter.register(ele, classLoader));
    }

    static List<Method> getBeanMethods(Class<?> beanClass) {
        var classInfoSet = BEAN_CLASSES;
        if (!classInfoSet.containsKey(beanClass)) {
            LOGGER.error(beanClass.getName() + " has not register");
            throw new RuntimeException(beanClass.getName() + " has not register");
        }
        return classInfoSet.get(beanClass).getMethods();
    }

    static <T extends Annotation> Set<DebbieBeanInfo<?>> getAnnotatedClass(Class<T> annotationClass) {
        var classInfoSet = CLASS_INFO_SET;

        Set<DebbieBeanInfo<?>> result = new HashSet<>();

        classInfoSet.stream()
                .filter(classInfo -> classInfo.getClassAnnotations().containsKey(annotationClass))
                .forEach(result::add);

        return result;
    }

    static <T extends Annotation> Set<DebbieBeanInfo<?>> getAnnotatedBeans() {
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
    static <Bean> DebbieBeanInfo<Bean> getRegisterRawBean(Class<Bean> bean) {
        return (DebbieBeanInfo<Bean>) BEAN_CLASSES.get(bean);
    }

    static Collection<DebbieBeanInfo<?>> getRegisterRawBeans() {
        return Collections.unmodifiableCollection(BEAN_CLASSES.values());
    }

    static Collection<Class<?>> getRegisterRawBeanTypes() {
        return Collections.unmodifiableCollection(BEAN_CLASSES.keySet());
    }

    /*protected static List<Method> getAnnotatedMethodsInAnnotatedBean
            (Class<? extends Annotation> classAnnotation, Class<? extends Annotation> methodAnnotation) {

    }*/

    static Set<DebbieBeanInfo<?>> getAnnotatedMethodsBean(Class<? extends Annotation> methodAnnotation) {
        return ANNOTATION_METHOD_BEANS.get(methodAnnotation);
    }

    static Set<DebbieBeanInfo<?>> getBeansByInterface(Class<?> interfaceType) {
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

    static Set<DebbieBeanInfo<?>> getBeanByAbstractSuper(Class<?> abstractType) {
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
