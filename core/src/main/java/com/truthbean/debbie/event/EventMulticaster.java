/*
  Copyright (c) 2024 TruthBean(Rogar·Q)
  Debbie is licensed under Mulan PSL v2.
  You can use this software according to the terms and conditions of the Mulan PSL v2.
  You may obtain a copy of Mulan PSL v2 at:
          http://license.coscl.org.cn/MulanPSL2
  THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
  See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.event;

/**
 * @author truthbean
 * @since 0.0.2
 */
public interface EventMulticaster extends DebbieEventPublisher {

    <E extends AbstractDebbieEvent, EL extends DebbieEventListener<E>> void addEventListener(Class<E> eventClass, EL listener);

    /**
     * Add a listener to be notified of all events.
     * @param listener the listener to add
     */
    void addEventListener(DebbieEventListener<? extends AbstractDebbieEvent> listener);

    /**
     * Remove all listeners registered with this multicaster by the AbstractDebbieEvent.
     * <p>After a remove call, the multicaster will perform no action
     * on event notification until new listeners are being registered.
     */
    void removeAllListeners(Class<? extends AbstractDebbieEvent> eventClass);

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

    <E extends I, I extends AbstractDebbieEvent> void multicastEvent(E event, Class<I> type);
}
