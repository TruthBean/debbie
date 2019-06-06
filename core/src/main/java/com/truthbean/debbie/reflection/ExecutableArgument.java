package com.truthbean.debbie.reflection;

import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.Map;

/**
 * @see java.lang.reflect.Executable
 *
 * @author TruthBean
 * @since 0.0.2
 * Created on 2018-03-09 13:51
 */
public class ExecutableArgument implements Comparable<ExecutableArgument>, Cloneable {
    private Class<?> type;
    private Object value;
    private int index;
    private String name;
    private Map<Class<? extends Annotation>, Annotation> annotations;

    public ExecutableArgument() {
        this.annotations = new HashMap<>();
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

    public Class<?> getType() {
        return type;
    }

    public void setType(Class<?> type) {
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
                && (annotations != null ? annotations.equals(that.annotations) : that.annotations == null);
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
    public ExecutableArgument clone() {
        ExecutableArgument parameter;
        try {
            parameter = (ExecutableArgument) super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
            parameter = new ExecutableArgument();
        }
        parameter.type = type;
        parameter.value = value;
        parameter.index = index;
        parameter.name = name;
        parameter.annotations = new HashMap<>(annotations);
        return parameter;
    }

    public void resetValue(){
        this.value = null;
    }
}
