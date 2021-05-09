/**
 * Copyright (c) 2021 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *         http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.reflection.asm;

import com.truthbean.debbie.reflection.TypeHelper;
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

    private int access;
    private int modifier;
    private String name;
    private String descriptor;
    private String signature;

    private Class<?> beanClass;
    private boolean isInterface;

    public AsmClassInfo(Class<?> beanClass) {
        this.access = TypeHelper.getAccessByModifiers(beanClass.getModifiers());
        this.name = beanClass.getName();
        this.descriptor = Type.getDescriptor(beanClass);
        this.signature = this.name + this.descriptor;

        this.beanClass = beanClass;

        this.isInterface = beanClass.isInterface();
    }

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

    public Class<?> getBeanClass() {
        return beanClass;
    }

    public void setBeanClass(Class<?> beanClass) {
        this.beanClass = beanClass;
    }

    public boolean isInterface() {
        return isInterface;
    }

    public void setInterface(boolean anInterface) {
        isInterface = anInterface;
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
