package com.truthbean.debbie.bean;

import com.truthbean.debbie.reflection.ClassInfo;
import com.truthbean.debbie.reflection.ReflectionHelper;
import com.truthbean.debbie.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Supplier;

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

    private Map<Integer, DebbieBeanInfo<?>> constructorBeanDependent;
    private Map<Field, DebbieBeanInfo<?>> fieldBeanDependent;
    private boolean hasVirtualValue;

    public DebbieBeanInfo(Class<Bean> beanClass) {
        super(beanClass);
        Map<Class<? extends Annotation>, Annotation> classAnnotations = getClassAnnotations();
        if (classAnnotations == null || classAnnotations.isEmpty())
            return;

        for (Map.Entry<Class<? extends Annotation>, Annotation> entry : classAnnotations.entrySet()) {
            Class<? extends Annotation> key = entry.getKey();
            Annotation value = entry.getValue();
            // resolve BeanComponent if has no bean Annotation, use it
            resolveBeanComponent(key, value);
            // 如果有其他Annotation，则使用其他的，而不是BeanComponent
            if (resolveComponent(key, value))
                break;
        }
    }

    public void setConstructorBeanDependent(Map<Integer, DebbieBeanInfo<?>> constructorBeanDependent) {
        this.constructorBeanDependent = constructorBeanDependent;
    }

    public void addConstructorBeanDependent(Integer index, DebbieBeanInfo<?> beanInfo) {
        if (this.constructorBeanDependent == null) {
            this.constructorBeanDependent = new HashMap<>();
        }
        this.constructorBeanDependent.put(index, beanInfo);
    }

    public void setFieldBeanDependent(Map<Field, DebbieBeanInfo<?>> fieldBeanDependent) {
        this.fieldBeanDependent = fieldBeanDependent;
    }

    public void addFieldBeanDependent(Field field, DebbieBeanInfo<?> debbieBeanInfo) {
        if (this.fieldBeanDependent == null) {
            this.fieldBeanDependent = new HashMap<>();
        }
        this.fieldBeanDependent.put(field, debbieBeanInfo);
    }

    public Map<Integer, DebbieBeanInfo<?>> getConstructorBeanDependent() {
        if (constructorBeanDependent == null) {
            constructorBeanDependent = new HashMap<>();
        }
        return constructorBeanDependent;
    }

    public boolean isConstructorBeanDependentHasValue() {
        for (DebbieBeanInfo<?> value : constructorBeanDependent.values()) {
            if (!value.hasVirtualValue) {
                return false;
            }
        }
        return true;
    }

    public Map<Field, DebbieBeanInfo<?>> getFieldBeanDependent() {
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

    private void resolveBeanComponent(Class<? extends Annotation> key, Annotation value) {
        if (key == BeanComponent.class) {
            var beanService = ((BeanComponent) value);
            beanName = beanService.name();
            if (beanName.isBlank()) {
                beanName = beanService.value();
            }
            beanType = beanService.type();
            lazyCreate = beanService.lazy();
        }
    }

    private boolean resolveComponent(Class<? extends Annotation> key, Annotation value) {
        BeanComponent annotation = key.getAnnotation(BeanComponent.class);
        if (annotation != null) {
            Method[] methods = key.getMethods();
            Method valueMethod = null;
            Method nameMethod = null;
            Method typeMethod = null;
            Method lazyMethod = null;
            for (Method method : methods) {
                if ("value".equals(method.getName()) && method.getReturnType() == String.class
                        && method.getAnnotation(BeanAliceForValue.class) != null) {
                    valueMethod = method;
                    continue;
                }
                if ("name".equals(method.getName()) && method.getReturnType() == String.class
                        && method.getAnnotation(BeanAliceForName.class) != null) {
                    nameMethod = method;
                    continue;
                }
                if ("type".equals(method.getName()) && method.getReturnType() == BeanType.class
                    && method.getAnnotation(BeanAliceForType.class) != null) {
                    typeMethod = method;
                    continue;
                }
                if ("lazy".equals(method.getName()) && method.getReturnType() == BeanType.class
                    && method.getAnnotation(BeanAliceForLazy.class) != null) {
                    lazyMethod = method;
                }
            }

            if ((valueMethod != null || nameMethod != null) && typeMethod != null) {
                if (valueMethod != null) {
                    beanName = ReflectionHelper.invokeMethod(value, valueMethod);
                }
                if (!StringUtils.hasText(beanName) && nameMethod != null) {
                    beanName = ReflectionHelper.invokeMethod(value, nameMethod);
                }
                if (StringUtils.isBlank(beanName)) {
                    beanName = getServiceName();
                }

                beanType = ReflectionHelper.invokeMethod(value, typeMethod);

                if (lazyMethod != null) {
                    lazyCreate = ReflectionHelper.invokeMethod(value, lazyMethod);
                }

                return true;
            }
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

    @SuppressWarnings("unchecked")
    public <T> Class<T> getBeanClass() {
        return (Class<T>) super.getClazz();
    }

    @SuppressWarnings("unchecked")
    public <T> Class<T> getBeanInterface() {
        if (noInterface) return null;
        if (beanInterface == null) {
            Class<?> clazz = super.getClazz();
            Class<?>[] interfaces = clazz.getInterfaces();
            if (interfaces == null || interfaces.length == 0) {
                LOGGER.trace(clazz.getName() + " has no direct interface");
                noInterface = true;
                beanInterface = null;
            } else {
                beanInterface = interfaces[0];
                noInterface = false;
                if (beanInterface == Serializable.class) {
                    beanInterface = null;
                    noInterface = true;
                }
            }
        }
        return (Class<T>) beanInterface;
    }

    public String getServiceName() {
        String name = beanName;
        if (name == null || name.isBlank()) {
            name = super.getClazz().getSimpleName();
            name = StringUtils.toFirstCharLowerCase(name);
            beanName = name;
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

    public Supplier<Bean> getBeanSupplier() {
        return () -> bean;
    }

    public void consumer(Consumer<Bean> consumer) {
        consumer.accept(bean);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DebbieBeanInfo)) return false;
        if (!super.equals(o)) return false;
        DebbieBeanInfo<?> beanInfo = (DebbieBeanInfo<?>) o;
        String beanName = getServiceName();
        String oBeanName = beanInfo.getServiceName();
        boolean beanNameEmpty = beanName == null || beanName.isBlank() || oBeanName == null || oBeanName.isBlank();
        if (beanNameEmpty && super.equals(o)) return true;
        return Objects.equals(beanName, oBeanName);
    }

    @Override
    public int hashCode() {
        String beanName = getServiceName();
        return Objects.hash(super.hashCode(), beanName);
    }

    @Override
    public DebbieBeanInfo<Bean> copy() {
        DebbieBeanInfo<Bean> beanInfo = new DebbieBeanInfo<>(getClazz());

        if (beanName != null && !beanName.isEmpty()) {
            beanInfo.setBeanName(beanName);
        }
        if (beanFactory != null) {
            beanInfo.setBeanFactory(beanFactory);
        }
        if (beanType != null)
            beanInfo.setBeanType(beanType);

        return beanInfo;
    }

    public void release() {
        if (beanFactory != null) {
            beanFactory.destroy();
        } else {
            Class<Bean> beanClass = getClazz();
            if (BeanClosure.class.isAssignableFrom(beanClass) && !DebbieBeanFactory.class.isAssignableFrom(beanClass)) {
                if (bean != null) {
                    ((BeanClosure) bean).destroy();
                }
            }
        }
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(DebbieBeanInfo.class);
}
