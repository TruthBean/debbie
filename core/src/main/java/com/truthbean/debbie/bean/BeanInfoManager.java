package com.truthbean.debbie.bean;

import com.truthbean.debbie.core.ApplicationContext;
import com.truthbean.debbie.io.ResourceResolver;
import com.truthbean.debbie.reflection.ReflectionHelper;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * @author TruthBean
 * @since 0.5.3
 * Created on 2021/11/26 19:09.
 */
public interface BeanInfoManager extends BeanAnnotationManager {

    default void register(Class<? extends Annotation> classAnnotation,
                               String packageName, ClassLoader classLoader,
                               ResourceResolver resourceResolver) {
        var allClass = ReflectionHelper.getAllClassByPackageName(packageName, classLoader, resourceResolver);
        if (!allClass.isEmpty()) {
            allClass.forEach(c -> {
                if (c.isAnnotationPresent(classAnnotation)) {
                    register(c);
                }
            });
        }
    }

    default void register(Class<? extends Annotation> classAnnotation,
                          List<String> packageNames, ClassLoader classLoader,
                          ResourceResolver resourceResolver) {
        packageNames.forEach(packageName -> register(classAnnotation, packageName, classLoader, resourceResolver));
    }

    default void register(String packageName, ClassLoader classLoader, ResourceResolver resourceResolver) {
        var allClass = ReflectionHelper.getAllClassByPackageName(packageName, classLoader, resourceResolver);
        if (!allClass.isEmpty()) {
            allClass.forEach(this::register);
        }
    }

    default void register(List<String> packageNames, ClassLoader classLoader, ResourceResolver resourceResolver) {
        packageNames.forEach(ele -> this.register(ele, classLoader, resourceResolver));
    }

    default void register(ClassLoader classLoader, ResourceResolver resourceResolver, String... packageName) {
        if (packageName != null) {
            for (String s : packageName) {
                this.register(s, classLoader, resourceResolver);
            }
        }
    }

    default void register(final Set<Class<?>> beanClasses) {
        if (beanClasses != null && !beanClasses.isEmpty()) {
            beanClasses.forEach(this::register);
        }
    }

    void registerBeanRegister(BeanRegister beanRegister);

    void registerClass(Class<?> beanClass);

    <Bean> void register(final Class<Bean> clazz);

    boolean registerBeanInfo(BeanInfo<?> beanInfo);

    void registerBeanLifecycle(BeanLifecycle beanLifecycle);

    Set<BeanLifecycle> getBeanLifecycles();

    <Bean> void refresh(BeanInfo<Bean> beanInfo);

    // void refreshBeans();

    void autoCreateSingletonBeans(ApplicationContext applicationContext);

    Set<BeanInfo> getLazyCreateBean();

    @Override
    boolean support(Class<?> beanClass);

    <T extends Annotation> Set<BeanInfo<?>> getAnnotatedClass(Class<T> annotationClass);

    Set<BeanInfo> getAllBeanInfo();

    <Bean> List<BeanInfo<? extends Bean>> getBeanInfoList(Class<Bean> type, boolean require);

    <Bean> BeanInfo<Bean> getBeanInfo(String serviceName, Class<Bean> type, boolean require);

    <Bean> BeanFactory<Bean> getBeanFactory(String serviceName, Class<Bean> type, boolean require);

    <T> BeanInfo<T> getBeanInfo(String serviceName, Class<T> type, boolean require, boolean throwException);

    <T> BeanFactory<T> getBeanFactory(String serviceName, Class<T> type, boolean require, boolean throwException);

    Set<BeanInfo<?>> getAnnotatedBeans();

    Set<BeanInfo<?>> getAnnotatedMethodsBean(Class<? extends Annotation> methodAnnotation);

    <T> Set<BeanInfo> getBeansByInterface(Class interfaceType);

    Set<BeanInfo<?>> getBeanByAbstractSuper(Class<?> abstractType);

    // <Bean> BeanInfo<Bean> getRegisterRawBean(Class<Bean> bean);

    // Set<Class<?>> getRegisteredRawBeanType();

    Collection<BeanInfo> getRegisteredBeans();

    Set<Method> getBeanMethods(Class<?> beanClass);

    /*default <Bean> Bean getRegisterBean(Class<Bean> bean, ApplicationContext applicationContext) {
        BeanInfo<Bean> registerRawBean = this.getRegisterRawBean(bean);
        if (registerRawBean == null) {
            return null;
        }
        if (registerRawBean instanceof MutableFactoryBeanInfo<Bean> beanInfo) {
            Bean resultBean = beanInfo.getBean();
            if (resultBean != null) {
                return resultBean;
            }
            BeanFactory<Bean> beanFactory = beanInfo.getBeanFactory();
            if (beanFactory != null) {
                return beanFactory.factoryBean(applicationContext);
            } else {
                throw new NoBeanException("bean " + bean.getName() + " has no registered! ");
            }
        } else if (registerRawBean instanceof BeanFactory) {
            return ((BeanFactory<Bean>) registerRawBean).factoryBean(applicationContext);
        }
        return null;
    }*/

    // <T> SimpleMutableBeanFactory<T, T> getBeanInfoWithBean(Class<T> type, GlobalBeanFactory globalBeanFactory);

    // Set<BeanInfo<?>> getRegisteredRawBeans();

    boolean isBeanRegistered(Class<?> beanClass);

    <T> boolean containsBean(Class<T> beanType);

    boolean containsBean(String beanName);

    void printGraalvmConfig(ApplicationContext context);

    void destroy(BeanInfo<?> beanInfo);

    void reset(ApplicationContext applicationContext);
}
