package com.truthbean.debbie.event;

import com.truthbean.debbie.bean.BeanComponent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@BeanComponent
public class TestEventListener {

    @EventMethodListener
    public void onTestEvent(TestEvent event) {
        LOGGER.debug("do event.....");
        LOGGER.debug(event.getEvent());
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(TestEventListener.class);
}
