/*
  Copyright (c) 2023 TruthBean(Rogar·Q)
  Debbie is licensed under Mulan PSL v2.
  You can use this software according to the terms and conditions of the Mulan PSL v2.
  You may obtain a copy of Mulan PSL v2 at:
  http://license.coscl.org.cn/MulanPSL2
  THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
  See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.event;

import com.truthbean.Logger;
import com.truthbean.LoggerFactory;
import com.truthbean.debbie.bean.*;
import com.truthbean.debbie.concurrent.ConcurrentHashSet;
import com.truthbean.debbie.concurrent.ThreadPooledExecutor;
import com.truthbean.debbie.core.ApplicationContext;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.UndeclaredThrowableException;
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
public class DefaultEventListenerBeanManager implements EventListenerBeanManager {
    private final ConcurrentMap<Class<? extends AbstractDebbieEvent>, ConcurrentHashSet<EventListenerInfo>> eventListenerMap;

    private final ApplicationContext applicationContext;
    private final GlobalBeanFactory beanFactory;
    private final ThreadPooledExecutor executor;

    public DefaultEventListenerBeanManager(final ApplicationContext applicationContext,
                                           final ThreadPooledExecutor threadPooledExecutor, final GlobalBeanFactory beanFactory) {
        this.applicationContext = applicationContext;
        this.executor = threadPooledExecutor;
        this.eventListenerMap = new ConcurrentHashMap<>();
        this.beanFactory = beanFactory;
    }

    @Override
    public <E extends AbstractDebbieEvent, EL extends DebbieEventListener<E>> void addEventListener(Class<E> eventClass, EL listener) {
        var set = eventListenerMap.getOrDefault(eventClass, new ConcurrentHashSet<>());

        EventListenerInfo eventListenerInfo = new EventListenerInfo();
        eventListenerInfo.setEventClass(eventClass);
        eventListenerInfo.setBeanInfo(null);
        eventListenerInfo.setEventListener(listener);
        eventListenerInfo.setEventListenerName(listener.getName());
        set.add(eventListenerInfo);
        eventListenerMap.put(eventClass, set);
    }

    @Override
    public void addEventListenerInfo(EventListenerInfo eventListenerInfo) {
        Class<? extends AbstractDebbieEvent> eventType = eventListenerInfo.getEventClass();
        var set = eventListenerMap.getOrDefault(eventType, new ConcurrentHashSet<>());
        eventListenerMap.put(eventType, set);
    }

    @Override
    public <EL extends DebbieEventListener<E>, E extends AbstractDebbieEvent>
    void addEventListener(Class<E> eventType, BeanFactory<EL> listenerBeanFactory) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(() -> "eventType: " + eventType + " ; listenerType: " + listenerBeanFactory.getBeanType());
        }
        var set = eventListenerMap.getOrDefault(eventType, new ConcurrentHashSet<>());

        EventListenerInfo eventListenerInfo = new EventListenerInfo();
        eventListenerInfo.setEventClass(eventType);
        eventListenerInfo.setBeanInfo(listenerBeanFactory);
        eventListenerInfo.setEventListenerName(eventType.getName());
        set.add(eventListenerInfo);
        eventListenerMap.put(eventType, set);
    }

    @Override
    public void addEventListener(DebbieEventListener<? extends AbstractDebbieEvent> listener) {
        var eventType = listener.getEventType();
        var set = eventListenerMap.getOrDefault(eventType, new ConcurrentHashSet<>());

        EventListenerInfo eventListenerInfo = new EventListenerInfo();
        eventListenerInfo.setEventClass(eventType);
        eventListenerInfo.setEventListenerName(eventType.getName());
        eventListenerInfo.setEventListener(listener);
        set.add(eventListenerInfo);
        eventListenerMap.put(eventType, set);
    }

    @Override
    public void addEventListener(Class<? extends AbstractDebbieEvent> eventType,
                                 BeanInfo<? extends DebbieEventListener<? extends AbstractDebbieEvent>> eventListenerBeanInfo) {
        var set = eventListenerMap.getOrDefault(eventType, new ConcurrentHashSet<>());

        EventListenerInfo eventListenerInfo = new EventListenerInfo();
        eventListenerInfo.setEventClass(eventType);
        eventListenerInfo.setEventListenerName(eventType.getName());
        eventListenerInfo.setBeanInfo(eventListenerBeanInfo);
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
                BeanInfo<? extends DebbieEventListener<? extends AbstractDebbieEvent>> beanInfo = info.getBeanInfo();
                if (beanInfo instanceof BeanFactory && listenerBeanFactory.equals(beanInfo)) {
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
        multicastEvent(event, event.getType());
    }

    @Override
    public <E extends I, I extends AbstractDebbieEvent> void multicastEvent(E event, Class<I> type) {
        long start = System.nanoTime();
        consumeEventListener(type, (eventListener) -> {
            if (!eventListener.async()) {
                onEvent(eventListener, event);
            } else if (eventListener.allowConcurrent()) {
                synchronized (this) {
                    onEvent(eventListener, event);
                }
            } else {
                try {
                    executor.execute(() -> onEvent(eventListener, event));
                } catch (Exception e) {
                    Throwable cause = e.getCause();
                    if (cause == null) {
                        cause = e;
                    }
                    LOGGER.error("listener handler event(" + event + ") error. ", cause);
                }
            }
        });
        long end = System.nanoTime();
        LOGGER.debug(() -> "publishEvent(" + event.getClass().getName() + ") spend time: " + ((end - start) / 1000000.0D) + "ms");
    }

    @Override
    public <E extends AbstractDebbieEvent> void publishEvent(E event) {
        multicastEvent(event, event.getType());
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private void onEvent(DebbieEventListener eventListener, AbstractDebbieEvent event) {
        try {
            eventListener.onEvent(event);
        } catch (Throwable ex) {
            Throwable cause = ex.getCause();
            int i = 0;
            while (i < 6 && cause != null) {
                i++;
                if (cause instanceof InvocationTargetException || cause instanceof UndeclaredThrowableException) {
                    Throwable cause1 = ex.getCause();
                    if (cause1 != null) {
                        cause = cause1;
                    }
                } else {
                    break;
                }
            }
            if (cause == null) {
                cause = ex;
            }
            LOGGER.error("listener handler event(" + event + ") error. ", cause);
        }
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
            } else if (info.getBeanInfo() != null) {
                BeanInfo<? extends DebbieEventListener<? extends AbstractDebbieEvent>> beanInfo = info.getBeanInfo();
                if (beanInfo instanceof BeanFactory beanFactory) {
                    value.add((DebbieEventListener<? extends AbstractDebbieEvent>) beanFactory.factoryBean(applicationContext));
                }
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
            }  else if (info.getBeanInfo() != null) {
                BeanInfo<? extends DebbieEventListener<? extends AbstractDebbieEvent>> beanInfo = info.getBeanInfo();
                if (beanInfo instanceof BeanFactory beanFactory) {
                    consumer.accept((DebbieEventListener) beanFactory.factoryBean(applicationContext));
                }
            }
        });
    }

    @Override
    public void destruct(ApplicationContext applicationContext) {
        eventListenerMap.clear();
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultEventListenerBeanManager.class);
}
