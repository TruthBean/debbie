package com.truthbean.debbie.event;

import java.util.EventObject;

public abstract class AbstractDebbieEvent extends EventObject {

    /**
     * System time when the event happened
     */
    private final long timestamp;


    /**
     * Create a new AbstractDebbieEvent.
     *
     * @param source the object on which the event initially occurred (never {@code null})
     */
    public AbstractDebbieEvent(Object source) {
        super(source);
        this.timestamp = System.currentTimeMillis();
    }


    /**
     * @return the system time in milliseconds when the event happened.
     */
    public final long getTimestamp() {
        return this.timestamp;
    }

}