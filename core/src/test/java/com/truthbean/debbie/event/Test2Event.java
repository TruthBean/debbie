package com.truthbean.debbie.event;

public class Test2Event extends AbstractDebbieEvent {

    private int event;

    /**
     * Constructs a prototypical Event.
     *
     * @param source the object on which the Event initially occurred
     * @throws IllegalArgumentException if source is null
     */
    public Test2Event(Object source, int event) {
        super(source);
        this.event = event;
    }

    public int getEvent() {
        return event;
    }

    public void setEvent(int event) {
        this.event = event;
    }
}
