package com.truthbean.debbie.bean;

import com.truthbean.debbie.data.transformer.DataTransformer;
import com.truthbean.debbie.data.transformer.DataTransformerFactory;
import com.truthbean.debbie.reflection.ReflectionHelper;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * @author TruthBean
 * @since 0.0.1
 * Created on 2019/3/23 11:47.
 */
public class BeanInitialization {
    private BeanInitialization() {
        BeanRegisterCenter.registerBeanAnnotation(BeanComponent.class);
    }

    private static final BeanInitialization INITIALIZATION = new BeanInitialization();

    static BeanInitialization getInstance() {
        return INITIALIZATION;
    }

    private static final Set<AnnotationRegister<?>> annotationRegisters = new HashSet<>();
    public <A extends AnnotationRegister<?>> void addAnnotationRegister(A register) {
        annotationRegisters.add(register);
        register.register();
    }
    public void registerAnnotations() {
        for (AnnotationRegister<?> annotationRegister : annotationRegisters) {
            annotationRegister.register();
        }
    }

    /*private static final Set<DebbieModuleStarter> debbieModuleStarters = new HashSet<>();
    public <A extends DebbieModuleStarter> void addDebbieModuleStarter(A moduleStarter) {
        debbieModuleStarters.add(moduleStarter);
    }
    public Set<DebbieModuleStarter> getDebbieModuleStarters() {
        return debbieModuleStarters;
    }*/

    public <D extends DataTransformer<?,?>> void registerDataTransformer(D dataTransformer, Type argsType1, Type argsType2) {
        Type[] types = new Type[2];
        types[0] = argsType1;
        types[1] = argsType2;
        DataTransformerFactory.register(dataTransformer, types);
    }
    public <D extends DataTransformer<?,?>> void registerDataTransformer(D transformer) {
        Type[] argsType = ReflectionHelper.getActualTypes(transformer.getClass());
        DataTransformerFactory.register(transformer, argsType);
    }
    public <O, T> T transform(final O origin, final Class<T> target) {
        return DataTransformerFactory.transform(origin, target);
    }

    public void init(Class<?> beanClass) {
        if (beanClass.isAnonymousClass()) {
            if ("".equals(beanClass.getSimpleName())) {
                Class<?>[] interfaces = beanClass.getInterfaces();
                if (interfaces.length == 1) {
                    BeanRegisterCenter.register(interfaces[0]);
                } else {
                    Class<?> superclass = beanClass.getSuperclass();
                    if (superclass != Object.class) {
                        BeanRegisterCenter.register(superclass);
                    }
                }
            }
        } else {
            BeanRegisterCenter.register(beanClass);
        }
    }

    public void initSingletonBean(DebbieBeanInfo<?> beanInfo) {
        beanInfo.setBeanType(BeanType.SINGLETON);
        BeanRegisterCenter.register(beanInfo);
    }

    public void refreshSingletonBean(DebbieBeanInfo<?> beanInfo) {
        beanInfo.setBeanType(BeanType.SINGLETON);
        BeanRegisterCenter.refresh(beanInfo);
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

    public <T extends Annotation> Set<DebbieBeanInfo<?>> getAnnotatedClass(Class<T> annotationClass) {
        return BeanRegisterCenter.getAnnotatedClass(annotationClass);
    }

    public <T extends Annotation> Set<DebbieBeanInfo<?>> getAnnotatedBeans() {
        return BeanRegisterCenter.getAnnotatedBeans();
    }

    public <T extends Annotation> Set<DebbieBeanInfo<?>> getAnnotatedMethodBean(Class<T> annotationClass) {
        return BeanRegisterCenter.getAnnotatedMethodsBean(annotationClass);
    }

    public Set<DebbieBeanInfo<?>> getBeanByInterface(Class<?> interfaceType) {
        return BeanRegisterCenter.getBeansByInterface(interfaceType);
    }

    public Set<DebbieBeanInfo<?>> getBeanByAbstractSuper(Class<?> abstractType) {
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

    public Set<DebbieBeanInfo<?>> getRegisteredBeans() {
        Set<DebbieBeanInfo<?>> result = new HashSet<>();
        Collection<DebbieBeanInfo<?>> registerRawBeans = BeanRegisterCenter.getRegisterRawBeans();
        if (!registerRawBeans.isEmpty()) {
            for (DebbieBeanInfo<?> registerRawBean : registerRawBeans) {
                if (registerRawBean.getBean() != null) {
                    result.add(registerRawBean);
                }
            }
        }
        return result;
    }

    public Set<DebbieBeanInfo<?>> getRegisteredRawBeans() {
        Set<DebbieBeanInfo<?>> result = new HashSet<>();
        Collection<DebbieBeanInfo<?>> registerRawBeans = BeanRegisterCenter.getRegisterRawBeans();
        if (!registerRawBeans.isEmpty()) {
            result.addAll(registerRawBeans);
        }
        return result;
    }

    public Set<Class<?>> getRegisteredRawBeanType() {
        Set<Class<?>> result = new HashSet<>();
        Collection<Class<?>> registerRawBeans = BeanRegisterCenter.getRegisterRawBeanTypes();
        if (!registerRawBeans.isEmpty()) {
            result.addAll(registerRawBeans);
        }
        return result;
    }

    public void reset() {
        BeanRegisterCenter.reset();
    }

}
