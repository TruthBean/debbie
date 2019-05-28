package com.truthbean.debbie.core.bean;

import com.truthbean.debbie.core.reflection.ClassInfo;
import com.truthbean.debbie.core.reflection.ReflectionHelper;
import com.truthbean.debbie.core.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Map;

/**
 * @author TruthBean
 * @since 0.0.1
 */
public class DebbieBeanInfo<Bean> extends ClassInfo<Bean> {
    private String beanName;
    private BeanInjectType injectType;

    private Bean bean;

    public DebbieBeanInfo(Class<Bean> clazz) {
        super(clazz);
        Map<Class<? extends Annotation>, Annotation> classAnnotations = getClassAnnotations();

        for (Map.Entry<Class<? extends Annotation>, Annotation> entry : classAnnotations.entrySet()) {
            Class<? extends Annotation> key = entry.getKey();
            Annotation value = entry.getValue();
            if (resolveBeanComponent(key, value)) break;
        }
    }

    private boolean resolveBeanComponent(Class<? extends Annotation> key, Annotation value) {
        if (key == BeanComponent.class) {
            var beanService = ((BeanComponent) value);
            beanName = beanService.value();
            injectType = beanService.type();
            return true;
        }

        BeanComponent annotation = key.getAnnotation(BeanComponent.class);
        if (annotation != null) {
            Method[] methods = key.getMethods();
            Method valueMethod = null;
            Method typeMethod = null;
            for (Method method : methods) {
                if ("value".equals(method.getName()) && method.getReturnType() == String.class) {
                    valueMethod = method;
                    continue;
                }
                if ("type".equals(method.getName()) && method.getReturnType() == BeanInjectType.class) {
                    typeMethod = method;
                }
            }

            if (valueMethod != null && typeMethod != null) {
                beanName = ReflectionHelper.invokeMethod(value, valueMethod);
                injectType = ReflectionHelper.invokeMethod(value, typeMethod);
            }
        }

        if (beanName == null || beanName.isBlank()) {
            beanName = getServiceName();
        }

        return false;
    }

    public <T> Class<T> getBeanClass() {
        return (Class<T>) super.getClazz();
    }

    public <T> Class<T> getBeanInterface() {
        Class<?> clazz = super.getClazz();
        Class<?>[] interfaces = clazz.getInterfaces();
        if (interfaces == null || interfaces.length == 0) {
            LOGGER.debug(clazz.getName() + " has no direct interface");
            return null;
        } else {
            return (Class<T>) interfaces[0];
        }
    }

    public String getServiceName() {
        String name = beanName;
        if (name == null || name.isBlank()) {
            name = super.getClazz().getSimpleName();
            name = StringUtils.toFirstCharLowerCase(name);
        }
        return name;
    }

    public BeanInjectType getInjectType() {
        return injectType;
    }

    public void setBeanName(String beanName) {
        this.beanName = beanName;
    }

    public void setBean(Bean bean) {
        this.bean = bean;
    }

    public Bean getBean() {
        return bean;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DebbieBeanInfo)) return false;
        DebbieBeanInfo that = (DebbieBeanInfo) o;
        return super.equals(that);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(DebbieBeanInfo.class);
}
