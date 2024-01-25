/**
 * Copyright (c) 2024 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 * http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.annotation;

import com.truthbean.core.util.ClassHelper;
import com.truthbean.debbie.reflection.ReflectionHelper;
import com.truthbean.debbie.reflection.TypeHelper;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author TruthBean/Rogar·Q
 * @since 0.1.0
 * Created on 2020-09-24 12:07
 */
public class AnnotationParser {
    public static AnnotationInfo parse(Annotation annotation) {
        var info = new AnnotationInfo(annotation);
        Class<? extends Annotation> type = annotation.annotationType();
        Method[] declaredMethods = type.getDeclaredMethods();
        for (Method declaredMethod : declaredMethods) {
            AnnotationMethodInfo methodInfo = new AnnotationMethodInfo();
            methodInfo.setMethod(declaredMethod);
            methodInfo.setMethodName(declaredMethod.getName());
            var value = ReflectionHelper.invokeMethod(annotation, declaredMethod);
            methodInfo.setValue(value);
            AliasFor aliasFor = declaredMethod.getAnnotation(AliasFor.class);
            if (aliasFor != null) {
                methodInfo.setAliasFor(true);
                methodInfo.setAliasForAnnotation(aliasFor.annotation());
                String attribute = aliasFor.attribute().trim();
                if (attribute.isBlank()) {
                    attribute = aliasFor.value().trim();
                }
                if (attribute.isBlank()) {
                    attribute = declaredMethod.getName();
                }
                methodInfo.setAliasForAttribute(attribute);
            }
            info.properties().put(declaredMethod.getName(), methodInfo);
        }
        return info;
    }

    public static Map<Class<? extends Annotation>, AnnotationInfo> parseClassAnnotation(Class<?> clazz) {
        Set<Annotation> annotations = ReflectionHelper.getClassAnnotations(clazz);

        Set<Annotation> classAnnotations = new HashSet<>();
        Map<Class<? extends Annotation>, AnnotationInfo> map = new HashMap<>();

        if (!annotations.isEmpty()) {
            for (Annotation annotation : annotations) {
                Set<Annotation> annotationInAnnotation = ReflectionHelper.getClassAnnotations(annotation.annotationType());
                if (!annotationInAnnotation.isEmpty()) {
                    for (Annotation ann : annotationInAnnotation) {
                        Class<? extends Annotation> annotationType = ann.annotationType();
                        if (ClassHelper.filterAnnotation(annotationType)) {
                            map.put(ann.annotationType(), new AnnotationInfo(ann));
                            classAnnotations.add(ann);
                        }
                    }
                }

                if (TypeHelper.filterAnnotation(annotation.annotationType())) {
                    map.put(annotation.annotationType(), new AnnotationInfo(annotation));
                    classAnnotations.add(annotation);
                }
            }
        }
        for (Annotation annotation : classAnnotations) {
            if (TypeHelper.filterAnnotation(annotation.annotationType())) {
                parseClassAnnotation(map.get(annotation.annotationType()), map, null, null);
            }
        }
        return map;
    }

    private static void parseClassAnnotation(AnnotationInfo info, Map<Class<? extends Annotation>, AnnotationInfo> map,
                                            String attributeName, Object attributeValue) {
        Class<? extends Annotation> type = info.annotationType();
        info.properties().forEach((name, methodInfo) -> {
            Method declaredMethod = methodInfo.getMethod();
            String methodName = methodInfo.getMethodName();
            var value = ReflectionHelper.invokeMethod(info.getOrigin(), declaredMethod);
            if (methodName.equals(attributeName)) {
                methodInfo.setValue(attributeValue);
                value = attributeValue;
            } else {
                methodInfo.setValue(value);
            }
            AliasFor aliasFor = declaredMethod.getAnnotation(AliasFor.class);
            if (aliasFor != null) {
                methodInfo.setAliasFor(true);
                var anno = aliasFor.annotation();
                if (anno == Annotation.class) {
                    methodInfo.setAliasForAnnotation(type);
                } else {
                    methodInfo.setAliasForAnnotation(anno);
                }
                String attribute = aliasFor.attribute().trim();
                if (attribute.isBlank()) {
                    attribute = aliasFor.value().trim();
                }
                if (attribute.isBlank()) {
                    attribute = declaredMethod.getName();
                }
                if (map.containsKey(anno)) {
                    var annoInfo = map.get(anno);
                    if (annoInfo != null) {
                        annoInfo.setPropertyValue(attribute, value);
                        annoInfo.handlePropertyAliasFor();
                    }
                } else {
                    var annoAnno = type.getAnnotation(anno);
                    if (annoAnno != null)
                        parseClassAnnotation(new AnnotationInfo(annoAnno), map, attribute, value);
                }
                methodInfo.setAliasForAttribute(attribute);
            }
        });
        info.handlePropertyAliasFor();
    }
}
