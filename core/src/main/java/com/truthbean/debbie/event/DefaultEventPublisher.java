package com.truthbean.debbie.event;

import com.truthbean.Logger;
import com.truthbean.LoggerFactory;
import com.truthbean.debbie.bean.*;
import com.truthbean.debbie.concurrent.ConcurrentHashSet;
import com.truthbean.debbie.concurrent.ThreadPooledExecutor;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.UndeclaredThrowableException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Consumer;

/**
 * @author truthbean
 * @since 0.0.2
 */
public class DefaultEventPublisher implements DebbieEventPublisher, EventMulticaster, BeanClosure {
    private final ConcurrentMap<Class<? extends AbstractDebbieEvent>, ConcurrentHashSet<BeanFactory<? extends DebbieEventListener<? extends AbstractDebbieEvent>>>> eventListenerMap;
    private final ConcurrentMap<Class<? extends AbstractDebbieEvent>, ConcurrentHashSet<DebbieEventListener<? extends AbstractDebbieEvent>>> cache = new ConcurrentHashMap<>();

    private final ThreadPooledExecutor executor;

    public DefaultEventPublisher(ThreadPooledExecutor threadPooledExecutor) {
        this.executor = threadPooledExecutor;
        this.eventListenerMap = new ConcurrentHashMap<>();
    }

    public void addEventListener(Class<? extends AbstractDebbieEvent> eventType,
                                 BeanFactory<? extends DebbieEventListener<? extends AbstractDebbieEvent>> listenerBeanFactory) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(() -> "eventType: " + eventType + " ; listenerType: " + listenerBeanFactory.getBeanType());
        }
        var set = eventListenerMap.getOrDefault(eventType, new ConcurrentHashSet<>());
        set.add(listenerBeanFactory);
        eventListenerMap.put(eventType, set);
    }

    @Override
    public void addEventListener(DebbieEventListener<? extends AbstractDebbieEvent> listener) {
        DebbieBeanFactory<DebbieEventListener<? extends AbstractDebbieEvent>> listenerBeanFactory = new DebbieBeanFactory<>();
        @SuppressWarnings("unchecked")
        var listenerType = (Class<DebbieEventListener<? extends AbstractDebbieEvent>>) listener.getClass();
        MutableBeanInfo<DebbieEventListener<? extends AbstractDebbieEvent>> beanInfo = new DebbieBeanInfo<>(listenerType);
        beanInfo.setBeanType(BeanType.SINGLETON);
        beanInfo.setBean(listener);
        listenerBeanFactory.setBeanInfo(beanInfo);
        var eventType = listener.getEventType();
        var set = eventListenerMap.getOrDefault(eventType, new ConcurrentHashSet<>());
        set.add(listenerBeanFactory);
        eventListenerMap.put(eventType, set);
    }

    @Override
    public void removeAllListeners(Class<? extends AbstractDebbieEvent> eventClass) {
        eventListenerMap.remove(eventClass);
        cache.remove(eventClass);
    }

    @Override
    public void removeAllListeners() {
        eventListenerMap.clear();
        cache.clear();
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
                onEvent(eventListener, event);
            } else if (eventListener.allowConcurrent()) {
                synchronized (this) {
                    onEvent(eventListener, event);
                }
            } else {
                try {
                    executor.execute(() -> onEvent(eventListener, event));
                } catch (Exception e) {
                    LOGGER.error("", e);
                }
            }
        });
        long end = System.currentTimeMillis();
        LOGGER.debug(() -> "publishEvent spend time: " + (end - start) + "ms");
    }

    private <E extends AbstractDebbieEvent> void onEvent(DebbieEventListener<E> eventListener, E event) {
        try {
            eventListener.onEvent(event);
        } catch (Throwable ex) {
            Throwable cause = ex.getCause();
            int i = 0;
            while (i < 6 && cause != null) {
                i++;
                if (cause instanceof InvocationTargetException || cause instanceof UndeclaredThrowableException) {
                    Throwable cause1 = ex.getCause();
                    if (cause1 != null)
                        cause = cause1;
                } else {
                    break;
                }
            }
            if (cause == null)
                cause = ex;
            LOGGER.error("listener handler event(" + event + ") error. ", cause);
        }
    }

    public List<BeanFactory<? extends DebbieEventListener<?>>> getEventListenerFactory(Class<?> eventClass) {
        return new ArrayList<>(eventListenerMap.getOrDefault(eventClass, new ConcurrentHashSet<>()));
    }

    public List<DebbieEventListener<?>> getEventListener(Class<? extends AbstractDebbieEvent> eventClass) {
        var set = cache.getOrDefault(eventClass, new ConcurrentHashSet<>());
        var hashSet = eventListenerMap.getOrDefault(eventClass, new ConcurrentHashSet<>());
        if (hashSet.size() == set.size()) {
            return new ArrayList<>(set);
        }

        ConcurrentHashSet<DebbieEventListener<? extends AbstractDebbieEvent>> value = new ConcurrentHashSet<>();
        hashSet.forEach(factory -> value.add(factory.getBean()));
        cache.put(eventClass, value);
        return new ArrayList<>(value);
    }

    @SuppressWarnings({"rawtypes"})
    public void consumeEventListener(Class<? extends AbstractDebbieEvent> eventClass,
                                     Consumer<DebbieEventListener> consumer) {
        var set = cache.getOrDefault(eventClass, new ConcurrentHashSet<>());
        var hashSet = eventListenerMap.getOrDefault(eventClass, new ConcurrentHashSet<>());
        if (hashSet.size() == set.size()) {
            set.forEach(consumer);
            return;
        }

        ConcurrentHashSet<DebbieEventListener<? extends AbstractDebbieEvent>> value = new ConcurrentHashSet<>();
        hashSet.forEach(factory -> {
            var bean = factory.getBean();
            value.add(bean);
            consumer.accept(bean);
        });
        cache.put(eventClass, value);
    }

    @Override
    public void destroy() {
        eventListenerMap.clear();
        cache.clear();
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultEventPublisher.class);
}
