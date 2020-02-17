package com.truthbean.debbie.event;

/**
 * @author TruthBean
 * @since 0.0.2
 */
public abstract class AbstractDebbieStartedEventListener implements GenericEventListener<DebbieStartedEvent> {
    @Override
    public boolean supportsSourceType(Class<?> sourceType) {
        return sourceType == DebbieStartedEvent.class;
    }

    @Override
    public void onEvent(DebbieStartedEvent event) {
        // do more
    }
}
