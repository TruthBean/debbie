package com.truthbean.code.debbie.core.reflection;

import java.lang.annotation.Annotation;

/**
 * @author TruthBean
 * @since 0.0.1
 * Created on 2018-03-09 13:51
 */
public class InvokedParameter<T> implements Comparable<InvokedParameter>, Cloneable {
    private Class<T> type;
    private T value;
    private int index;
    private String name;
    private Annotation annotation;

    public Annotation getAnnotation() {
        return annotation;
    }

    public void setAnnotation(Annotation annotation) {
        this.annotation = annotation;
    }

    public Class<T> getType() {
        return type;
    }

    public void setType(Class<T> type) {
        this.type = type;
    }

    public T getValue() {
        return value;
    }

    public void setValue(T value) {
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

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof InvokedParameter)) {
            return false;
        }

        InvokedParameter that = (InvokedParameter) o;

        if (getType() != null ? !getType().equals(that.getType()) : that.getType() != null) {
            return false;
        }
        if (getValue() != null ? !getValue().equals(that.getValue()) : that.getValue() != null) {
            return false;
        }
        return (getName() != null ? getName().equals(that.getName()) : that.getName() == null) && (getAnnotation() != null ? getAnnotation().equals(that.getAnnotation()) : that.getAnnotation() == null);
    }

    @Override
    public int hashCode() {
        int result = getType() != null ? getType().hashCode() : 0;
        result = 31 * result + (getValue() != null ? getValue().hashCode() : 0);
        //result = 31 * result + getIndex();
        result = 31 * result + (getName() != null ? getName().hashCode() : 0);
        result = 31 * result + (getAnnotation() != null ? getAnnotation().hashCode() : 0);
        return result;
    }

    @Override
    public int compareTo(InvokedParameter o) {
        return this.index - o.index;
    }

    @Override
    public InvokedParameter<T> clone() {
        InvokedParameter<T> parameter = new InvokedParameter<>();
        parameter.type = type;
        parameter.value = value;
        parameter.index = index;
        parameter.name = name;
        parameter.annotation = annotation;
        return parameter;
    }

    public void resetValue(){
        this.value = null;
    }
}
