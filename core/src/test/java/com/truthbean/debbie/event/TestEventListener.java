package com.truthbean.debbie.event;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@EventBeanListener
public class TestEventListener implements GenericEventListener<TestEvent> {

    @Override
    public void onEvent(TestEvent event) {
        LOGGER.debug("do event.....");
        LOGGER.debug(event.getEvent());
    }

    @Override
    public boolean supportsSourceType(Class<?> sourceType) {
        return sourceType == TestEvent.class;
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(TestEventListener.class);
}
