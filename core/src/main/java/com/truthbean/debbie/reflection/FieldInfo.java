/**
 * Copyright (c) 2020 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 * http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.reflection;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Objects;

/**
 * @author TruthBean/Rogar·Q
 * @since 0.1.0
 * Created on 2020-06-28 11:14.
 */
public class FieldInfo {
    private final Field field;
    private boolean hasValue;

    public FieldInfo(Field field) {
        this.field = field;
        int modifiers = this.field.getModifiers();
        if (Modifier.isFinal(modifiers)) {
            hasValue = true;
        }
    }

    public Field getField() {
        return field;
    }

    public void setValue() {
        this.hasValue = true;
    }

    public boolean hasValue() {
        return hasValue;
    }

    public <A extends Annotation> A getAnnotation(Class<A> annotation) {
        return field.getAnnotation(annotation);
    }

    public Class<?> getType() {
        return field.getType();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof FieldInfo)) return false;
        FieldInfo fieldInfo = (FieldInfo) o;
        return getField().equals(fieldInfo.getField());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getField());
    }

}
