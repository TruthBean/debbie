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

import com.truthbean.debbie.reflection.ReflectionHelper;
import com.truthbean.debbie.util.StringUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

/**
 * @author TruthBean/Rogar·Q
 * @since 0.1.0
 * Created on 2020-08-24 15:43
 */
public class BeanComponentParser {

    public static BeanComponentInfo parse(Annotation annotation) {
        if (annotation == null)
            return null;
        if (annotation.annotationType() == BeanComponent.class) {
            return parse((BeanComponent) annotation);
        }
        // todo
        return null;
    }

    public static BeanComponentInfo parse(BeanComponent value) {
        var info = new BeanComponentInfo();
        String beanName = value.name();
        if (beanName.isBlank()) {
            beanName = value.value();
        }
        if (!beanName.isBlank()) {
            info.setName(beanName);
        }
        info.setType(value.type());
        info.setLazy(value.lazy());

        return info;
    }

    public static BeanComponentInfo parse(Class<? extends Annotation> key, Annotation value) {
        BeanComponent annotation = key.getAnnotation(BeanComponent.class);
        if (annotation != null) {
            Method[] methods = key.getMethods();
            Method valueMethod = null;
            Method nameMethod = null;
            Method typeMethod = null;
            Method lazyMethod = null;
            for (Method method : methods) {
                if ("value".equals(method.getName()) && method.getReturnType() == String.class
                        && method.getAnnotation(BeanAliceForValue.class) != null) {
                    valueMethod = method;
                    continue;
                }
                if ("name".equals(method.getName()) && method.getReturnType() == String.class
                        && method.getAnnotation(BeanAliceForName.class) != null) {
                    nameMethod = method;
                    continue;
                }
                if ("type".equals(method.getName()) && method.getReturnType() == BeanType.class
                        && method.getAnnotation(BeanAliceForType.class) != null) {
                    typeMethod = method;
                    continue;
                }
                if ("lazy".equals(method.getName()) && method.getReturnType() == boolean.class
                        && method.getAnnotation(BeanAliceForLazy.class) != null) {
                    lazyMethod = method;
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

                return info;
            }
        }

        return null;
    }
}
