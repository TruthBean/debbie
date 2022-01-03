/**
 * Copyright (c) 2021 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *         http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.event;

import com.truthbean.debbie.reflection.ReflectionHelper;

import java.util.EventListener;

/**
 * @author TruthBean
 * @since 0.0.2
 */
@FunctionalInterface
public interface DebbieEventListener<E extends AbstractDebbieEvent> extends EventListener {

    default String getName() {
        return this.getClass().getName();
    }

    default boolean async() {
        return true;
    }

    default boolean allowConcurrent() {
        return false;
    }

    /**
     * Handle an application event.
     *
     * @param event the event to respond to
     */
    void onEvent(E event);

    /**
     * Determine whether this listener actually supports the given source type.
     *
     * @param sourceType the source type, or {@code null} if no source
     *
     * @return boolean
     */
    default boolean supportsSourceType(Class<?> sourceType) {
        return sourceType == getEventType();
    }

    @SuppressWarnings({"unchecked"})
    default Class<? extends AbstractDebbieEvent> getEventType() {
        var types = ReflectionHelper.getActualTypes(getClass());
        if (types != null && types.length > 0) {
            return (Class<? extends AbstractDebbieEvent>) types[0];
        }
        return null;
    }
}