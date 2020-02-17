package com.truthbean.debbie.event;

/**
 * @author truthbean
 * @since 0.0.2
 */
public interface GenericEventListener<E extends AbstractDebbieEvent> extends DebbieEventListener<E> {

    /**
     * Determine whether this listener actually supports the given source type.
     *
     * @param sourceType the source type, or {@code null} if no source
     *
     * @return boolean
     */
    boolean supportsSourceType(Class<?> sourceType);

}