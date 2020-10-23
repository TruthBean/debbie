/**
 * Copyright (c) 2020 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 * http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.bean;

import com.truthbean.debbie.annotation.AliasFor;
import com.truthbean.debbie.annotation.AnnotationInfo;
import com.truthbean.debbie.annotation.AnnotationMethodInfo;
import com.truthbean.debbie.annotation.AnnotationParser;
import com.truthbean.debbie.reflection.ReflectionHelper;
import com.truthbean.debbie.util.StringUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Map;

/**
 * @author TruthBean/Rogar·Q
 * @since 0.1.0
 * Created on 2020-08-24 15:43
 */
public interface BeanComponentParser {

    BeanComponentInfo parse(Annotation annotation, Class<?> beanType);

    static BeanComponentInfo parse(Class<? extends Annotation> key, Annotation value) {
        BeanComponent annotation = key.getAnnotation(BeanComponent.class);
        if (annotation != null) {
            Method[] methods = key.getMethods();
            Method valueMethod = null;
            Method nameMethod = null;
            Method typeMethod = null;
            Method lazyMethod = null;
            for (Method method : methods) {
                AliasFor aliasFor = method.getAnnotation(AliasFor.class);
                if (aliasFor != null) {
                    Class<? extends Annotation> type = aliasFor.annotation();
                    String attribute = aliasFor.attribute().trim();
                    if (attribute.isBlank()) {
                        attribute = aliasFor.value().trim();
                    }
                    if (attribute.isBlank()) {
                        attribute = method.getName();
                    }
                    if (type == BeanComponent.class) {
                        if (("value".equals(attribute) || "value".equals(method.getName()))
                                && method.getReturnType() == String.class) {
                            valueMethod = method;
                            continue;
                        }
                        if (("name".equals(attribute) || "name".equals(method.getName()))
                                && method.getReturnType() == String.class) {
                            nameMethod = method;
                            continue;
                        }
                        if ("type".equals(attribute) || "type".equals(method.getName())
                                && method.getReturnType() == BeanType.class) {
                            typeMethod = method;
                            continue;
                        }
                        if ("lazy".equals(attribute) || "lazy".equals(method.getName())
                                && method.getReturnType() == boolean.class) {
                            lazyMethod = method;
                        }
                    }
                }
            }

            if (valueMethod != null || nameMethod != null) {
                String beanName = null;
                if (valueMethod != null) {
                    beanName = ReflectionHelper.invokeMethod(value, valueMethod);
                }
                if (!StringUtils.hasText(beanName) && nameMethod != null) {
                    beanName = ReflectionHelper.invokeMethod(value, nameMethod);
                }
                var info = new BeanComponentInfo();
                info.setName(beanName);

                if (typeMethod != null) {
                    info.setType(ReflectionHelper.invokeMethod(value, typeMethod));
                }

                if (lazyMethod != null) {
                    Boolean lazy = ReflectionHelper.invokeMethod(value, lazyMethod);
                    if (lazy != null) {
                        info.setLazy(lazy);
                    }
                }

                info.setFactory(annotation.factory());

                return info;
            }
        }

        return null;
    }

    static BeanComponentInfo parse(Annotation value, BeanComponent annotation) {
        AnnotationInfo annotationInfo = AnnotationParser.parse(value);
        Map<String, AnnotationMethodInfo> methods = annotationInfo.methods();
        String beanName = null;
        String nameValue = null;
        BeanType beanType = null;
        boolean lazy = true;
        for (Map.Entry<String, AnnotationMethodInfo> entry : methods.entrySet()) {
            String name = entry.getKey();
            var info = entry.getValue();
            if (info.hasAliasFor()) {
                Class<? extends Annotation> type = info.getAliasForAnnotation();
                if (type == BeanComponent.class) {
                    if (info.aliasFor("name", String.class)) {
                        beanName = (String) annotationInfo.properties().get(name);
                        continue;
                    }
                    if (info.aliasFor("value", String.class)) {
                        nameValue = (String) annotationInfo.properties().get(name);
                        continue;
                    }
                    if (info.aliasFor("type", BeanType.class)) {
                        beanType = (BeanType) annotationInfo.properties().get(name);
                        continue;
                    }
                    if (info.aliasFor("lazy", boolean.class)) {
                        lazy = (boolean) annotationInfo.properties().get(name);
                    }
                }
            }

        }

        if (beanName != null || nameValue != null) {
            var info = new BeanComponentInfo();
            if (StringUtils.hasText(nameValue)) {
                info.setName(nameValue);
            } else {
                info.setName(beanName);
            }

            if (beanType != null) {
                info.setType(beanType);
            }

            info.setLazy(lazy);
            info.setFactory(annotation.factory());

            return info;
        }

        return null;
    }
}
