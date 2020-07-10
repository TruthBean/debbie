/**
 * Copyright (c) 2020 TruthBean(Rogar·Q)
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
public interface DebbieStartedEventListener extends GenericEventListener<DebbieStartedEvent> {
    @Override
    default boolean supportsSourceType(Class<?> sourceType) {
        return sourceType == DebbieStartedEvent.class;
    }

    @Override
    default Class<DebbieStartedEvent> getEventType() {
        return DebbieStartedEvent.class;
    }

    default void onEvent(DebbieStartedEvent event) {
        // do more
    }
}
