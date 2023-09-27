/**
 * Copyright (c) 2023 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *         http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.asm.reflect;

import org.objectweb.asm.Type;

/**
 * A representation of a method signature, containing the method name,
 * return type, and parameter types.
 */
public class Signature {
    private final String name;
    private final String desc;

    public Signature(String name, String desc) {
        if (name.indexOf('(') >= 0) {
            throw new IllegalArgumentException("Name '" + name + "' is invalid");
        }
        this.name = name;
        this.desc = desc;
    }

    public Signature(String name, Type returnType, Type[] argumentTypes) {
        this(name, Type.getMethodDescriptor(returnType, argumentTypes));
    }

    public String getName() {
        return name;
    }

    public String getDescriptor() {
        return desc;
    }

    public Type getReturnType() {
        return Type.getReturnType(desc);
    }

    public Type[] getArgumentTypes() {
        return Type.getArgumentTypes(desc);
    }

    @Override
    public String toString() {
        return name + desc;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null) {
            return false;
        }
        if (!(o instanceof Signature)) {
            return false;
        }
        Signature other = (Signature)o;
        return name.equals(other.name) && desc.equals(other.desc);
    }

    @Override
    public int hashCode() {
        return name.hashCode() ^ desc.hashCode();
    }
}