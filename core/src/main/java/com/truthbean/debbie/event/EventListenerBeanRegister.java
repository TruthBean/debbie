/**
 * Copyright (c) 2020 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *         http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.event;

import com.truthbean.debbie.bean.*;
import com.truthbean.debbie.concurrent.ThreadPooledExecutor;
import com.truthbean.debbie.core.ApplicationContext;

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

    private final ApplicationContext applicationContext;

    public EventListenerBeanRegister(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    public <E extends AbstractDebbieEvent, L extends DebbieEventListener<E>> void register(final Class<E> eventType,
                         final BeanFactory<L> listenerBeanFactory) {
        var globalBeanFactory = applicationContext.getGlobalBeanFactory();

        DebbieEventPublisher debbieEventPublisher = globalBeanFactory.factory("eventPublisher");
        if (debbieEventPublisher instanceof DefaultEventPublisher) {
            var eventPublisher = (DefaultEventPublisher) debbieEventPublisher;
            listenerBeanFactory.setGlobalBeanFactory(globalBeanFactory);
            eventPublisher.addEventListener(eventType, listenerBeanFactory);
        }
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public void register() {
        var beanInitialization = applicationContext.getBeanInitialization();
        var globalBeanFactory = applicationContext.getGlobalBeanFactory();
        ThreadPooledExecutor executor = globalBeanFactory.factory("threadPooledExecutor");
        DefaultEventPublisher eventPublisher = new DefaultEventPublisher(executor);
        // 先注册系统相关的
        SingletonBeanRegister register = new SingletonBeanRegister(applicationContext);
        register.registerSingletonBean(eventPublisher, DebbieEventPublisher.class, "eventPublisher");
        register.registerSingletonBean(eventPublisher, EventMulticaster.class, "eventMulticaster");
        eventPublisher.addEventListener(new ApplicationExitEventListener());
        // 然后处理用户自定义的
        Set<DebbieClassBeanInfo<?>> classInfoSet = beanInitialization.getAnnotatedClass(EventBeanListener.class);
        for (DebbieClassBeanInfo debbieBeanInfo : classInfoSet) {
            Class<?> beanType = debbieBeanInfo.getBeanClass();
            if (DebbieEventListener.class.isAssignableFrom(beanType)) {
                List<Type> actualTypes = debbieBeanInfo.getActualTypes();
                if ((actualTypes == null || actualTypes.isEmpty())
                        && GenericStartedEventListener.class.isAssignableFrom(beanType)) {
                    BeanFactory<? extends GenericStartedEventListener<? extends DebbieStartedEvent>> listenerBeanFactory = new DebbieBeanFactory<>(debbieBeanInfo);
                    listenerBeanFactory.setGlobalBeanFactory(globalBeanFactory);
                    eventPublisher.addEventListener(DebbieStartedEvent.class, listenerBeanFactory);
                } else if (actualTypes != null && !actualTypes.isEmpty()) {
                    BeanFactory<DebbieEventListener<? extends AbstractDebbieEvent>> listenerBeanFactory = new DebbieBeanFactory<>(debbieBeanInfo);
                    listenerBeanFactory.setGlobalBeanFactory(globalBeanFactory);
                    eventPublisher.addEventListener((Class<? extends AbstractDebbieEvent>) actualTypes.get(0), listenerBeanFactory);
                }
            }
        }
        // 处理 EventMethodListener
        Set<DebbieClassBeanInfo<?>> beanInfoList = beanInitialization.getAnnotatedMethodBean(EventMethodListener.class);
        if (beanInfoList != null && !beanInfoList.isEmpty()) {
            for (DebbieClassBeanInfo debbieBeanInfo : beanInfoList) {
                Class<?> beanType = debbieBeanInfo.getBeanClass();
                Object bean = globalBeanFactory.factory(beanType);
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
                                listener.setAllowConcurrent(annotation.allowConcurrent());

                                DebbieBeanInfo listenerBeanInfo = new DebbieBeanInfo<>(EventMethodListenerFactory.class);
                                listenerBeanInfo.setBean(listener);

                                DebbieBeanFactory<? extends DebbieEventListener<? extends AbstractDebbieEvent>> listenerBeanFactory = new DebbieBeanFactory<>();
                                listenerBeanFactory.setGlobalBeanFactory(globalBeanFactory);
                                listenerBeanFactory.setBeanInfo(listenerBeanInfo);

                                eventPublisher.addEventListener((Class<? extends AbstractDebbieEvent>) type, listenerBeanFactory);
                            }
                        }
                    }
                }
            }
        }
    }
}
