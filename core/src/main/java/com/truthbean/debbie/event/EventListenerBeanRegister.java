package com.truthbean.debbie.event;

import com.truthbean.debbie.bean.BeanFactoryHandler;
import com.truthbean.debbie.bean.BeanInitialization;
import com.truthbean.debbie.bean.DebbieBeanInfo;
import com.truthbean.debbie.bean.SingletonBeanRegister;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Set;

public class EventListenerBeanRegister {

    private final BeanFactoryHandler beanFactoryHandler;

    public EventListenerBeanRegister(BeanFactoryHandler beanFactoryHandler) {
        this.beanFactoryHandler = beanFactoryHandler;
    }

    public void register() {
        BeanInitialization beanInitialization = beanFactoryHandler.getBeanInitialization();
        DefaultEventPublisher eventPublisher = new DefaultEventPublisher();
        Set<DebbieBeanInfo> classInfoSet = beanInitialization.getAnnotatedClass(EventBeanListener.class);
        for (DebbieBeanInfo debbieBeanInfo : classInfoSet) {
            Class<?> beanType = debbieBeanInfo.getBeanClass();
            if (DebbieEventListener.class.isAssignableFrom(beanType)) {
                Object bean = beanFactoryHandler.factory(beanType);
                debbieBeanInfo.setBean(bean);
                List<Type> actualTypes = debbieBeanInfo.getActualTypes();
                eventPublisher.addEventListener((Class<? extends DebbieEvent>) actualTypes.get(0), (DebbieEventListener<? extends DebbieEvent>) bean);
            }
        }
        SingletonBeanRegister register = new SingletonBeanRegister(beanFactoryHandler);
        register.registerSingletonBean(eventPublisher, DebbieEventPublisher.class, "eventPublisher");
    }
}
