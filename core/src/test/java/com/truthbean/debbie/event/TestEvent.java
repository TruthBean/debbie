package com.truthbean.debbie.event;

import com.truthbean.debbie.bean.BeanInject;

@EventComponent("test")
public class TestEvent extends AbstractDebbieEvent {

    private String event;

    /**
     * Constructs a prototypical Event.
     *
     * @param source the object on which the Event initially occurred
     * @throws IllegalArgumentException if source is null
     */
    public TestEvent(Object source, @BeanInject TestEventListener eventListener) {
        super(source);
        this.event = "test";
    }

    public String getEvent() {
        return event;
    }

    public void setEvent(String event) {
        this.event = event;
    }
}
