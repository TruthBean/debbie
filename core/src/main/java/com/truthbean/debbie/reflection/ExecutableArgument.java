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

import com.truthbean.debbie.lang.Copyable;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * @see java.lang.reflect.Executable
 *
 * @author TruthBean
 * @since 0.0.2
 * Created on 2018-03-09 13:51
 */
public class ExecutableArgument implements Comparable<ExecutableArgument>, Copyable<ExecutableArgument> {
    private Type type;
    private Object value;
    private int index;
    private String name;
    private Map<Class<? extends Annotation>, Annotation> annotations;

    private String stack;

    private final ClassLoader classLoader;

    public ExecutableArgument(ClassLoader classLoader) {
        this.annotations = new HashMap<>();
        this.classLoader = classLoader;
    }

    public ClassLoader getClassLoader() {
        return classLoader;
    }

    public Map<Class<? extends Annotation>, Annotation> getAnnotations() {
        return annotations;
    }

    public Annotation getAnnotation(Class<? extends Annotation> clazz) {
        return annotations.get(clazz);
    }

    public void setAnnotations(Annotation[] annotations) {
        if (annotations != null && annotations.length > 0) {
            for (Annotation annotation : annotations) {
                this.annotations.put(annotation.annotationType(), annotation);
            }
        }
    }

    public void setAnnotation(Annotation annotation) {
        this.annotations.put(annotation.annotationType(), annotation);
    }

    public Type getType() {
        return type;
    }

    public Class<?> getRawType() {
        return TypeHelper.getClass(type);
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

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setStack(String stack) {
        this.stack = stack;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ExecutableArgument)) {
            return false;
        }

        ExecutableArgument that = (ExecutableArgument) o;

        if (getType() != null ? !getType().equals(that.getType()) : that.getType() != null) {
            return false;
        }
        if (getValue() != null ? !getValue().equals(that.getValue()) : that.getValue() != null) {
            return false;
        }
        return (getName() != null ? getName().equals(that.getName()) : that.getName() == null)
                && Objects.equals(annotations, that.annotations);
    }

    @Override
    public int hashCode() {
        int result = getType() != null ? getType().hashCode() : 0;
        result = 31 * result + (getValue() != null ? getValue().hashCode() : 0);
        //result = 31 * result + getIndex();
        result = 31 * result + (getName() != null ? getName().hashCode() : 0);
        result = 31 * result + (annotations != null ? annotations.hashCode() : 0);
        return result;
    }

    @Override
    public int compareTo(ExecutableArgument o) {
        return this.index - o.index;
    }

    @Override
    public ExecutableArgument copy() {
        ExecutableArgument parameter = new ExecutableArgument(this.classLoader);
        parameter.type = type;
        parameter.value = value;
        parameter.index = index;
        parameter.name = name;
        parameter.annotations = new HashMap<>(annotations);
        parameter.stack = stack;
        return parameter;
    }

    public void resetValue(){
        this.value = null;
    }

    @Override
    public String toString() {
        return "{" +
                "\"type\":" + type +
                ",\"VALUE\":" + value +
                ",\"index\":" + index +
                ",\"name\":\"" + name + '\"' +
                ",\"annotations\":" + annotations +
                '}';
    }

    public String stack() {
        return stack;
    }
}
