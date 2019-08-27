package com.truthbean.debbie.event;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedHashMap;
import java.util.Map;

public class DefaultEventPublisher implements DebbieEventPublisher {
    private final Map<Class<? extends DebbieEvent>, DebbieEventListener> eventListenerMap = new LinkedHashMap<>();

    public void addEventListener(Class<? extends DebbieEvent> eventType, DebbieEventListener<? extends DebbieEvent> listener) {
        eventListenerMap.put(eventType, listener);
    }

    @Override
    public void publishEvent(DebbieEvent event) {
        long start = System.currentTimeMillis();
        for (var classDebbieEventListenerEntry : eventListenerMap.entrySet()) {
            if (event.getClass() == classDebbieEventListenerEntry.getKey()) {
                classDebbieEventListenerEntry.getValue().onEvent(event);
            }
        }
        long end = System.currentTimeMillis();
        LOGGER.debug("publishEvent spend time: " + (end - start) + "ms");
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultEventPublisher.class);
}
