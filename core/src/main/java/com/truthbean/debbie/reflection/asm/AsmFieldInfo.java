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

import org.objectweb.asm.Type;

/**
 * @author 璩诗斌
 * @since 0.0.1
 */
public class AsmFieldInfo {
    private int access;
    private String name;
    private Type type;
    private Object value;

    public AsmFieldInfo() {
    }

    public AsmFieldInfo(int access, String name, Type type, Object value) {
        this.access = access;
        this.name = name;
        this.type = type;
        this.value = value;
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

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null) {
            return false;
        }
        if (!(o instanceof AsmFieldInfo)) {
            return false;
        }
        AsmFieldInfo other = (AsmFieldInfo) o;
        if (access != other.access ||
                !name.equals(other.name) ||
                !type.equals(other.type)) {
            return false;
        }
        if ((value == null) ^ (other.value == null)) {
            return false;
        }
        return value == null || value.equals(other.value);
    }

    @Override
    public int hashCode() {
        return access ^ name.hashCode() ^ type.hashCode() ^ ((value == null) ? 0 : value.hashCode());
    }
}
