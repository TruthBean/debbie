package com.truthbean.debbie.bean;

import com.truthbean.debbie.reflection.ClassInfo;
import com.truthbean.debbie.reflection.ReflectionHelper;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Set;

/**
 * @author TruthBean
 * @since 0.0.2
 */
public class BeanConfigurationRegister {

    public void register(Class<?>... classes) {
        if (classes != null) {
            for (Class<?> clazz : classes) {
                register(clazz);
            }
        }
    }

    public void register(Set<Class<?>> classes) {
        if (classes != null) {
            for (Class<?> clazz : classes) {
                register(clazz);
            }
        }
    }

    public void init(String... packageName) {
        if (packageName != null) {
            for (String s : packageName) {
                register(s);
            }
        }
    }

    public void register(String packageName) {
        var allClass = ReflectionHelper.getAllClassByPackageName(packageName);
        if (!allClass.isEmpty()) {
            allClass.forEach(this::register);
        }
    }

    public <C> void register(Class<C> beanConfigurationClass) {
        if (beanConfigurationClass.getAnnotation(BeanConfiguration.class) != null) {
            ClassInfo<C> classInfo = new ClassInfo<>(beanConfigurationClass);
            C configuration = ReflectionHelper.newInstance(beanConfigurationClass);
            register(configuration, classInfo);
        }
    }

    public <Configuration> void register(Configuration beanConfiguration) {
        Class<Configuration> beanConfigurationClass = (Class<Configuration>) beanConfiguration.getClass();
        ClassInfo<Configuration> classInfo = new ClassInfo<>(beanConfigurationClass);
        register(beanConfiguration, classInfo);
    }

    private <Configuration> void register(Configuration configuration, ClassInfo<Configuration> classInfo) {
        List<Method> annotationMethod = classInfo.getAnnotationMethod(DebbieBean.class);
        if (!annotationMethod.isEmpty()) {
            for (Method method : annotationMethod) {
                DebbieBeanInfo beanInfo = new DebbieBeanInfo<>(method.getReturnType());
                DebbieBean debbieBean = method.getAnnotation(DebbieBean.class);
                String name = debbieBean.name();
                if (name.isBlank()) {
                    name = method.getName();
                }
                beanInfo.setBeanName(name);
                beanInfo.setBeanType(BeanType.SINGLETON);
                beanInfo.setBean(ReflectionHelper.invokeMethod(configuration, method));
                BeanRegisterCenter.register(beanInfo);
            }
        }
    }
}
