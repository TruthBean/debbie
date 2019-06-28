package com.truthbean.debbie.bean;

import com.truthbean.debbie.reflection.ClassInfo;
import com.truthbean.debbie.reflection.ReflectionHelper;
import com.truthbean.debbie.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;

/**
 * @author TruthBean
 * @since 0.0.1
 */
public class DebbieBeanInfo<Bean> extends ClassInfo<Bean> {
    private String beanName;
    private BeanType beanType;
    private Boolean lazyCreate;

    private BeanFactory<Bean> beanFactory;
    private Bean bean;

    private boolean noInterface = false;
    private Class<?> beanInterface;

    private Map<Integer, DebbieBeanInfo> constructorBeanDependent;
    private Map<Field, DebbieBeanInfo> fieldBeanDependent;
    private boolean hasVirtualValue;

    public DebbieBeanInfo(Class<Bean> clazz) {
        super(clazz);
        Map<Class<? extends Annotation>, Annotation> classAnnotations = getClassAnnotations();

        for (Map.Entry<Class<? extends Annotation>, Annotation> entry : classAnnotations.entrySet()) {
            Class<? extends Annotation> key = entry.getKey();
            Annotation value = entry.getValue();
            if (resolveBeanComponent(key, value))
                break;
        }
    }

    public void setConstructorBeanDependent(Map<Integer, DebbieBeanInfo> constructorBeanDependent) {
        this.constructorBeanDependent = constructorBeanDependent;
    }

    public void addConstructorBeanDependent(Integer index, DebbieBeanInfo beanInfo) {
        if (this.constructorBeanDependent == null) {
            this.constructorBeanDependent = new HashMap<>();
        }
        this.constructorBeanDependent.put(index, beanInfo);
    }

    public void setFieldBeanDependent(Map<Field, DebbieBeanInfo> fieldBeanDependent) {
        this.fieldBeanDependent = fieldBeanDependent;
    }

    public void addFieldBeanDependent(Field field, DebbieBeanInfo debbieBeanInfo) {
        if (this.fieldBeanDependent == null) {
            this.fieldBeanDependent = new HashMap<>();
        }
        this.fieldBeanDependent.put(field, debbieBeanInfo);
    }

    public Map<Integer, DebbieBeanInfo> getConstructorBeanDependent() {
        return constructorBeanDependent;
    }

    public boolean isConstructorBeanDependentHasValue() {
        for (DebbieBeanInfo value : constructorBeanDependent.values()) {
            if (!value.hasVirtualValue) {
                return false;
            }
        }
        return true;
    }

    public Map<Field, DebbieBeanInfo> getFieldBeanDependent() {
        return fieldBeanDependent;
    }


    public boolean isHasVirtualValue() {
        return hasVirtualValue;
    }

    public void setHasVirtualValue(boolean hasVirtualValue) {
        this.hasVirtualValue = hasVirtualValue;
    }

    public void setBeanFactory(BeanFactory<Bean> beanFactory) {
        this.beanFactory = beanFactory;
    }

    public BeanFactory<Bean> getBeanFactory() {
        return beanFactory;
    }

    private boolean resolveBeanComponent(Class<? extends Annotation> key, Annotation value) {
        if (key == BeanComponent.class) {
            var beanService = ((BeanComponent) value);
            beanName = beanService.name();
            if (beanName.isBlank()) {
                beanName = beanService.value();
            }
            beanType = beanService.type();
            lazyCreate = beanService.lazy();
            return true;
        }

        BeanComponent annotation = key.getAnnotation(BeanComponent.class);
        if (annotation != null) {
            Method[] methods = key.getMethods();
            Method valueMethod = null;
            Method typeMethod = null;
            Method lazyMethod = null;
            for (Method method : methods) {
                if ("value".equals(method.getName()) && method.getReturnType() == String.class) {
                    valueMethod = method;
                    continue;
                }
                if ("type".equals(method.getName()) && method.getReturnType() == BeanType.class) {
                    typeMethod = method;
                }
                if ("lazy".equals(method.getName()) && method.getReturnType() == BeanType.class) {
                    lazyMethod = method;
                }
            }

            if (valueMethod != null && typeMethod != null) {
                beanName = ReflectionHelper.invokeMethod(value, valueMethod);
                beanType = ReflectionHelper.invokeMethod(value, typeMethod);
                lazyCreate = ReflectionHelper.invokeMethod(value, lazyMethod);
            }
        }

        if (beanName == null || beanName.isBlank()) {
            beanName = getServiceName();
        }

        return false;
    }

    /**
     * WARN: only used by BeanInitialization.init(DebbieBeanInfo)
     *
     * @param beanType BeanType
     */
    void setBeanType(BeanType beanType) {
        this.beanType = beanType;
    }

    public <T> Class<T> getBeanClass() {
        return (Class<T>) super.getClazz();
    }

    public <T> Class<T> getBeanInterface() {
        if (noInterface) return null;
        if (beanInterface == null) {
            Class<?> clazz = super.getClazz();
            Class<?>[] interfaces = clazz.getInterfaces();
            if (interfaces == null || interfaces.length == 0) {
                LOGGER.debug(clazz.getName() + " has no direct interface");
                noInterface = true;
                beanInterface = null;
            } else {
                beanInterface = interfaces[0];
                noInterface = false;
            }
        }
        return (Class<T>) beanInterface;
    }

    public String getServiceName() {
        String name = beanName;
        if (name == null || name.isBlank()) {
            name = super.getClazz().getSimpleName();
            name = StringUtils.toFirstCharLowerCase(name);
        }
        return name;
    }

    public BeanType getBeanType() {
        return beanType;
    }

    public void setBeanName(String beanName) {
        this.beanName = beanName;
    }

    public void setBean(Bean bean) {
        this.bean = bean;
    }

    public Boolean getLazyCreate() {
        return lazyCreate;
    }

    public Bean getBean() {
        return bean;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DebbieBeanInfo)) return false;
        if (!super.equals(o)) return false;
        DebbieBeanInfo<?> beanInfo = (DebbieBeanInfo<?>) o;
        return Objects.equals(beanName, beanInfo.beanName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), beanName);
    }

    @Override
    public DebbieBeanInfo<Bean> copy() {
        return new DebbieBeanInfo<>(getClazz());
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(DebbieBeanInfo.class);
}
