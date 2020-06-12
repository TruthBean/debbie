/**
 * Copyright (c) 2020 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *         http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.reflection.asm;

import com.truthbean.debbie.reflection.TypeHelper;
import org.objectweb.asm.Attribute;
import org.objectweb.asm.Type;

import java.lang.reflect.Constructor;

/**
 * @author truthbean/Rogar·Q
 * @since 0.0.2
 * Created on 2020-04-24 10:01.
 */
public class AsmConstructorInfo implements Comparable<AsmConstructorInfo> {
    private AsmClassInfo classInfo;

    private int modifier;
    private int access;
    private String name;
    private String descriptor;
    private String signature;

    private String[] exceptions;
    private Type[] exceptionTypes;

    private Attribute attribute;

    private Class<?>[] parameterTypes;
    private Constructor<?> constructor;

    public AsmConstructorInfo() {
    }

    public AsmConstructorInfo(Constructor<?> constructor) {
        this.access = TypeHelper.getAccessByModifiers(constructor.getModifiers());
        this.name = constructor.getName();
        this.descriptor = Type.getConstructorDescriptor(constructor);
        this.signature = this.name + this.descriptor;

        this.parameterTypes = constructor.getParameterTypes();

        this.exceptions = TypeHelper.getExceptions(constructor.getExceptionTypes());
        this.constructor = constructor;
    }

    public AsmClassInfo getClassInfo() {
        return classInfo;
    }

    public void setClassInfo(AsmClassInfo classInfo) {
        this.classInfo = classInfo;
    }

    public int getModifier() {
        return modifier;
    }

    public void setModifier(int modifier) {
        this.modifier = modifier;
    }

    public int getAccess() {
        return access;
    }

    public void setAccess(int access) {
        this.access = access;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescriptor() {
        return descriptor;
    }

    public void setDescriptor(String descriptor) {
        this.descriptor = descriptor;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    public String[] getExceptions() {
        return exceptions;
    }

    public void setExceptions(String[] exceptions) {
        this.exceptions = exceptions;
    }

    public Type[] getExceptionTypes() {
        return exceptionTypes;
    }

    public void setExceptionTypes(Type[] exceptionTypes) {
        this.exceptionTypes = exceptionTypes;
    }

    public Attribute getAttribute() {
        return attribute;
    }

    public void setAttribute(Attribute attribute) {
        this.attribute = attribute;
    }

    public Class<?>[] getParameterTypes() {
        return parameterTypes;
    }

    public void setParameterTypes(Class<?>[] parameterTypes) {
        this.parameterTypes = parameterTypes;
    }

    public Constructor<?> getConstructor() {
        return constructor;
    }

    public void setConstructor(Constructor<?> constructor) {
        this.constructor = constructor;
    }

    public Object[] makeConstructorDefaultValue() {
        final Object[] result = new Object[parameterTypes.length];
        if (parameterTypes.length > 0 && parameterTypes[0] != null) {
            int n = parameterTypes.length;
            for (int i = 0; i < n; i++) {
                Class<?> parameterType = parameterTypes[i];
                if (parameterType == byte.class) {
                    result[i] = (byte) 0;
                } else if (parameterType == char.class) {
                    result[i] = (char) 0;
                } else if (parameterType == short.class) {
                    result[i] = (short) 0;
                } else if (parameterType == int.class) {
                    result[i] = 0;
                } else if (parameterType == long.class) {
                    result[i] = 0L;
                } else if (parameterType == float.class) {
                    result[i] = 0.0F;
                } else if (parameterType == double.class) {
                    result[i] = 0.0D;
                } else if (parameterType == boolean.class) {
                    result[i] = false;
                } else {
                    result[i] = null;
                }
            }
        }
        return result;
    }

    @Override
    public int compareTo(AsmConstructorInfo o) {
        return Integer.compare(constructor.getParameterCount(), o.constructor.getParameterCount());
    }
}
