/*
package com.truthbean.debbie.controversial;

import com.truthbean.debbie.bean.Aware;
import com.truthbean.debbie.bean.ConstructPost;
import com.truthbean.debbie.core.ApplicationContext;

import java.util.Map;
import java.util.function.Supplier;

*/
/**
 * @author TruthBean
 * @since 0.5.3
 * Created on 2021/11/26 23:48.
 *//*

public class SimpleBeanCreator<Bean> implements ReflectionBeanCreator<Bean> {

    private final FactoryBeanInfo<Bean> factoryBeanInfo;
    private final InjectedBeanFactory beanFactory;

    public SimpleBeanCreator(FactoryBeanInfo<Bean> factoryBeanInfo, InjectedBeanFactory beanFactory) {
        this.factoryBeanInfo = factoryBeanInfo;
        this.beanFactory = beanFactory;
    }

    @Override
    public Bean create() {
        return null;
    }

    @Override
    public FactoryBeanInfo<Bean> getBeanInfo() {
        return null;
    }

    @Override
    public void createPreparation(Map<FactoryBeanInfo<?>, ReflectionBeanCreator<?>> singletonBeanCreatorMap, Object firstParamValue) {

    }

    @Override
    public void createPreparationByDependence(ApplicationContext applicationContext) {

    }

    @Override
    public void postConstructor() {
        Bean bean = factoryBeanInfo.getBean();
        if (bean instanceof ConstructPost) {
            ((ConstructPost) bean).postConstruct();
        }
    }

    @Override
    public void postPreparation(Map<FactoryBeanInfo<?>, ReflectionBeanCreator<?>> singletonBeanCreatorMap) {
        Class<Bean> clazz = factoryBeanInfo.getBeanClass();
        if (Aware.class.isAssignableFrom(clazz)) {
            // injectedBeanFactory.resolveAwareValue(bean, clazz);
        }
    }

    @Override
    public void postCreated() {

    }

    @Override
    public boolean isCreated() {
        return false;
    }

    @Override
    public void create(Bean bean) {

    }

    @Override
    public void create(Supplier<Bean> bean) {

    }

    @Override
    public Bean getCreatedBean() {
        return null;
    }
}
*/
