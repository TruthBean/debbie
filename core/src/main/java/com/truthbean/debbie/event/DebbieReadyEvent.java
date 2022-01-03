package com.truthbean.debbie.event;

import com.truthbean.debbie.core.ApplicationContext;

/**
 * @author TruthBean
 * @since 0.5.3
 * Created on 2021/11/25 21:03.
 */
public class DebbieReadyEvent extends AbstractDebbieEvent {
    private final ApplicationContext applicationContext;

    /**
     * Create a new AbstractDebbieEvent.
     *
     * @param source the object on which the event initially occurred (never {@code null})
     */
    public DebbieReadyEvent(ApplicationContext applicationContext, Object source) {
        super(source);
        this.applicationContext = applicationContext;
    }

    public ApplicationContext getApplicationContext() {
        return applicationContext;
    }
}
