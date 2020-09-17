/**
 * Copyright (c) 2020 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 * http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.bean;

import com.truthbean.Logger;
import com.truthbean.debbie.properties.DebbieConfiguration;
import com.truthbean.debbie.properties.DebbieProperties;
import com.truthbean.debbie.proxy.javaassist.JavaassistProxyBean;
import com.truthbean.debbie.reflection.ClassInfo;
import com.truthbean.debbie.reflection.FieldInfo;
import com.truthbean.debbie.util.StringUtils;
import com.truthbean.logger.LoggerFactory;

import java.io.Closeable;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * @author TruthBean
 * @since 0.0.1
 */
public class DebbieBeanInfo<Bean> extends ClassInfo<Bean> implements WriteableBeanInfo<Bean> {
    private final Set<String> beanNames = new HashSet<>();
    private int order;

    private BeanType beanType;
    private Boolean lazyCreate;

    private BeanFactory<Bean> beanFactory;
    private Bean bean;

    private boolean noInterface = false;
    private Class<?> beanInterface;

    private Method initMethod;
    private Method destroyMethod;

    private List<BeanExecutableDependence> constructorBeanDependent;
    private List<BeanExecutableDependence> initMethodBeanDependent;
    private Map<FieldInfo, DebbieBeanInfo<?>> fieldBeanDependent;
    private boolean hasVirtualValue;

    public DebbieBeanInfo(Class<Bean> beanClass) {
        super(beanClass);
        Map<Class<? extends Annotation>, Annotation> classAnnotations = getClassAnnotations();
        if (classAnnotations == null || classAnnotations.isEmpty())
            return;

        for (Map.Entry<Class<? extends Annotation>, Annotation> entry : classAnnotations.entrySet()) {
            Class<? extends Annotation> key = entry.getKey();
            Annotation value = entry.getValue();
            // 如果有其他Annotation，则使用其他的，而不是BeanComponent
            if (key != BeanComponent.class && resolveComponent(key, value))
                break;
        }
        // resolve BeanComponent if has no bean Annotation, use it
        if (!resolveBeanComponent(classAnnotations.get(BeanComponent.class))) {
            // resolve custom component annotation
            // todo
            LOGGER.warn("class(" + beanClass + ") no @BeanComponent");
        }
    }

    public void setInitMethod(Method initMethod) {
        this.initMethod = initMethod;
    }

    public Method getInitMethod() {
        return initMethod;
    }

    public void setDestroyMethod(Method destroyMethod) {
        this.destroyMethod = destroyMethod;
    }

    public Method getDestroyMethod() {
        return destroyMethod;
    }

    public void setConstructorBeanDependent(List<BeanExecutableDependence> constructorBeanDependent) {
        this.constructorBeanDependent = constructorBeanDependent;
    }

    public void addConstructorBeanDependent(Integer index, DebbieBeanInfo<?> beanInfo) {
        if (this.constructorBeanDependent == null) {
            this.constructorBeanDependent = new ArrayList<>();
        }
        this.constructorBeanDependent.add(new BeanExecutableDependence(index, beanInfo, beanInfo.getBeanClass()));
    }

    public void setInitMethodBeanDependent(List<BeanExecutableDependence> initMethodBeanDependent) {
        this.initMethodBeanDependent = initMethodBeanDependent;
    }

    public void addInitMethodBeanDependent(Integer index, DebbieBeanInfo<?> beanInfo) {
        if (this.initMethodBeanDependent == null) {
            this.initMethodBeanDependent = new ArrayList<>();
        }
        this.initMethodBeanDependent.add(new BeanExecutableDependence(index, beanInfo, beanInfo.getBeanClass()));
    }

    public void setFieldBeanDependent(Map<FieldInfo, DebbieBeanInfo<?>> fieldBeanDependent) {
        this.fieldBeanDependent = fieldBeanDependent;
    }

    public void addFieldBeanDependent(Field field, DebbieBeanInfo<?> debbieBeanInfo) {
        if (this.fieldBeanDependent == null) {
            this.fieldBeanDependent = new HashMap<>();
        }
        this.fieldBeanDependent.put(new FieldInfo(field), debbieBeanInfo);
    }

    public List<BeanExecutableDependence> getInitMethodBeanDependent() {
        if (initMethodBeanDependent == null) {
            initMethodBeanDependent = new ArrayList<>();
        }
        return initMethodBeanDependent;
    }

    public boolean isInitMethodBeanDependentHasValue() {
        for (BeanExecutableDependence dependence : initMethodBeanDependent) {
            if (!dependence.isPresent()) {
                return false;
            }
        }
        return true;
    }

    public List<BeanExecutableDependence> getConstructorBeanDependent() {
        if (constructorBeanDependent == null) {
            constructorBeanDependent = new ArrayList<>();
        }
        return constructorBeanDependent;
    }

    public boolean isConstructorBeanDependentHasValue() {
        for (BeanExecutableDependence dependence : constructorBeanDependent) {
            if (dependence.getBeanInfo() != null && dependence.getBeanInfo().isEmpty())
                return false;
        }
        return true;
    }

    public Map<FieldInfo, DebbieBeanInfo<?>> getFieldBeanDependent() {
        return fieldBeanDependent;
    }


    public boolean hasNoVirtualValue() {
        return !hasVirtualValue;
    }

    public void setHasVirtualValue(boolean hasVirtualValue) {
        this.hasVirtualValue = hasVirtualValue;
    }

    @Override
    public void setBeanFactory(BeanFactory<Bean> beanFactory) {
        this.beanFactory = beanFactory;
    }

    public BeanFactory<Bean> getBeanFactory() {
        return beanFactory;
    }

    public boolean hasBeanFactory() {
        return beanFactory != null;
    }

    private boolean resolveBeanComponent(Annotation value) {
        if (value == null)
            return false;
        if (value.annotationType() == BeanComponent.class) {
            var beanService = ((BeanComponent) value);
            var info = BeanComponentParser.parse(beanService);
            setBeanComponent(info);
            return true;
        }
        return false;
    }

    private void setBeanComponent(BeanComponentInfo info) {
        if (beanNames.isEmpty()) {
            if (info.hasName()) {
                beanNames.add(info.getName());
            }
        }
        if (beanType == null)
            beanType = info.getType();

        if (lazyCreate == null)
            lazyCreate = info.isLazy();
    }

    private boolean resolveComponent(Class<? extends Annotation> key, Annotation value) {
        var info = BeanComponentParser.parse(key, value);
        if (info != null) {
            setBeanComponent(info);
            return true;
        }

        return false;
    }

    /**
     * WARN: only used by BeanInitialization.init(DebbieBeanInfo)
     *
     * @param beanType BeanType
     */
    @Override
    public void setBeanType(BeanType beanType) {
        this.beanType = beanType;
    }

    public boolean isSingleton() {
        return this.beanType != null && this.beanType == BeanType.SINGLETON;
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
            if (interfaces.length == 0) {
                LOGGER.trace(() -> clazz.getName() + " has no direct interface");
                noInterface = true;
                beanInterface = null;
            } else {
                beanInterface = interfaces[0];
                noInterface = false;
                if (beanInterface.getPackageName().startsWith("java.")
                        || beanInterface == JavaassistProxyBean.class
                        || beanInterface == DebbieProperties.class || beanInterface == DebbieConfiguration.class) {
                    beanInterface = null;
                    noInterface = true;
                }
            }
        }
        return (Class<T>) beanInterface;
    }

    public String getServiceName() {
        String name = this.beanNames.isEmpty() ? null : this.beanNames.iterator().next();
        if (name == null || name.isBlank()) {
            name = super.getClazz().getSimpleName();
            name = StringUtils.toFirstCharLowerCase(name);
            this.beanNames.add(name);
        }
        return name;
    }

    public boolean containName(String name) {
        return this.beanNames.contains(name);
    }

    public Set<String> getBeanNames() {
        return this.beanNames;
    }

    public BeanType getBeanType() {
        return beanType;
    }

    @Override
    public void addBeanName(String beanName) {
        if (beanName != null && !beanName.isBlank())
            this.beanNames.add(beanName);
    }

    public void addBeanNames(Set<String> beanNames) {
        this.beanNames.addAll(beanNames);
    }

    @Override
    public void setBean(Bean bean) {
        this.bean = bean;
    }

    public Boolean getLazyCreate() {
        return lazyCreate;
    }

    public Bean getBean() {
        return bean;
    }

    public Optional<Bean> optional() {
        return Optional.ofNullable(bean);
    }

    public boolean isEmpty() {
        return bean == null;
    }

    public boolean isPresent() {
        return bean != null;
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
        Set<String> beanNames = getBeanNames();
        Set<String> oBeanNames = beanInfo.getBeanNames();
        boolean beanNameEmpty = beanNames == null || beanNames.isEmpty() || oBeanNames == null || oBeanNames.isEmpty();
        if (beanNameEmpty) return true;
        return Objects.equals(beanNames, oBeanNames);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), this.beanNames);
    }

    @Override
    public DebbieBeanInfo<Bean> copy() {
        DebbieBeanInfo<Bean> beanInfo = new DebbieBeanInfo<>(getClazz());

        if (!beanNames.isEmpty()) {
            beanInfo.beanNames.addAll(beanNames);
        }
        if (beanFactory != null) {
            beanInfo.setBeanFactory(beanFactory);
        }
        if (beanType != null)
            beanInfo.setBeanType(beanType);

        return beanInfo;
    }

    public void release() {
        beanNames.clear();
        if (beanFactory != null) {
            beanFactory.destroy();
        } else {
            Class<Bean> beanClass = getClazz();
            if (!DebbieBeanFactory.class.isAssignableFrom(beanClass) && bean != null) {
                if (BeanClosure.class.isAssignableFrom(beanClass)) {
                    ((BeanClosure) bean).destroy();
                }
                if (Closeable.class.isAssignableFrom(beanClass)) {
                    try {
                        ((Closeable) bean).close();
                    } catch (IOException e) {
                        LOGGER.error("", e);
                    }
                }
                if (AutoCloseable.class.isAssignableFrom(beanClass)) {
                    try {
                        ((AutoCloseable) bean).close();
                    } catch (Exception e) {
                        LOGGER.error("", e);
                    }
                }
            }
        }
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(DebbieBeanInfo.class);
}
