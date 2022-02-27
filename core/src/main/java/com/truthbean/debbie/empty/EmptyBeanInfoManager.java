package com.truthbean.debbie.empty;

import com.truthbean.debbie.bean.*;
import com.truthbean.debbie.core.ApplicationContext;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author TruthBean
 * @since 0.5.3
 * Created on 2021/11/26 19:37.
 */
public class EmptyBeanInfoManager implements BeanInfoManager {

    @Override
    public <A extends Annotation> void registerInjectType(Class<A> injectType) {

    }

    @Override
    public void registerInjectType(Set<Class<? extends Annotation>> injectTypes) {

    }

    @Override
    public <A extends Annotation> void registerBeanAnnotation(Class<A> annotationType, BeanComponentParser parser) {

    }

    @Override
    public Set<Class<? extends Annotation>> getBeanAnnotations() {
        return null;
    }

    @Override
    public Set<Class<? extends Annotation>> getClassAnnotation() {
        return null;
    }

    @Override
    public Set<Class<? extends Annotation>> getMethodAnnotation() {
        return null;
    }

    @Override
    public Set<Class<? extends Annotation>> getInjectTypes() {
        return null;
    }

    @Override
    public Class<? extends Annotation> getInjectType() {
        return null;
    }

    @Override
    public boolean hasInjectType(AnnotatedElement annotatedElement, boolean another) {
        return another;
    }

    @Override
    public boolean injectedRequired(Annotation annotation, boolean another) {
        return another;
    }

    @Override
    public Boolean injectBeanRequiredIfPresent(AnnotatedElement annotatedElement, boolean another) {
        return another;
    }

    @Override
    public void registerBeanRegister(BeanRegister beanRegister) {

    }

    @Override
    public boolean containInjectType(Class<? extends Annotation> annotation) {
        return false;
    }

    @Override
    public void addIgnoreInterface(Class<?> ignoreInterface) {

    }

    @Override
    public Set<Class<?>> getIgnoreInterface() {
        return new HashSet<>();
    }

    @Override
    public void addIgnoreAnnotation(Class<? extends Annotation> annotation) {

    }

    @Override
    public Set<Class<? extends Annotation>> getIgnoredAnnotations() {
        return new HashSet<>();
    }

    @Override
    public void registerClass(Class<?> beanClass) {

    }

    @Override
    public <Bean> void register(Class<Bean> clazz) {

    }

    @Override
    public boolean registerBeanInfo(BeanInfo<?> beanInfo) {
        return false;
    }

    @Override
    public void registerBeanLifecycle(BeanLifecycle beanLifecycle) {

    }

    @Override
    public Set<BeanLifecycle> getBeanLifecycles() {
        return null;
    }

    @Override
    public <Bean> void refresh(BeanInfo<Bean> beanInfo) {

    }

    /*@Override
    public void refreshBeans() {

    }*/

    @Override
    public void autoCreateSingletonBeans(ApplicationContext applicationContext) {

    }

    @Override
    public Set<BeanInfo> getLazyCreateBean() {
        return null;
    }

    @Override
    public boolean support(Class<?> beanClass) {
        return false;
    }

    @Override
    public <T extends Annotation> Set<BeanInfo<?>> getAnnotatedClass(Class<T> annotationClass) {
        return null;
    }

    @Override
    public Set<BeanInfo> getAllBeanInfo() {
        return null;
    }

    @Override
    public <Bean> List<BeanInfo<? extends Bean>> getBeanInfoList(Class<Bean> type, boolean require) {
        return null;
    }

    @Override
    public <Bean> BeanInfo<Bean> getBeanInfo(String serviceName, Class<Bean> type, boolean require) {
        return null;
    }

    @Override
    public <Bean> BeanFactory<Bean> getBeanFactory(String serviceName, Class<Bean> type, boolean require) {
        return null;
    }

    @Override
    public <T> BeanInfo<T> getBeanInfo(String serviceName, Class<T> type, boolean require, boolean throwException) {
        return null;
    }

    @Override
    public <T> BeanFactory<T> getBeanFactory(String serviceName, Class<T> type, boolean require, boolean throwException) {
        return null;
    }

    @Override
    public Set<BeanInfo<?>> getAnnotatedBeans() {
        return null;
    }

    @Override
    public Set<BeanInfo<?>> getAnnotatedMethodsBean(Class<? extends Annotation> methodAnnotation) {
        return null;
    }

    @Override
    public Set<BeanInfo> getBeansByInterface(Class interfaceType) {
        return null;
    }

    @Override
    public Set<BeanInfo<?>> getBeanByAbstractSuper(Class<?> abstractType) {
        return null;
    }

    @Override
    public Collection<BeanInfo> getRegisteredBeans() {
        return null;
    }

    @Override
    public Set<Method> getBeanMethods(Class<?> beanClass) {
        return null;
    }

    @Override
    public boolean isBeanRegistered(Class<?> beanClass) {
        return false;
    }

    @Override
    public <T> boolean containsBean(Class<T> beanType) {
        return false;
    }

    @Override
    public boolean containsBean(String beanName) {
        return false;
    }

    @Override
    public void printGraalvmConfig(ApplicationContext context) {
    }

    @Override
    public void destroy(BeanInfo<?> beanInfo) {

    }

    @Override
    public void reset(ApplicationContext applicationContext) {

    }
}
