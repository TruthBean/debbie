/**
 * Copyright (c) 2020 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 * http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.annotation;

import com.truthbean.debbie.reflection.ReflectionHelper;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

/**
 * @author TruthBean/Rogar·Q
 * @since 0.1.0
 * Created on 2020-09-24 12:07
 */
public class AnnotationParser {
    public static AnnotationInfo parse(Annotation annotation) {
        AnnotationInfo info = new AnnotationInfo(annotation);
        Class<? extends Annotation> type = annotation.annotationType();
        Method[] declaredMethods = type.getDeclaredMethods();
        for (Method declaredMethod : declaredMethods) {
            AnnotationMethodInfo methodInfo = new AnnotationMethodInfo();
            methodInfo.setMethod(declaredMethod);
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
                methodInfo.setMethodName(declaredMethod.getName());
            }
            info.methods().put(declaredMethod.getName(), methodInfo);
            info.properties().put(declaredMethod.getName(), ReflectionHelper.invokeMethod(annotation, declaredMethod));
        }
        return info;
    }
}
