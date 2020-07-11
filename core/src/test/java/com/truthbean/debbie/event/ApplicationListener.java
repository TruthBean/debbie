package com.truthbean.debbie.event;

@FunctionalInterface
public interface ApplicationListener<E extends DebbieStartedEvent> extends DebbieStartedEventListener {

    /**
     * Handle an application event.
     *
     * @param event the event to respond to
     */
    void onApplicationEvent(E event);
}