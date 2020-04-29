package com.truthbean.debbie.event;

import com.truthbean.debbie.bean.BeanFactoryHandler;
import com.truthbean.debbie.bean.BeanInitialization;
import com.truthbean.debbie.bean.DebbieBeanInfo;
import com.truthbean.debbie.bean.SingletonBeanRegister;
import com.truthbean.debbie.task.ThreadPooledExecutor;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Set;

/**
 * @author TruthBean
 * @since 0.0.2
 */
public class EventListenerBeanRegister {

    private final BeanFactoryHandler beanFactoryHandler;

    public EventListenerBeanRegister(BeanFactoryHandler beanFactoryHandler) {
        this.beanFactoryHandler = beanFactoryHandler;
    }

    @SuppressWarnings("unchecked")
    public void register() {
        BeanInitialization beanInitialization = beanFactoryHandler.getBeanInitialization();
        ThreadPooledExecutor executor = beanFactoryHandler.factory("threadPooledExecutor");
        DefaultEventPublisher eventPublisher = new DefaultEventPublisher(executor);
        // 先注册系统相关的
        SingletonBeanRegister register = new SingletonBeanRegister(beanFactoryHandler);
        register.registerSingletonBean(eventPublisher, DebbieEventPublisher.class, "eventPublisher");
        // 然后处理用户自定义的
        Set<DebbieBeanInfo<?>> classInfoSet = beanInitialization.getAnnotatedClass(EventBeanListener.class);
        for (DebbieBeanInfo debbieBeanInfo : classInfoSet) {
            Class<?> beanType = debbieBeanInfo.getBeanClass();
            if (DebbieEventListener.class.isAssignableFrom(beanType)) {
                Object bean = beanFactoryHandler.factory(beanType);
                debbieBeanInfo.setBean(bean);
                List<Type> actualTypes = debbieBeanInfo.getActualTypes();
                eventPublisher.addEventListener((Class<? extends AbstractDebbieEvent>) actualTypes.get(0), (DebbieEventListener<? extends AbstractDebbieEvent>) bean);
            }
        }
        // 处理 EventMethodListener
        Set<DebbieBeanInfo<?>> beanInfoList = beanInitialization.getAnnotatedMethodBean(EventMethodListener.class);
        if (beanInfoList != null && beanInfoList.isEmpty()) {
            for (DebbieBeanInfo debbieBeanInfo : beanInfoList) {
                Class<?> beanType = debbieBeanInfo.getBeanClass();
                Object bean = beanFactoryHandler.factory(beanType);
                Set<Method> methods = debbieBeanInfo.getMethods();
                for (Method method : methods) {
                    EventMethodListener annotation = method.getAnnotation(EventMethodListener.class);
                    if (annotation != null) {
                        int count = method.getParameterCount();
                        if (count == 1) {
                            Parameter parameter = method.getParameters()[0];
                            Class<?> type = parameter.getType();
                            if (AbstractDebbieEvent.class.isAssignableFrom(type)) {
                                var listener = new EventMethodListenerFactory(bean, type, method);
                                listener.setAsync(annotation.async());
                                debbieBeanInfo.setBean(listener);
                                eventPublisher.addEventListener((Class<? extends AbstractDebbieEvent>) type, (DebbieEventListener<? extends AbstractDebbieEvent>) listener);
                            }
                        }
                    }
                }
            }
        }
    }
}
