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
import com.truthbean.debbie.annotation.AnnotationInfo;
import com.truthbean.debbie.core.ApplicationContextAware;
import com.truthbean.debbie.event.DebbieEventPublisherAware;
import com.truthbean.debbie.properties.DebbieConfiguration;
import com.truthbean.debbie.properties.DebbieProperties;
import com.truthbean.debbie.proxy.javaassist.JavaassistProxyBean;
import com.truthbean.debbie.reflection.ClassInfo;
import com.truthbean.debbie.reflection.FieldInfo;
import com.truthbean.debbie.reflection.ReflectionHelper;
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
public class DebbieBeanInfo<Bean> implements MutableBeanInfo<Bean> {
    private final Set<String> beanNames = new HashSet<>();
    private int order;

    private BeanType beanType;
    private Boolean lazyCreate;

    private final Class<Bean> beanClass;
    private BeanFactory<Bean> beanFactory;
    private Bean bean;

    public DebbieBeanInfo(Class<Bean> beanClass) {
        this.beanClass = beanClass;
    }

    public DebbieBeanInfo(BeanInfo<Bean> beanInfo) {
        this.beanClass = beanInfo.getBeanClass();
        this.bean = beanInfo.getBean();
        this.addBeanName(beanInfo.getServiceName());
        this.addBeanNames(beanInfo.getBeanNames());
        this.beanFactory = beanInfo.getBeanFactory();
        this.beanType = beanInfo.getBeanType();
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

        if (beanFactory == null && info.getFactory() != null) {
            Class<? extends BeanFactory> factory = info.getFactory();
            if (factory != null && factory != BeanFactory.class) {
                BeanFactory beanFactory = ReflectionHelper.newInstance(factory, new Class[]{BeanInfo.class},
                        new Object[]{this});
                setBeanFactory(beanFactory);
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
    public boolean isSingleton() {
        return this.beanType != null && this.beanType == BeanType.SINGLETON;
    }

    @Override
    public Class<Bean> getBeanClass() {
        return beanClass;
    }

    @Override
    public String getServiceName() {
        String name = this.beanNames.isEmpty() ? null : this.beanNames.iterator().next();
        if (name == null || name.isBlank()) {
            name = this.getBeanClass().getSimpleName();
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
        DebbieBeanInfo<Bean> beanInfo = new DebbieBeanInfo<>(getBeanClass());

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

    @Override
    public void release() {
        beanNames.clear();
        if (beanFactory != null) {
            beanFactory.destroy();
        } else {
            Class<Bean> beanClass = getBeanClass();
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

    @Override
    public String toString() {
        return "\"DebbieBeanInfo\":{" +
                "\"beanNames\":" + beanNames + "," +
                "\"order\":" + order + "," +
                "\"beanType\":" + beanType + "," +
                "\"lazyCreate\":" + lazyCreate + "," +
                "\"beanClass\":" + beanClass + "," +
                "\"beanFactory\":" + beanFactory + "," +
                "\"bean\":" + bean + "}";
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(DebbieBeanInfo.class);
}
