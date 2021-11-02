/**
 * Copyright (c) 2021 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 * http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.event;

import com.truthbean.Logger;
import com.truthbean.LoggerFactory;
import com.truthbean.debbie.bean.*;
import com.truthbean.debbie.concurrent.ConcurrentHashSet;
import com.truthbean.debbie.concurrent.ThreadPooledExecutor;
import com.truthbean.debbie.proxy.BeanProxyType;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Consumer;

/**
 * @author TruthBean/Rogar·Q
 * @since 0.2.0
 * Created on 2020-12-21 16:56
 */
public class DefaultEventManager implements DebbieEventPublisher, EventManager, BeanClosure {
    private final ConcurrentMap<Class<? extends AbstractDebbieEvent>, ConcurrentHashSet<EventListenerInfo>> eventListenerMap;

    private final GlobalBeanFactory beanFactory;
    private final ThreadPooledExecutor executor;

    public DefaultEventManager(final ThreadPooledExecutor threadPooledExecutor, final GlobalBeanFactory beanFactory) {
        this.executor = threadPooledExecutor;
        this.eventListenerMap = new ConcurrentHashMap<>();
        this.beanFactory = beanFactory;
    }

    @Override
    public void addEventListenerInfo(EventListenerInfo eventListenerInfo) {
        Class<? extends AbstractDebbieEvent> eventType = eventListenerInfo.getEventClass();
        var set = eventListenerMap.getOrDefault(eventType, new ConcurrentHashSet<>());
        eventListenerMap.put(eventType, set);
    }

    public void addEventListener(Class<? extends AbstractDebbieEvent> eventType,
                                 BeanFactory<? extends DebbieEventListener<? extends AbstractDebbieEvent>> listenerBeanFactory) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(() -> "eventType: " + eventType + " ; listenerType: " + listenerBeanFactory.getBeanType());
        }
        var set = eventListenerMap.getOrDefault(eventType, new ConcurrentHashSet<>());

        EventListenerInfo eventListenerInfo = new EventListenerInfo();
        eventListenerInfo.setEventClass(eventType);
        eventListenerInfo.setBeanFactory(listenerBeanFactory);
        eventListenerInfo.setEventListenerName(eventType.getName());
        set.add(eventListenerInfo);
        eventListenerMap.put(eventType, set);
    }

    @Override
    public void addEventListener(DebbieEventListener<? extends AbstractDebbieEvent> listener) {
        DebbieBeanFactory<DebbieEventListener<? extends AbstractDebbieEvent>> listenerBeanFactory = new DebbieBeanFactory<>();
        @SuppressWarnings("unchecked")
        var listenerType = (Class<DebbieEventListener<? extends AbstractDebbieEvent>>) listener.getClass();
        MutableBeanInfo<DebbieEventListener<? extends AbstractDebbieEvent>> beanInfo = new DebbieBeanInfo<>(listenerType);
        beanInfo.setBeanType(BeanType.SINGLETON);
        beanInfo.setBeanProxyType(BeanProxyType.ASM);
        beanInfo.setBean(listener);
        listenerBeanFactory.setBeanInfo(beanInfo);
        var eventType = listener.getEventType();
        var set = eventListenerMap.getOrDefault(eventType, new ConcurrentHashSet<>());

        EventListenerInfo eventListenerInfo = new EventListenerInfo();
        eventListenerInfo.setEventClass(eventType);
        eventListenerInfo.setBeanFactory(listenerBeanFactory);
        eventListenerInfo.setEventListenerName(eventType.getName());
        eventListenerInfo.setEventListener(listener);
        set.add(eventListenerInfo);
        eventListenerMap.put(eventType, set);
    }

    @Override
    public void addEventListenerBean(String listenerBeanName) {
        Object eventListener = beanFactory.factory(listenerBeanName);
        if (!(eventListener instanceof DebbieEventListener)) {
            throw new EventListenerRegisterException("bean(" + listenerBeanName + ") is not DebbieEventListener ");
        }
        addEventListener((DebbieEventListener<? extends AbstractDebbieEvent>) eventListener);
    }

    @Override
    public void removeEventListener(DebbieEventListener<? extends AbstractDebbieEvent> listener) {
        Collection<ConcurrentHashSet<EventListenerInfo>> values = eventListenerMap.values();
        for (ConcurrentHashSet<EventListenerInfo> value : values) {
            ConcurrentHashSet<EventListenerInfo> copy = new ConcurrentHashSet<>();
            copy.addAll(value);
            for (EventListenerInfo info : copy) {
                if (info.getEventListener().equals(listener)) {
                    value.remove(info);
                }
            }
        }
    }

    @Override
    public void removeEventListener(BeanFactory<? extends DebbieEventListener<? extends AbstractDebbieEvent>> listenerBeanFactory) {
        Collection<ConcurrentHashSet<EventListenerInfo>> values = eventListenerMap.values();
        for (ConcurrentHashSet<EventListenerInfo> value : values) {
            ConcurrentHashSet<EventListenerInfo> copy = new ConcurrentHashSet<>();
            copy.addAll(value);
            for (EventListenerInfo info : copy) {
                if (info.getBeanFactory().equals(listenerBeanFactory)) {
                    value.remove(info);
                }
            }
        }
    }

    @Override
    public void removeEventListener(String listenerBeanName) {
        Collection<ConcurrentHashSet<EventListenerInfo>> values = eventListenerMap.values();
        for (ConcurrentHashSet<EventListenerInfo> value : values) {
            ConcurrentHashSet<EventListenerInfo> copy = new ConcurrentHashSet<>();
            copy.addAll(value);
            for (EventListenerInfo info : copy) {
                if (info.getEventListenerName().equals(listenerBeanName)) {
                    value.remove(info);
                }
            }
        }
    }

    @Override
    public void removeAllListeners(Class<? extends AbstractDebbieEvent> eventClass) {
        eventListenerMap.remove(eventClass);
    }

    @Override
    public void removeAllListeners() {
        eventListenerMap.clear();
    }

    @Override
    public <E extends AbstractDebbieEvent> void multicastEvent(E event) {
        publishEvent(event);
    }

    @SuppressWarnings({"unchecked"})
    @Override
    public <E extends AbstractDebbieEvent> void publishEvent(E event) {
        long start = System.currentTimeMillis();
        consumeEventListener(event.getClass(), (eventListener) -> {
            if (!eventListener.async()) {
                try {
                    eventListener.onEvent(event);
                } catch (Throwable e) {
                    Throwable cause = e.getCause();
                    if (cause == null)
                        cause = e;
                    LOGGER.error("listener handler event(" + event + ") error. ", cause);
                }
            } else if (eventListener.allowConcurrent()) {
                synchronized (this) {
                    try {
                        eventListener.onEvent(event);
                    } catch (Exception e) {
                        Throwable cause = e.getCause();
                        if (cause == null)
                            cause = e;
                        LOGGER.error("listener handler event(" + event + ") error. ", cause);
                    }
                }
            } else {
                try {
                    executor.execute(() -> {
                        try {
                            eventListener.onEvent(event);
                        } catch (Exception ex) {
                            Throwable cause = ex.getCause();
                            if (cause == null)
                                cause = ex;
                            LOGGER.error("listener handler event(" + event + ") error. ", cause);
                        }
                    });
                } catch (Exception e) {
                    Throwable cause = e.getCause();
                    if (cause == null)
                        cause = e;
                    LOGGER.error("listener handler event(" + event + ") error. ", cause);
                }
            }
        });
        long end = System.currentTimeMillis();
        LOGGER.debug(() -> "publishEvent spend time: " + (end - start) + "ms");
    }

    public List<EventListenerInfo> getEventListenerFactory(Class<?> eventClass) {
        return new ArrayList<>(eventListenerMap.getOrDefault(eventClass, new ConcurrentHashSet<>()));
    }

    public List<DebbieEventListener<?>> getEventListener(Class<? extends AbstractDebbieEvent> eventClass) {
        var hashSet = eventListenerMap.getOrDefault(eventClass, new ConcurrentHashSet<>());

        ConcurrentHashSet<DebbieEventListener<? extends AbstractDebbieEvent>> value = new ConcurrentHashSet<>();
        hashSet.forEach(info -> {
            if (info.getEventListener() != null) {
                value.add(info.getEventListener());
            } else if (info.getBeanFactory() != null && !info.getBeanFactory().isSkipCreatedBeanFactory()) {
                DebbieEventListener<? extends AbstractDebbieEvent> bean = info.getBeanFactory().factoryBean();
                info.setEventListener(bean);
                value.add(bean);
            }
        });
        return new ArrayList<>(value);
    }

    @SuppressWarnings({"rawtypes"})
    public void consumeEventListener(Class<? extends AbstractDebbieEvent> eventClass,
                                     Consumer<DebbieEventListener> consumer) {
        var hashSet = eventListenerMap.getOrDefault(eventClass, new ConcurrentHashSet<>());

        hashSet.forEach(info -> {
            if (info.getEventListener() != null) {
                DebbieEventListener<? extends AbstractDebbieEvent> eventListener = info.getEventListener();
                consumer.accept(eventListener);
            } else if (info.getBeanFactory() != null && !info.getBeanFactory().isSkipCreatedBeanFactory()) {
                DebbieEventListener<? extends AbstractDebbieEvent> bean = info.getBeanFactory().factoryBean();
                info.setEventListener(bean);
                consumer.accept(bean);
            }
        });
    }

    @Override
    public void destroy() {
        eventListenerMap.clear();
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultEventPublisher.class);
}
