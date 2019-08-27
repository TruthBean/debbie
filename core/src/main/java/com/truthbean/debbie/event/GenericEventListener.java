package com.truthbean.debbie.event;

public interface GenericEventListener<E extends DebbieEvent> extends DebbieEventListener<E> {

    /**
     * Determine whether this listener actually supports the given source type.
     *
     * @param sourceType the source type, or {@code null} if no source
     */
    boolean supportsSourceType(Class<?> sourceType);

}