/**
 * Copyright (c) 2026 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 * <a href="http://license.coscl.org.cn/MulanPSL2">http://license.coscl.org.cn/MulanPSL2</a>
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.annotation;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * Metadata wrapper around an {@link Annotation} instance, exposing its
 * attributes as a map of {@link AnnotationMethodInfo} keyed by attribute
 * name. Supports {@link AliasFor} resolution via
 * {@link #handlePropertyAliasFor()}.
 *
 * @author TruthBean/Rogar·Q
 * @since 0.1.0
 * Created on 2020-09-24 11:57
 */
public class AnnotationInfo {
    /**
     * the original annotation instance
     */
    private final Annotation origin;
    /**
     * the annotation type
     */
    private final Class<? extends Annotation> type;

    /**
     * attribute name → method/value metadata
     */
    private final Map<String, AnnotationMethodInfo> properties = new HashMap<>();

    /**
     * Wraps the given annotation and initialises attribute metadata with
     * default values.
     *
     * @param origin the annotation to wrap
     */
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

    /**
     * Returns the original annotation instance.
     */
    public Annotation getOrigin() {
        return origin;
    }

    /**
     * Returns the annotation type.
     */
    public Class<? extends Annotation> annotationType() {
        return type;
    }

    /**
     * Returns the map of attribute name to method/value metadata.
     */
    public Map<String, AnnotationMethodInfo> properties() {
        return properties;
    }

    /**
     * Checks whether any attribute aliases the given annotation type.
     *
     * @param annotationClass the annotation type to look for
     * @return {@code true} if an attribute aliases the given type
     */
    public boolean containAnnotation(Class<? extends Annotation> annotationClass) {
        for (Map.Entry<String, AnnotationMethodInfo> entry : properties.entrySet()) {
            var info = entry.getValue();
            if (info.getAliasForAnnotation() == annotationClass) {
                return true;
            }
        }
        return false;
    }

    /**
     * Invokes the named attribute and returns its value.
     *
     * @param attribute the attribute name
     * @param <T>       the value type
     * @return the attribute value, or {@code null} if not found
     */
    public <T> T invokeAttribute(String attribute) {
        return invokeAttribute(attribute, null);
    }

    /**
     * Invokes the named attribute and returns its value, or the given
     * default if the attribute is not found.
     *
     * @param attribute    the attribute name
     * @param defaultValue the fallback value
     * @param <T>          the value type
     * @return the attribute value, or {@code defaultValue} if not found
     */
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

    /**
     * Sets the value of the named attribute.
     *
     * @param name  the attribute name
     * @param value the new value
     */
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

    /**
     * Resolves {@link AliasFor} relationships: for each attribute that
     * declares an alias, copies its value to the aliased attribute when
     * the latter still holds its default value.
     */
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
