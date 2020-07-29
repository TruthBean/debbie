package com.truthbean.debbie.event;

import com.truthbean.Logger;
import com.truthbean.debbie.bean.BeanClosure;
import com.truthbean.debbie.bean.BeanFactory;
import com.truthbean.debbie.bean.BeanType;
import com.truthbean.debbie.bean.DebbieBeanFactory;
import com.truthbean.debbie.bean.DebbieBeanInfo;
import com.truthbean.debbie.concurrent.ThreadPooledExecutor;
import com.truthbean.logger.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author truthbean
 * @since 0.0.2
 */
public class DefaultEventPublisher implements DebbieEventPublisher, DebbieEventMulticaster, BeanClosure {
    private final Map<Class<? extends AbstractDebbieEvent>, BeanFactory<? extends DebbieEventListener<? extends AbstractDebbieEvent>>> eventListenerMap;

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
        eventListenerMap.put(eventType, listenerBeanFactory);
    }

    @Override
    public void addEventListener(DebbieEventListener<? extends AbstractDebbieEvent> listener) {
        DebbieBeanFactory<DebbieEventListener<? extends AbstractDebbieEvent>> listenerBeanFactory = new DebbieBeanFactory<>();
        @SuppressWarnings("unchecked")
        var listenerType = (Class<DebbieEventListener<? extends AbstractDebbieEvent>>) listener.getClass();
        DebbieBeanInfo<DebbieEventListener<? extends AbstractDebbieEvent>> beanInfo = new DebbieBeanInfo<>(listenerType);
        beanInfo.setBeanType(BeanType.SINGLETON);
        beanInfo.setBean(listener);
        listenerBeanFactory.setBeanInfo(beanInfo);
        eventListenerMap.put(listener.getEventType(), listenerBeanFactory);
    }

    @Override
    public void addEventListenerBean(String listenerBeanName) {
        // todo
    }

    @Override
    public void removeEventListener(DebbieEventListener<? extends AbstractDebbieEvent> listener) {
        eventListenerMap.remove(listener.getEventType());
    }

    @Override
    public void removeEventListenerBean(String listenerBeanName) {
        // todo
    }

    @Override
    public void removeAllListeners() {
        eventListenerMap.clear();
    }

    @Override
    public <E extends AbstractDebbieEvent> void multicastEvent(E event) {
        publishEvent(event);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    @Override
    public <E extends AbstractDebbieEvent> void publishEvent(E event) {
        long start = System.currentTimeMillis();
        for (var classDebbieEventListenerEntry : eventListenerMap.entrySet()) {
            if (event.getClass() == classDebbieEventListenerEntry.getKey()) {
                var beanFactory = classDebbieEventListenerEntry.getValue();
                DebbieEventListener eventListener = beanFactory.getBean();
                if (!eventListener.async()) {
                    eventListener.onEvent(event);
                } else {
                    try {
                        executor.execute(() -> {
                            try {
                                eventListener.onEvent(event);
                            } catch (Exception ex) {
                                LOGGER.error("", ex);
                            }
                        });
                    } catch (Exception e) {
                        LOGGER.error("", e);
                    }
                }
            }
        }
        long end = System.currentTimeMillis();
        LOGGER.debug(() -> "publishEvent spend time: " + (end - start) + "ms");
    }

    @Override
    public void destroy() {
        eventListenerMap.clear();
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultEventPublisher.class);
}
