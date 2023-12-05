package com.truthbean.debbie.boot;

import com.truthbean.debbie.bean.*;
import com.truthbean.debbie.core.ApplicationContext;

import java.util.List;

/**
 * @author TruthBean/Rogar·Q
 * @since 0.5.5
 */
public interface ApplicationBootContext extends ApplicationContext {

    <T> void registerBean(BeanFactory<T> beanFactory);

    <T extends I, I> void registerSingleBean(Class<I> beanClass, T bean, String... names);

    // <E extends AbstractDebbieEvent, EL extends DebbieEventListener<E>> void registerEventListener(Class<E> eventClass, EL listener);

    void registerBeanLifecycle(BeanLifecycle beanLifecycle);

    // void refreshBeans();

    <O, T> T transform(final O origin, final Class<T> target);

    <T> T factory(String beanName);

    <T> T factory(Class<T> beanType);

    <T> List<T> factories(Class<T> beanType);

    <T> T factoryConfiguration(String profile, String category, Class<T> beanType);

    <T> T factory(BeanInjection<T> injection);
}
