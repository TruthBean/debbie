/**
 * Copyright (c) 2021 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 * http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.bean;

import com.truthbean.Logger;
import com.truthbean.debbie.annotation.AnnotationInfo;
import com.truthbean.debbie.core.ApplicationContextAware;
import com.truthbean.debbie.event.DebbieEventPublisherAware;
import com.truthbean.debbie.properties.DebbieConfiguration;
import com.truthbean.debbie.properties.DebbieProperties;
import com.truthbean.debbie.proxy.BeanProxyType;
import com.truthbean.debbie.proxy.javaassist.JavaassistProxyBean;
import com.truthbean.debbie.reflection.ClassInfo;
import com.truthbean.debbie.reflection.FieldInfo;
import com.truthbean.debbie.reflection.ReflectionHelper;
import com.truthbean.common.mini.util.StringUtils;
import com.truthbean.LoggerFactory;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * @author TruthBean/Rogar·Q
 * @since 0.2.0
 * Created on 2020-12-10 21:57
 */
public class DebbieClassBeanInfo<Bean> extends ClassInfo<Bean> implements ClassDetailedMutableBeanInfo<Bean> {
    private final Set<String> beanNames = new HashSet<>();
    private int order;

    private BeanType beanType;
    private BeanProxyType beanProxyType;
    private Boolean lazyCreate;

    private BeanFactory<Bean> beanFactory;
    private Bean bean;

    private final Map<String, Object> properties = new HashMap<>();

    private boolean noInterface = false;
    private Class<?> beanInterface;

    private Method initMethod;
    private Method destroyMethod;

    private List<BeanExecutableDependence> constructorBeanDependent;
    private List<BeanExecutableDependence> initMethodBeanDependent;
    private Map<FieldInfo, BeanInfo<?>> fieldBeanDependent;
    private boolean hasVirtualValue;

    private final Set<BeanCondition> conditions = new HashSet<>();

    public DebbieClassBeanInfo(Class<Bean> beanClass) {
        super(beanClass);
        Map<Class<? extends Annotation>, AnnotationInfo> classAnnotations = getClassAnnotations();
        if (classAnnotations == null || classAnnotations.isEmpty())
            return;

        var value = classAnnotations.get(BeanComponent.class);
        if (value != null) {
            var componentInfo = BeanComponentParser.parse(value);
            setBeanComponent(componentInfo);
        } else {
            LOGGER.debug("class(" + beanClass + ") no @BeanComponent");
        }
    }

    public DebbieClassBeanInfo(Class<Bean> beanClass, Map<Class<? extends Annotation>, BeanComponentParser> componentAnnotationTypes) {
        super(beanClass);
        Map<Class<? extends Annotation>, AnnotationInfo> classAnnotations = getClassAnnotations();
        if (classAnnotations == null || classAnnotations.isEmpty())
            return;

        var value = classAnnotations.get(BeanComponent.class);
        if (value != null) {
            var componentInfo = BeanComponentParser.parse(value);
            setBeanComponent(componentInfo);
        } else {
            // resolve customize component annotation
            LOGGER.debug("class(" + beanClass + ") no @BeanComponent");
            for (Map.Entry<Class<? extends Annotation>, BeanComponentParser> entry : componentAnnotationTypes.entrySet()) {
                var type = entry.getKey();
                var parser = entry.getValue();
                if (classAnnotations.containsKey(type)) {
                    var info = parser.parse(classAnnotations.get(type).getOrigin(), beanClass);
                    setBeanComponent(info);
                    break;
                }
            }
        }
    }

    public DebbieClassBeanInfo(BeanInfo<Bean> beanInfo) {
        super(beanInfo.getBeanClass());
        this.bean = beanInfo.getBean();
        this.addBeanName(beanInfo.getServiceName());
        this.addBeanNames(beanInfo.getBeanNames());
        this.beanFactory = beanInfo.getBeanFactory();
        this.beanType = beanInfo.getBeanType();
    }

    @Override
    public Set<BeanCondition> getConditions() {
        return conditions;
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

    public void addConstructorBeanDependent(Integer index, DebbieClassBeanInfo<?> beanInfo) {
        if (this.constructorBeanDependent == null) {
            this.constructorBeanDependent = new ArrayList<>();
        }
        this.constructorBeanDependent.add(new BeanExecutableDependence(index, beanInfo, beanInfo.getBeanClass()));
    }

    public void setInitMethodBeanDependent(List<BeanExecutableDependence> initMethodBeanDependent) {
        this.initMethodBeanDependent = initMethodBeanDependent;
    }

    public void addInitMethodBeanDependent(Integer index, DebbieClassBeanInfo<?> beanInfo) {
        if (this.initMethodBeanDependent == null) {
            this.initMethodBeanDependent = new ArrayList<>();
        }
        this.initMethodBeanDependent.add(new BeanExecutableDependence(index, beanInfo, beanInfo.getBeanClass()));
    }

    public void setFieldBeanDependent(Map<FieldInfo, BeanInfo<?>> fieldBeanDependent) {
        this.fieldBeanDependent = fieldBeanDependent;
    }

    public void addFieldBeanDependent(Field field, DebbieClassBeanInfo<?> debbieBeanInfo) {
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

    public void getCircleDependencyInConstructor() {
        if (constructorBeanDependent == null)
            return;
        for (BeanExecutableDependence dependence : constructorBeanDependent) {
            getCircleDependencyInConstructor(this, dependence.getBeanInfo(), new StringBuilder());
        }
    }

    public void getCircleDependencyInConstructor(BeanInfo<?> beanInfo, BeanInfo<?> dependency,
                                                 StringBuilder dependencyLine) {
        if (dependency == null) {
            return;
        }
        if (dependency instanceof DebbieClassBeanInfo) {
            DebbieClassBeanInfo<?> classBeanInfoDependency = (DebbieClassBeanInfo<?>) dependency;
            if (classBeanInfoDependency.constructorBeanDependent == null)
                return;
            Class<?> beanType = beanInfo.getBeanClass();
            for (BeanExecutableDependence dependence : classBeanInfoDependency.constructorBeanDependent) {
                var type = dependence.getType();
                LOGGER.trace(beanType + " ===>>> " + type);
                if (beanType == type) {
                    LOGGER.error(beanType + " --> " + type);
                } else {
                    getCircleDependencyInConstructor(beanInfo, dependence.getBeanInfo(), dependencyLine);
                }
            }
        }
    }

    public boolean isConstructorBeanDependentHasValue() {
        for (BeanExecutableDependence dependence : constructorBeanDependent) {
            if (dependence.getBeanInfo() != null && dependence.getBeanInfo().isEmpty())
                return false;
        }
        return true;
    }

    public Map<FieldInfo, BeanInfo<?>> getFieldBeanDependent() {
        return fieldBeanDependent;
    }


    public boolean hasNoVirtualValue() {
        return !hasVirtualValue;
    }

    public void setHasVirtualValue(boolean hasVirtualValue) {
        this.hasVirtualValue = hasVirtualValue;
    }

    @Override
    public Class<Bean> getBeanClass() {
        return this.getClazz();
    }

    @Override
    public void setBeanFactory(BeanFactory<Bean> beanFactory) {
        this.beanFactory = beanFactory;
    }

    @Override
    public BeanFactory<Bean> getBeanFactory() {
        return beanFactory;
    }

    @Override
    public boolean hasBeanFactory() {
        return beanFactory != null;
    }

    private boolean resolveBeanComponent(Annotation value) {
        if (value == null)
            return false;
        if (value.annotationType() == BeanComponent.class) {
            var beanService = ((BeanComponent) value);
            var info = new DefaultBeanComponentParser().parse(beanService);
            setBeanComponent(info);
            return true;
        }
        return false;
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
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
        else {
            // note: default value is true
            lazyCreate = true;
        }

        if (beanProxyType == null) {
            beanProxyType = info.getProxy();
        }

        if (beanFactory == null && info.getFactory() != null) {
            Class<? extends BeanFactory> factory = info.getFactory();
            if (factory != null && factory != BeanFactory.class) {
                BeanFactory beanFactory = ReflectionHelper.newInstance(factory, new Class[]{BeanInfo.class},
                        new Object[]{this});
                setBeanFactory(beanFactory);
            }
        }

        Class<? extends BeanCondition>[] condition = info.getCondition();
        if (condition != null && condition.length > 0) {
            for (Class<? extends BeanCondition> conditionClass : condition) {
                if (conditionClass == DefaultBeanCondition.class) {
                    this.conditions.add(DefaultBeanCondition.INSTANCE);
                } else {
                    BeanCondition beanCondition = ReflectionHelper.newInstance(conditionClass);
                    this.conditions.add(beanCondition);
                }
            }
        }
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

    @Override
    public void setBeanProxyType(BeanProxyType beanProxyType) {
        this.beanProxyType = beanProxyType;
    }

    @Override
    public boolean isSingleton() {
        return this.beanType != null && this.beanType == BeanType.SINGLETON;
    }

    @Override
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
                        || beanInterface == DebbieProperties.class || beanInterface == DebbieConfiguration.class
                        || beanInterface == ApplicationContextAware.class || beanInterface == ClassLoaderAware.class
                        || beanInterface == BeanAware.class || beanInterface == GlobalBeanFactoryAware.class
                        || beanInterface == InjectedBeanFactoryAware.class || beanInterface == DebbieEventPublisherAware.class) {
                    beanInterface = null;
                    noInterface = true;
                }
            }
        }
        return (Class<T>) beanInterface;
    }

    @Override
    public String getServiceName() {
        String name = this.beanNames.isEmpty() ? null : this.beanNames.iterator().next();
        if (name == null || name.isBlank()) {
            name = super.getClazz().getSimpleName();
            name = StringUtils.toFirstCharLowerCase(name);
            this.beanNames.add(name);
        }
        return name;
    }

    @Override
    public boolean containName(String name) {
        return this.beanNames.contains(name);
    }

    @Override
    public Set<String> getBeanNames() {
        return this.beanNames;
    }

    @Override
    public BeanType getBeanType() {
        return beanType;
    }

    @Override
    public BeanProxyType getBeanProxyType() {
        return beanProxyType;
    }

    @Override
    public void addBeanName(String beanName) {
        if (beanName != null && !beanName.isBlank())
            this.beanNames.add(beanName);
    }

    @Override
    public void addBeanNames(Set<String> beanNames) {
        this.beanNames.addAll(beanNames);
    }

    @Override
    public void setBean(Bean bean) {
        this.bean = bean;
    }

    @Override
    public void setBean(Supplier<Bean> bean) {
        this.bean = bean.get();
    }

    @Override
    public void addProperty(String name, Object value) {
        properties.put(name, value);
    }

    @Override
    public Object getProperty(String name) {
        return properties.get(name);
    }

    @Override
    public boolean isLazyCreate() {
        return lazyCreate != null && lazyCreate;
    }

    @Override
    public Bean getBean() {
        return bean;
    }

    @Override
    public Optional<Bean> optional() {
        return Optional.ofNullable(bean);
    }

    @Override
    public boolean isEmpty() {
        return bean == null;
    }

    @Override
    public boolean isPresent() {
        return bean != null;
    }

    @Override
    public Supplier<Bean> getBeanSupplier() {
        return () -> bean;
    }

    @Override
    public void consumer(Consumer<Bean> consumer) {
        consumer.accept(bean);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BeanInfo)) return false;
        // if (!super.equals(o)) return false;
        BeanInfo<?> beanInfo = (BeanInfo<?>) o;
        Set<String> beanNames = getBeanNames();
        Set<String> oBeanNames = beanInfo.getBeanNames();
        boolean beanNameEmpty = beanNames == null || beanNames.isEmpty() || oBeanNames == null || oBeanNames.isEmpty();
        if (beanNameEmpty) return true;
        if (beanNames.size() == oBeanNames.size()) {
            boolean[] equals = new boolean[beanNames.size()];
            int i = 0;
            for (String s1 : beanNames) {
                for (String s2 : oBeanNames) {
                    if (s1.equals(s2)) {
                        equals[i] = true;
                        break;
                    }
                }
                i++;
            }
            for (boolean equal : equals) {
                if (!equal) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    @Override
    public int hashCode() {
        if (beanNames.isEmpty()) {
            return Objects.hash(super.hashCode(), this.beanNames);
        }
        // 重新计算hashcode
        int h = 0;
        for (String obj : beanNames) {
            if (obj != null)
                h += obj.hashCode();
        }
        return h;
    }

    @Override
    public DebbieClassBeanInfo<Bean> copy() {
        DebbieClassBeanInfo<Bean> beanInfo = new DebbieClassBeanInfo<>(getClazz());

        if (!beanNames.isEmpty()) {
            beanInfo.beanNames.addAll(beanNames);
        }
        if (beanFactory != null) {
            beanInfo.setBeanFactory(beanFactory);
        }
        if (beanType != null)
            beanInfo.setBeanType(beanType);

        if (beanProxyType != null) {
            beanInfo.setBeanProxyType(beanProxyType);
        }

        if (!properties.isEmpty()) {
            beanInfo.properties.putAll(properties);
        }

        return beanInfo;
    }

    @Override
    public void release() {
        beanNames.clear();
        properties.clear();
        if (beanFactory != null) {
            beanFactory.destroy();
        } else {
            close();
        }
        order = 0;
        bean = null;
        if (constructorBeanDependent != null) {
            constructorBeanDependent.clear();
        }
        constructorBeanDependent = null;
        if (initMethodBeanDependent != null) {
            initMethodBeanDependent.clear();
        }
        initMethodBeanDependent = null;
        if (fieldBeanDependent != null) {
            fieldBeanDependent.clear();
        }
        fieldBeanDependent = null;
        hasVirtualValue = false;
    }

    @Override
    public String toString() {
        return "\"DebbieClassBeanInfo\":{" +
                "\"beanClass\":" + getBeanClass() + "," +
                "\"beanNames\":" + beanNames + "," +
                "\"order\":" + order + "," +
                "\"beanType\":" + beanType + "," +
                "\"lazyCreate\":" + lazyCreate + "," +
                "\"beanFactory\":" + beanFactory + "," +
                "\"bean\":" + bean + "," +
                "\"noInterface\":" + noInterface + "," +
                "\"beanInterface\":" + beanInterface + "," +
                "\"initMethod\":" + initMethod + "," +
                "\"destroyMethod\":" + destroyMethod + "," +
                "\"constructorBeanDependent\":" + constructorBeanDependent + "," +
                "\"initMethodBeanDependent\":" + initMethodBeanDependent + "," +
                "\"fieldBeanDependent\":" + fieldBeanDependent + "," +
                "\"hasVirtualValue\":" + hasVirtualValue + "}";
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(DebbieBeanInfo.class);
}