package com.truthbean.debbie.event;

/**
 * @author truthbean
 * @since 0.0.2
 */
public interface DebbieEventMulticaster {
    /**
     * Add a listener to be notified of all events.
     * @param listener the listener to add
     */
    void addEventListener(DebbieEventListener<? extends AbstractDebbieEvent> listener);

    /**
     * Add a listener bean to be notified of all events.
     * @param listenerBeanName the name of the listener bean to add
     */
    void addEventListenerBean(String listenerBeanName);

    /**
     * Remove a listener from the notification list.
     * @param listener the listener to remove
     */
    void removeEventListener(DebbieEventListener<? extends AbstractDebbieEvent> listener);

    /**
     * Remove a listener bean from the notification list.
     * @param listenerBeanName the name of the listener bean to add
     */
    void removeEventListenerBean(String listenerBeanName);

    /**
     * Remove all listeners registered with this multicaster.
     * <p>After a remove call, the multicaster will perform no action
     * on event notification until new listeners are being registered.
     */
    void removeAllListeners();

    /**
     * Multicast the given application event to appropriate listeners.
     * @param <E> AbstractDebbieEvent subclass
     * @param event the event to multicast
     */
    <E extends AbstractDebbieEvent> void multicastEvent(E event);
}
