package com.truthbean.debbie.reflection.asm;

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
        this.access = TypeHelper.getAccessByModifiers(method.getModifiers());
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

    public boolean isIdDefault() {
        return idDefault;
    }

    public boolean isInterface() {
        return isInterface;
    }

    public boolean isReturnVoid() {
        return isReturnVoid;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof AsmMethodInfo)) {
            return false;
        }
        AsmMethodInfo that = (AsmMethodInfo) o;
        return Objects.equals(getSignature(), that.getSignature());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getSignature());
    }
}
