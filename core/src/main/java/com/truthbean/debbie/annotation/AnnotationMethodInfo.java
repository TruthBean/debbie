/**
 * Copyright (c) 2026 TruthBean(Rogar·Q)
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

/**
 * Metadata for a single annotation attribute (method), including its
 * value, {@link AliasFor} declaration, and whether the current value
 * is still the default.
 *
 * @author TruthBean/Rogar·Q
 * @since 0.1.0
 * Created on 2020-09-24 14:39
 */
public class AnnotationMethodInfo {
    /**
     * the attribute (method) name
     */
    private String methodName;

    /**
     * whether this attribute declares an {@link AliasFor}
     */
    private boolean aliasFor;
    /**
     * the aliased attribute name
     */
    private String aliasForAttribute;
    /**
     * the annotation type containing the aliased attribute
     */
    private Class<? extends Annotation> aliasForAnnotation;

    /**
     * the current attribute value
     */
    private Object value;

    /**
     * the underlying annotation method
     */
    private Method method;

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public String getAliasForAttribute() {
        return aliasForAttribute;
    }

    public void setAliasForAttribute(String aliasForAttribute) {
        this.aliasForAttribute = aliasForAttribute;
    }

    public Class<? extends Annotation> getAliasForAnnotation() {
        return aliasForAnnotation;
    }

    public void setAliasForAnnotation(Class<? extends Annotation> aliasForAnnotation) {
        this.aliasForAnnotation = aliasForAnnotation;
    }

    public Object getValue() {
        return value;
    }

    /**
     * Returns whether the current value is still the annotation default.
     *
     * @return {@code true} if the value is null or equals the method's default
     */
    public boolean isDefaultValue() {
        return value == null || value == method.getDefaultValue() || value.equals(method.getDefaultValue());
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public Method getMethod() {
        return method;
    }

    public void setMethod(Method method) {
        this.method = method;
    }

    public boolean hasAliasFor() {
        return aliasFor;
    }

    public void setAliasFor(boolean aliasFor) {
        this.aliasFor = aliasFor;
    }

    /**
     * Checks whether this attribute aliases the given attribute name
     * and has a compatible return type.
     *
     * @param name             the candidate aliased attribute name
     * @param methodReturnType the expected return type
     * @return {@code true} if this attribute aliases the given name and type
     */
    public boolean aliasFor(String name, Class<?> methodReturnType) {
        return name.equals(aliasForAttribute) && methodReturnType == method.getReturnType();
    }

    @Override
    public String toString() {
        return "{" + "\"methodName\":\"" + methodName + "\"" + ","
                + "\"aliasFor\":" + aliasFor + ","
                + "\"aliasForAttribute\":\"" + aliasForAttribute + "\"" + ","
                + "\"aliasForAnnotation\":" + aliasForAnnotation + ","
                + "\"VALUE\":" + value + ","
                + "\"method\":" + method + "}";
    }
}
