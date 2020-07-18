package com.truthbean.debbie.event;

@FunctionalInterface
public interface ApplicationListener<E extends TestStartedEvent> extends GenericStartedEventListener<E> {

    /**
     * handle an application event.
     *
     * @param event the event to respond to
     */
    void onApplicationEvent(E event);
}