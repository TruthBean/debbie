/**
 * Copyright (c) 2023 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *         http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.event;

import java.time.Clock;
import java.util.EventObject;

public abstract class AbstractDebbieEvent extends EventObject {

    /**
     * System time when the event happened
     */
    private final long timestamp;


    /**
     * Create a new AbstractDebbieEvent.
     *
     * @param source the object on which the event initially occurred (never {@code null})
     */
    public AbstractDebbieEvent(Object source) {
        super(source);
        this.timestamp = System.currentTimeMillis();
    }


    /**
     * Create a new {@code ApplicationEvent} with its {@link #getTimestamp() timestamp}
     * set to the value returned by {@link Clock#millis()} in the provided {@link Clock}.
     * <p>This constructor is typically used in testing scenarios.
     *
     * @param source the object on which the event initially occurred or with
     *               which the event is associated (never {@code null})
     * @param clock  a clock which will provide the timestamp
     * @see #AbstractDebbieEvent(Object)
     */
    public AbstractDebbieEvent(Object source, Clock clock) {
        super(source);
        this.timestamp = clock.millis();
    }


    /**
     * @return the system time in milliseconds when the event happened.
     */
    public long getTimestamp() {
        return this.timestamp;
    }

    @SuppressWarnings("unchecked")
    public <E extends AbstractDebbieEvent> Class<E> getType() {
        return (Class<E>) getClass();
    }

}