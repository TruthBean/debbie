package com.truthbean.debbie.reflection.asm;

import org.objectweb.asm.Type;

import java.util.Objects;

/**
 * @author 璩诗斌
 * @since 0.0.1
 */
public class AsmClassInfo {

    private Type classType;
    private Type superClassType;
    private Type[] interfaceTypes;

    private int modifier;

    public Type getClassType() {
        return classType;
    }

    public void setClassType(Type classType) {
        this.classType = classType;
    }

    public Type getSuperClassType() {
        return superClassType;
    }

    public void setSuperClassType(Type superClassType) {
        this.superClassType = superClassType;
    }

    public Type[] getInterfaceTypes() {
        return interfaceTypes;
    }

    public void setInterfaceTypes(Type[] interfaceTypes) {
        this.interfaceTypes = interfaceTypes;
    }

    public int getModifier() {
        return modifier;
    }

    public void setModifier(int modifier) {
        this.modifier = modifier;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof AsmClassInfo)) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }
        AsmClassInfo that = (AsmClassInfo) o;
        return Objects.equals(getClassType(), that.getClassType());
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), getClassType());
    }
}
