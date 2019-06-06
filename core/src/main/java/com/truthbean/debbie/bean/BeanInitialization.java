package com.truthbean.debbie.bean;

import com.truthbean.debbie.spi.SpiLoader;

import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * @author TruthBean
 * @since 0.0.1
 * Created on 2019/3/23 11:47.
 */
public class BeanInitialization {
    static {
        // TODO: how to flush
        Set<AnnotationRegister> annotationRegisters = SpiLoader.loadProviders(AnnotationRegister.class);
        annotationRegisters.forEach(AnnotationRegister::register);
    }

    private BeanInitialization() {
    }

    private static final BeanInitialization INITIALIZATION = new BeanInitialization();

    static BeanInitialization getInstance() {
        return INITIALIZATION;
    }

    public void init(Class<?> beanClass) {
        BeanRegisterCenter.register(beanClass);
    }

    public void initSingletonBean(DebbieBeanInfo<?> beanInfo) {
        beanInfo.setBeanType(BeanType.SINGLETON);
        BeanRegisterCenter.register(beanInfo);
    }

    public void refreshBean(DebbieBeanInfo<?> beanInfo) {
        BeanRegisterCenter.refresh(beanInfo);
    }

    public void init(Set<Class<?>> beanClasses) {
        if (beanClasses != null && !beanClasses.isEmpty()) {
            beanClasses.forEach(this::init);
        }
    }

    public void init(String... packageName) {
        if (packageName != null) {
            for (String s : packageName) {
                BeanRegisterCenter.register(s);
            }
        }
    }

    public <T extends Annotation> Set<DebbieBeanInfo> getAnnotatedClass(Class<T> annotationClass) {
        return BeanRegisterCenter.getAnnotatedClass(annotationClass);
    }

    public <T extends Annotation> Set<DebbieBeanInfo> getAnnotatedMethodBean(Class<T> annotationClass) {
        return BeanRegisterCenter.getAnnotatedMethodsBean(annotationClass);
    }

    public Set<DebbieBeanInfo> getBeanByInterface(Class<?> interfaceType) {
        return BeanRegisterCenter.getBeansByInterface(interfaceType);
    }

    public Set<DebbieBeanInfo> getBeanByAbstractSuper(Class<?> abstractType) {
        return BeanRegisterCenter.getBeansByInterface(abstractType);
    }

    public Set<Class<? extends Annotation>> getBeanAnnotations() {
        return BeanRegisterCenter.getBeanAnnotations();
    }

    public <Bean> DebbieBeanInfo<Bean> getRegisterRawBean(Class<Bean> bean) {
        return BeanRegisterCenter.getRegisterRawBean(bean);
    }

    public <Bean> Bean getRegisterBean(Class<Bean> bean) {
        DebbieBeanInfo<Bean> registerRawBean = BeanRegisterCenter.getRegisterRawBean(bean);
        if (registerRawBean == null) {
            return null;
        }
        Bean resultBean = registerRawBean.getBean();
        BeanFactory<Bean> beanFactory = registerRawBean.getBeanFactory();
        if (resultBean != null) {
            return resultBean;
        } else if (beanFactory != null) {
            return beanFactory.getBean();
        } else {
            throw new NoBeanException("bean " + bean.getName() + " has no registered! ");
        }
    }

    public Set<DebbieBeanInfo> getRegisteredBeans() {
        Set<DebbieBeanInfo> result = new HashSet<>();
        Collection<DebbieBeanInfo> registerRawBeans = BeanRegisterCenter.getRegisterRawBeans();
        if (!registerRawBeans.isEmpty()) {
            for (DebbieBeanInfo registerRawBean : registerRawBeans) {
                if (registerRawBean.getBean() != null) {
                    result.add(registerRawBean);
                }
            }
        }
        return result;
    }

}
