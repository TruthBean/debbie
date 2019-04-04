package com.truthbean.code.debbie.core.bean;

import com.truthbean.code.debbie.core.reflection.ClassInfo;
import com.truthbean.code.debbie.core.reflection.ReflectionHelper;
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
final class BeanCacheHandler {
    private BeanCacheHandler() {
    }

    private static final Map<Class<?>, ClassInfo> BEAN_CLASSES = new HashMap<>();
    private static final Set<ClassInfo> CLASS_INFO_SET = new HashSet<>();

    private static final Set<Class<? extends Annotation>> CLASS_ANNOTATION = new HashSet<>();
    private static final Map<Class<? extends Annotation>, Map<Class<?>, List<Method>>> BEAN_CLASS_METHOD_MAP = new HashMap<>();

    private static final Set<Class<? extends Annotation>> METHOD_ANNOTATION = new HashSet<>();
    private static final Map<Class<? extends Annotation>, Set<ClassInfo>> ANNOTATION_METHOD_BEANS = new HashMap<>();

    protected static void register(Class<?> beanClass) {
        LOGGER.debug("register class " + beanClass.getName());
        var beanClassInfo = new ClassInfo(beanClass);

        BEAN_CLASSES.put(beanClass, beanClassInfo);
        CLASS_INFO_SET.add(beanClassInfo);

        List<Method> declaredMethods = beanClassInfo.getMethods();
        for (var method: declaredMethods) {
            var annotations = method.getDeclaredAnnotations();
            if (annotations != null) {
                for (Annotation annotation: annotations) {
                    var methodAnnotation = annotation.annotationType();
                    METHOD_ANNOTATION.add(methodAnnotation);

                    var annotationMethodBeans = ANNOTATION_METHOD_BEANS.computeIfAbsent(methodAnnotation, k -> new HashSet<>());
                    annotationMethodBeans.add(beanClassInfo);
                }
            }
        }

        var classAnnotation = beanClassInfo.getClassAnnotations();
        if (!classAnnotation.isEmpty()) {
            var annotations = classAnnotation.keySet();
            CLASS_ANNOTATION.addAll(annotations);

            for (var annotation: annotations) {
                if (beanClass.isAnnotationPresent(annotation)) {
                    var classMethodMap = BEAN_CLASS_METHOD_MAP.get(annotation);
                    if (classMethodMap == null) {
                        classMethodMap = new HashMap<>();
                    }
                    classMethodMap.put(beanClass, declaredMethods);
                    BEAN_CLASS_METHOD_MAP.put(annotation, classMethodMap);
                }
            }
        }

    }

    protected static void register(Class<? extends Annotation> classAnnotation, String packageName) {
        var allClass = ReflectionHelper.getAllClassByPackageName(packageName);
        if (!allClass.isEmpty()) {
            allClass.forEach(c -> {
                if (c.isAnnotationPresent(classAnnotation)) {
                    register(c);
                }
            });
        }
    }

    protected static void register(Class<? extends Annotation> classAnnotation, List<String> packageNames) {
        for (String packageName: packageNames) {
            register(classAnnotation, packageName);
        }
    }

    protected static void register(String packageName) {
        var allClass = ReflectionHelper.getAllClassByPackageName(packageName);
        if (!allClass.isEmpty()) {
            allClass.forEach(BeanCacheHandler::register);
        }
    }

    protected static void register(List<String> packageNames) {
        for (String packageName: packageNames) {
            register(packageName);
        }
    }

    protected static List<Method> getBeanMethods(Class<?> beanClass) {
        var classInfoSet = BEAN_CLASSES;
        if (!classInfoSet.containsKey(beanClass)) {
            LOGGER.error(beanClass.getName() + " has not register");
            throw new RuntimeException(beanClass.getName() + " has not register");
        }
        return BEAN_CLASSES.get(beanClass).getMethods();
    }

    protected static <T extends Annotation> Set<ClassInfo> getAnnotatedClass(Class<T> annotationClass) {
        var classInfoSet = CLASS_INFO_SET;

        Set<ClassInfo> result = new HashSet<>();

        for (ClassInfo classInfo : classInfoSet) {
            if (classInfo.getClassAnnotations().containsKey(annotationClass)) {
                result.add(classInfo);
            }
        }

        return result;
    }

    public static ClassInfo getRegisterBean(Class<?> bean) {
        return BEAN_CLASSES.get(bean);
    }

    /*protected static List<Method> getAnnotatedMethodsInAnnotatedBean
            (Class<? extends Annotation> classAnnotation, Class<? extends Annotation> methodAnnotation) {

    }*/

    protected static Set<ClassInfo> getAnnotatedMethodsBean(Class<? extends Annotation> methodAnnotation) {
        return ANNOTATION_METHOD_BEANS.get(methodAnnotation);
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(BeanCacheHandler.class);

}
