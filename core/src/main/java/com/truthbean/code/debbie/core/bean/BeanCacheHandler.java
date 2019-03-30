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

    private static final Set<ClassInfo> CLASS_INFO_SET = new HashSet<>();

    private static final Map<Class<?>, ClassInfo> BEAN_CLASSES = new HashMap<>();

    private static final Map<Class<? extends Annotation>, Map<Class<?>, List<Method>>> BEAN_CLASS_METHOD_MAP = new HashMap<>();

    protected static void register(Class<? extends Annotation> classAnnotation, Class<?> beanClass) {
        var beanClassInfo = new ClassInfo(beanClass);
        BEAN_CLASSES.put(beanClass, beanClassInfo);
        CLASS_INFO_SET.add(beanClassInfo);

        List<Method> declaredMethods = beanClassInfo.getMethods();

        if (classAnnotation != null && beanClass.isAnnotationPresent(classAnnotation)) {
            var classMethodMap = BEAN_CLASS_METHOD_MAP.get(classAnnotation);
            if (classMethodMap == null) {
                classMethodMap = new HashMap<>();
            }
            classMethodMap.put(beanClass, declaredMethods);
            BEAN_CLASS_METHOD_MAP.put(classAnnotation, classMethodMap);
        }
    }

    protected static void register(Class<? extends Annotation> classAnnotation, String packageName) {
        var allClass = ReflectionHelper.getAllClassByPackageName(packageName);
        if (allClass.isEmpty()) {
            allClass.forEach( bean -> register(classAnnotation, bean));
        }
    }

    protected static void register(Class<? extends Annotation> classAnnotation, List<String> packageNames) {
        for (String packageName: packageNames) {
            register(classAnnotation, packageName);
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

    /*protected static List<Method> getAnnotatedMethodsInAnnotatedBean
            (Class<? extends Annotation> classAnnotation, Class<? extends Annotation> methodAnnotation) {

    }*/

    private static final Logger LOGGER = LoggerFactory.getLogger(BeanCacheHandler.class);

}
