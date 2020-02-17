package com.truthbean.debbie.event;

import com.truthbean.debbie.bean.BeanClosure;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author truthbean
 * @since 0.0.2
 */
public class DefaultEventPublisher implements DebbieEventPublisher, BeanClosure {
    private final Map<Class<? extends AbstractDebbieEvent>, DebbieEventListener> eventListenerMap = new LinkedHashMap<>();

    public void addEventListener(Class<? extends AbstractDebbieEvent> eventType, DebbieEventListener<? extends AbstractDebbieEvent> listener) {
        eventListenerMap.put(eventType, listener);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void publishEvent(AbstractDebbieEvent event) {
        long start = System.currentTimeMillis();
        for (var classDebbieEventListenerEntry : eventListenerMap.entrySet()) {
            if (event.getClass() == classDebbieEventListenerEntry.getKey()) {
                classDebbieEventListenerEntry.getValue().onEvent(event);
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
