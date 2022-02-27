/**
 * Copyright (c) 2022 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 * http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.annotation;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.*;

/**
 * @author TruthBean/Rogar·Q
 * @since 0.1.0
 * Created on 2020-09-24 11:57
 */
public class AnnotationInfo {
    private final Annotation origin;
    private final Class<? extends Annotation> type;

    private final Map<String, AnnotationMethodInfo> properties = new HashMap<>();

    public AnnotationInfo(Annotation origin) {
        this.origin = origin;
        this.type = origin.annotationType();
        this.setPropertiesDefaultValue();
    }

    private void setPropertiesDefaultValue() {
        Method[] declaredMethods = type.getDeclaredMethods();
        for (Method declaredMethod : declaredMethods) {
            AnnotationMethodInfo methodInfo = new AnnotationMethodInfo();
            methodInfo.setMethod(declaredMethod);
            String methodName = declaredMethod.getName();
            methodInfo.setMethodName(methodName);
            properties.put(methodName, methodInfo);
        }
    }

    public Annotation getOrigin() {
        return origin;
    }

    public Class<? extends Annotation> annotationType() {
        return type;
    }

    public Map<String, AnnotationMethodInfo> properties() {
        return properties;
    }

    public boolean containAnnotation(Class<? extends Annotation> annotationClass) {
        for (Map.Entry<String, AnnotationMethodInfo> entry : properties.entrySet()) {
            var info = entry.getValue();
            if (info.getAliasForAnnotation() == annotationClass) {
                return true;
            }
        }
        return false;
    }

    public <T> T invokeAttribute(String attribute) {
        return invokeAttribute(attribute, null);
    }

    @SuppressWarnings("unchecked")
    public <T> T invokeAttribute(String attribute, T defaultValue) {
        for (Map.Entry<String, AnnotationMethodInfo> entry : properties.entrySet()) {
            var name = entry.getKey();
            if (name.equals(attribute)) {
                var info = entry.getValue();
                return (T) info.getValue();
            }
        }
        return defaultValue;
    }

    public void setPropertyValue(String name, Object value) {
        for (Map.Entry<String, AnnotationMethodInfo> entry : properties.entrySet()) {
            var propertyName = entry.getKey();
            if (propertyName.equals(name)) {
                var methodInfo = entry.getValue();
                methodInfo.setValue(value);
                break;
            }
        }
    }

    void handlePropertyAliasFor() {
        for (Map.Entry<String, AnnotationMethodInfo> entry : properties.entrySet()) {
            var info = entry.getValue();
            Class<? extends Annotation> alias = info.getAliasForAnnotation();
            if (info.hasAliasFor() && alias == Annotation.class || alias == origin.annotationType()) {
                var aliasInfo = properties.get(info.getAliasForAttribute());
                if (aliasInfo != null && aliasInfo.isDefaultValue()) {
                    aliasInfo.setValue(info.getValue());
                }
            }
        }
    }

    @Override
    public String toString() {
        return "{" + "\"origin\":" + origin + ","
                + "\"properties\":" + properties + "}";
    }
}
