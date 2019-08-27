package com.truthbean.debbie.event;

@FunctionalInterface
public interface DebbieEventPublisher<E extends DebbieEvent> {

    void publishEvent(E event);

}