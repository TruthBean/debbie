package com.truthbean.debbie.event;

/**
 * @author TruthBean
 * @since 0.0.2
 */
@FunctionalInterface
public interface DebbieEventPublisher {

    <E extends AbstractDebbieEvent> void publishEvent(E event);

}