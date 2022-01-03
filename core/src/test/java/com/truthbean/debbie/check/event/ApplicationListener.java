package com.truthbean.debbie.check.event;

import com.truthbean.debbie.event.GenericStartedEventListener;

@FunctionalInterface
public interface ApplicationListener<E extends TestStartedEvent> extends GenericStartedEventListener<E> {

    /**
     * handle an application event.
     *
     * @param event the event to respond to
     */
    @Override
    void onApplicationEvent(E event);
}