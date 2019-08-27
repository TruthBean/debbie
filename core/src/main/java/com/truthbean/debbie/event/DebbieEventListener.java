package com.truthbean.debbie.event;

import java.util.EventListener;

public interface DebbieEventListener<E extends DebbieEvent> extends EventListener {

    /**
     * Handle an application event.
     *
     * @param event the event to respond to
     */
    void onEvent(E event);
}