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

/**
 * @author TruthBean
 * @since 0.0.2
 */
@FunctionalInterface
public interface GenericStartedEventListener<E extends DebbieStartedEvent> extends DebbieEventListener<E> {

    @Override
    default void onEvent(E event) {
        onApplicationEvent(event);
    }

    /**
     * handle an application event.
     *
     * @param event the event to respond to
     */
    void onApplicationEvent(E event);
}
