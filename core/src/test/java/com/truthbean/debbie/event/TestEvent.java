package com.truthbean.debbie.event;

import com.truthbean.debbie.bean.BeanInject;

@DebbieEvent("test")
public class TestEvent extends AbstractDebbieEvent {

    private String event;

    public TestEvent(@BeanInject TestEventListener eventListener) {
        super(eventListener);
        this.event = "test";
    }

    /**
     * Constructs a prototypical Event.
     *
     * @param source the object on which the Event initially occurred
     * @throws IllegalArgumentException if source is null
     */
    /*public TestEvent(Object source, String event) {
        super(source);
        this.event = event;
    }*/

    public String getEvent() {
        return event;
    }

    public void setEvent(String event) {
        this.event = event;
    }
}
