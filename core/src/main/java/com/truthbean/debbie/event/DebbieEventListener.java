package com.truthbean.debbie.event;

import java.util.EventListener;

/**
 * @author TruthBean
 * @since 0.0.2
 */
public interface DebbieEventListener<E extends AbstractDebbieEvent> extends EventListener {

    /**
     * Handle an application event.
     *
     * @param event the event to respond to
     */
    void onEvent(E event);
}