package com.truthbean.debbie.event;

import java.util.EventObject;

public abstract class DebbieEvent extends EventObject {

    /**
     * System time when the event happened
     */
    private final long timestamp;


    /**
     * Create a new DebbieEvent.
     *
     * @param source the object on which the event initially occurred (never {@code null})
     */
    public DebbieEvent(Object source) {
        super(source);
        this.timestamp = System.currentTimeMillis();
    }


    /**
     * Return the system time in milliseconds when the event happened.
     */
    public final long getTimestamp() {
        return this.timestamp;
    }

}