package com.truthbean.debbie.event;

import com.truthbean.debbie.bean.DebbieApplicationContext;

/**
 * @author truthbean
 * @since 0.1.0
 * Created on 2020/7/13 15:17.
 */
@EventComponent
public class TestStartedEvent extends DebbieStartedEvent {
    /**
     * @param source             event source
     * @param applicationContext bean factory handler
     * @see AbstractDebbieEvent#AbstractDebbieEvent(Object)
     */
    public TestStartedEvent(Object source, DebbieApplicationContext applicationContext) {
        super(source);
    }
}
