package com.truthbean.debbie.event;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedHashSet;
import java.util.Set;

public class DefaultEventMulticaster implements DebbieEventMulticaster {

    private final Set<DebbieEventListener<? extends AbstractDebbieEvent>> listeners = new LinkedHashSet<>();
    private final Set<String> listenerBeans = new LinkedHashSet<>();

    @Override
    public void addEventListener(DebbieEventListener<? extends AbstractDebbieEvent> listener) {
        listeners.add(listener);
    }

    @Override
    public void addEventListenerBean(String listenerBeanName) {
        listenerBeans.add(listenerBeanName);
    }

    @Override
    public void removeEventListener(DebbieEventListener<? extends AbstractDebbieEvent> listener) {
        listeners.remove(listener);
    }

    @Override
    public void removeEventListenerBean(String listenerBeanName) {
        listenerBeans.remove(listenerBeanName);
    }

    @Override
    public void removeAllListeners() {
        listeners.clear();
        listenerBeans.clear();
    }

    @SuppressWarnings("unchecked")
    @Override
    public <E extends AbstractDebbieEvent> void multicastEvent(E event) {
        long start = System.currentTimeMillis();
        for (DebbieEventListener listener : listeners) {
            if (listener instanceof GenericEventListener) {
                if (((GenericEventListener) listener).supportsSourceType(event.getClass())) {
                    listener.onEvent(event);
                }
            }
        }
        long end = System.currentTimeMillis();
        LOGGER.debug("multicastEvent spend time: " + (end - start) + "ms");
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultEventMulticaster.class);
}
