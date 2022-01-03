package com.truthbean.debbie.check.event;

import com.truthbean.debbie.core.ApplicationContext;
import com.truthbean.debbie.event.AbstractDebbieEvent;
import com.truthbean.debbie.event.DebbieStartedEvent;

/**
 * @author truthbean
 * @since 0.1.0
 * Created on 2020/7/13 15:17.
 */
public class TestStartedEvent extends DebbieStartedEvent {
    /**
     * @param source             event source
     * @param applicationContext bean factory handler
     * @see AbstractDebbieEvent#AbstractDebbieEvent(Object)
     */
    public TestStartedEvent(Object source, ApplicationContext applicationContext) {
        super(source);
    }
}
