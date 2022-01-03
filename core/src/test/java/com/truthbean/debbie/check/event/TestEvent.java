package com.truthbean.debbie.check.event;

import com.truthbean.debbie.bean.BeanComponent;
import com.truthbean.debbie.bean.BeanInit;
import com.truthbean.debbie.bean.BeanInject;
import com.truthbean.debbie.event.AbstractDebbieEvent;

@BeanComponent("test")
public class TestEvent extends AbstractDebbieEvent {

    private String event;

    @BeanInit
    public static TestEvent init(@BeanInject TestEventListener eventListener) {
        return new TestEvent(eventListener ,eventListener);
    }

    /**
     * Constructs a prototypical Event.
     *
     * @param source the object on which the Event initially occurred
     * @throws IllegalArgumentException if source is null
     */
    public TestEvent(Object source, TestEventListener eventListener) {
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
