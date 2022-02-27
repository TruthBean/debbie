/**
 * Copyright (c) 2022 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *         http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.asm.reflect;

import com.truthbean.debbie.reflection.MethodInfo;
import com.truthbean.debbie.reflection.TypeHelper;
import org.objectweb.asm.Attribute;
import org.objectweb.asm.Type;

import java.lang.reflect.Method;
import java.util.Objects;

/**
 * @author 璩诗斌
 * @since 0.0.1
 */
public class AsmMethodInfo extends MethodInfo {
    private AsmClassInfo classInfo;

    private final int access;
    private final String descriptor;
    private final String signature;

    private final String[] exceptions;

    private Attribute attribute;

    private final boolean idDefault;
    private final boolean isInterface;
    private final boolean isReturnVoid;

    public AsmMethodInfo(Method method) {
        super(method);
        this.access = AsmTypeHelper.getAccessByModifiers(method.getModifiers());
        this.descriptor = Type.getMethodDescriptor(method);
        this.signature = super.getName() + this.descriptor;

        this.exceptions = TypeHelper.getExceptions(method.getExceptionTypes());

        this.isInterface = method.getDeclaringClass().isInterface();
        this.isReturnVoid = method.getReturnType().isAssignableFrom(Void.class);
        this.idDefault = method.isDefault();
    }

    public int getAccess() {
        return access;
    }

    public String getDescriptor() {
        return descriptor;
    }

    public String getSignature() {
        return signature;
    }

    public String[] getExceptions() {
        return exceptions;
    }

    public boolean isDefault() {
        return idDefault;
    }

    public boolean isInterface() {
        return isInterface;
    }

    public boolean isReturnVoid() {
        return isReturnVoid;
    }

    public boolean canOverride() {
        return !isDefault() && !isPrivate() && !isFinal() && !isStatic();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof AsmMethodInfo that)) {
            return false;
        }
        return Objects.equals(getSignature(), that.getSignature());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getSignature());
    }
}
