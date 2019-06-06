package com.truthbean.debbie.reflection.asm;

import org.objectweb.asm.Attribute;
import org.objectweb.asm.Type;

import java.util.Objects;

/**
 * @author 璩诗斌
 * @since 0.0.1
 */
public class AsmMethodInfo {
    private AsmClassInfo classInfo;
    private int modifier;
    private Signature signature;
    private Type[] exceptionTypes;
    private Attribute attribute;

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

    public Signature getSignature() {
        return signature;
    }

    public void setSignature(Signature signature) {
        this.signature = signature;
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
