/**
 * Copyright (c) 2021 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *         http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.reflection;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Objects;

/**
 * 解决override问题
 * @author truthbean/Rogar·Q
 * @since 0.0.2
 * Created on 2020-04-26 19:03.
 */
public class MethodInfo {
    private final Annotation[] annotations;
    private final int modifiers;
    private final String name;
    private final Class<?> returnType;
    private final Class<?>[] paramTypes;
    private final Class<?>[] exceptionTypes;
    private final Method method;
    private final Class<?> declaringClass;

    public MethodInfo(Method method) {
        this.method = method;
        this.annotations = method.getDeclaredAnnotations();
        this.modifiers = method.getModifiers();
        this.name = method.getName();
        this.returnType = method.getReturnType();
        this.paramTypes = method.getParameterTypes();
        this.exceptionTypes = method.getExceptionTypes();

        this.declaringClass = method.getDeclaringClass();
    }

    public Class<?> getDeclaringClass() {
        return declaringClass;
    }

    public Annotation[] getAnnotations() {
        return annotations;
    }

    public int getModifiers() {
        return modifiers;
    }

    public boolean isPublic() {
        return Modifier.isPublic(modifiers);
    }

    public boolean isProtected() {
        return Modifier.isProtected(modifiers);
    }

    public boolean isPrivate() {
        return Modifier.isPrivate(modifiers);
    }

    public boolean isFinal() {
        return Modifier.isFinal(modifiers);
    }

    public boolean isStatic() {
        return Modifier.isStatic(modifiers);
    }

    public boolean isMethodParameterContainPrimitiveClass() {
        for (Class<?> paramType : paramTypes) {
            if (TypeHelper.isRawBaseType(paramType)) {
                return true;
            }
        }
        return false;
    }

    public String getName() {
        return name;
    }

    public Class<?> getReturnType() {
        return returnType;
    }

    public Class<?> getReturnWrapperType() {
        if (TypeHelper.isRawBaseType(returnType)) {
            return TypeHelper.getWrapperClass(returnType);
        }
        return returnType;
    }

    public Class<?>[] getParamTypes() {
        return paramTypes;
    }

    public boolean hasParams() {
        return paramTypes != null && paramTypes.length > 0 && (paramTypes.length == 1 && paramTypes[0] != null);
    }

    public int getParamSize() {
        return paramTypes != null ? paramTypes.length : 0;
    }

    public Class<?>[] getExceptionTypes() {
        return exceptionTypes;
    }

    public Method getMethod() {
        return method;
    }

    @SuppressWarnings("unchecked")
    public <T extends Annotation> T getAnnotation(Class<? extends Annotation> methodAnnotation) {
        return (T) method.getAnnotation(methodAnnotation);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MethodInfo that = (MethodInfo) o;
        return Objects.equals(name, that.name) &&
                Arrays.equals(paramTypes, that.paramTypes);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(name);
        result = 31 * result + Arrays.hashCode(paramTypes);
        return result;
    }
}
