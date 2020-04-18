package com.truthbean.debbie.event;

import com.truthbean.debbie.bean.BeanClosure;
import com.truthbean.debbie.task.ThreadPooledExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author truthbean
 * @since 0.0.2
 */
public class DefaultEventPublisher implements DebbieEventPublisher, BeanClosure {
    private final Map<Class<? extends AbstractDebbieEvent>, DebbieEventListener> eventListenerMap;

    private ThreadPooledExecutor executor;
    public DefaultEventPublisher(ThreadPooledExecutor threadPooledExecutor) {
        this.executor = threadPooledExecutor;
        this.eventListenerMap = new LinkedHashMap<>();;
    }

    public void addEventListener(Class<? extends AbstractDebbieEvent> eventType, DebbieEventListener<? extends AbstractDebbieEvent> listener) {
        eventListenerMap.put(eventType, listener);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <E extends AbstractDebbieEvent> void publishEvent(E event) {
        long start = System.currentTimeMillis();
        for (var classDebbieEventListenerEntry : eventListenerMap.entrySet()) {
            if (event.getClass() == classDebbieEventListenerEntry.getKey()) {
                DebbieEventListener eventListener = classDebbieEventListenerEntry.getValue();
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
        LOGGER.debug("publishEvent spend time: " + (end - start) + "ms");
    }

    @Override
    public void destroy() {
        eventListenerMap.clear();
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultEventPublisher.class);
}
