package com.truthbean.debbie.check.event;

import com.truthbean.debbie.bean.BeanComponent;
import com.truthbean.Logger;
import com.truthbean.debbie.event.EventMethodListener;
import com.truthbean.LoggerFactory;

@BeanComponent
public class TestEventListener {

    @EventMethodListener
    public void onTestEvent(TestEvent event) {
        LOGGER.debug(() -> "do event.....");
        LOGGER.debug(event::getEvent);
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(TestEventListener.class);
}
