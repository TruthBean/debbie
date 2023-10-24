/**
 * Copyright (c) 2023 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *         http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.event;

import com.truthbean.LoggerFactory;
import com.truthbean.core.util.ReflectionUtils;
import com.truthbean.debbie.bean.*;
import com.truthbean.core.concurrent.NamedThreadFactory;
import com.truthbean.debbie.concurrent.ThreadPooledExecutor;
import com.truthbean.debbie.core.ApplicationContext;
import com.truthbean.debbie.proxy.BeanProxyType;
import com.truthbean.debbie.reflection.ClassInfo;
import com.truthbean.logger.LogLevel;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.Type;
import java.util.HashSet;
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
        if (debbieEventPublisher instanceof EventListenerBeanManager eventPublisher) {
            eventPublisher.addEventListener(eventType, listenerBeanFactory);
        }
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public EventListenerBeanManager register() {
        var beanInfoManager = applicationContext.getBeanInfoManager();
        var globalBeanFactory = applicationContext.getGlobalBeanFactory();
        final ThreadPooledExecutor executor = new ThreadPooledExecutor(Runtime.getRuntime().availableProcessors(), Runtime.getRuntime().availableProcessors() * 10,
                new NamedThreadFactory("DebbieEventListener")
                        .setUncaughtExceptionHandler((t, e) ->
                                LoggerFactory.getLogger(LogLevel.ERROR, t.getClass()).error("", e)), 5000L);
        DefaultEventListenerBeanManager eventPublisher = new DefaultEventListenerBeanManager(applicationContext, executor, globalBeanFactory);
        // 先注册系统相关的
        // SingletonBeanRegister register = new SingletonBeanRegister(applicationContext);
        // register.registerSingletonBean(eventPublisher, DebbieEventPublisher.class, "eventPublisher");
        // register.registerSingletonBean(eventPublisher, EventMulticaster.class, "eventMulticaster");
        var eventPublisherBeanFactory = new SimpleBeanFactory<>(eventPublisher, EventMulticaster.class, BeanProxyType.NO, "eventPublisher", "eventMulticaster", DebbieEventPublisher.class.getName(), EventMulticaster.class.getName());
        beanInfoManager.registerBeanInfo(eventPublisherBeanFactory);
        eventPublisher.addEventListener(new ApplicationExitEventListener());
        // 然后处理用户自定义的
        Set<BeanInfo> eventListenersBeans = beanInfoManager.getBeansByInterface(DebbieEventListener.class);
        if (eventListenersBeans != null) {
            for (BeanInfo<DebbieEventListener<? extends AbstractDebbieEvent>> listenersBean : eventListenersBeans) {
                Class<?> beanClass = listenersBean.getBeanClass();
                if (listenersBean instanceof BeanFactory<DebbieEventListener<? extends AbstractDebbieEvent>> beanFactory) {
                    DebbieEventListener beanValue = beanFactory.factoryBean(applicationContext);
                    if (beanValue != null && beanFactory.isCreated()) {
                        eventPublisher.addEventListener(beanValue.getEventType(), listenersBean);
                        continue;
                    }
                }

                Type[] types = ReflectionUtils.getActualTypes(beanClass);
                if (types == null || types.length != 1) {
                    throw new EventListenerRegisterException("EventListener(" + beanClass + ") has wrong event type");
                }
                eventPublisher.addEventListener((Class<? extends AbstractDebbieEvent>) types[0], listenersBean);
            }
        }
        Set<BeanInfo<?>> classInfoSet = beanInfoManager.getAnnotatedClass(EventBeanListener.class);
        for (BeanInfo debbieBeanInfo : classInfoSet) {
            Class<?> beanType = debbieBeanInfo.getBeanClass();
            if (DebbieEventListener.class.isAssignableFrom(beanType)) {
                if (debbieBeanInfo instanceof ClassInfo) {
                    List<Type> actualTypes = ((ClassInfo)debbieBeanInfo).getActualTypes();
                    if ((actualTypes == null || actualTypes.isEmpty())
                            && GenericStartedEventListener.class.isAssignableFrom(beanType)) {
                        eventPublisher.addEventListener(DebbieStartedEvent.class, debbieBeanInfo);
                    } else if (actualTypes != null && !actualTypes.isEmpty()) {
                        eventPublisher.addEventListener((Class<? extends AbstractDebbieEvent>) actualTypes.get(0), debbieBeanInfo);
                    }
                }
            }
        }
        // 处理 EventMethodListener
        Set<BeanInfo<?>> beanInfoList = beanInfoManager.getAnnotatedMethodsBean(EventMethodListener.class);
        if (beanInfoList != null && !beanInfoList.isEmpty()) {
            for (BeanInfo debbieBeanInfo : beanInfoList) {
                Class<?> beanType = debbieBeanInfo.getBeanClass();
                Object bean = globalBeanFactory.factory(beanType);
                final Set<Method> methods = new HashSet<>();
                if (debbieBeanInfo instanceof ClassInfo) {
                    methods.addAll(((ClassInfo<?>) debbieBeanInfo).getMethods());
                }
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

                                eventPublisher.addEventListener(listener);
                            }
                        }
                    }
                }
            }
        }
        return eventPublisher;
    }
}
