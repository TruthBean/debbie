/**
 * Copyright (c) 2024 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *         http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.event;

import com.truthbean.debbie.reflection.ReflectionHelper;

import java.lang.reflect.Method;

/**
 * @author truthbean/Rogar·Q
 * @since 0.0.2
 * Created on 2020-04-02 18:18.
 */
public class EventMethodListenerFactory<EVENT extends AbstractDebbieEvent> implements DebbieEventListener<EVENT> {

    private final Class<EVENT> eventType;
    private final Object targetBean;
    private final Method eventMethod;

    private boolean async = false;
    private boolean allowConcurrent = false;

    public EventMethodListenerFactory(Object targetBean, Class<EVENT> eventType, Method eventMethod) {
        this.eventType = eventType;
        this.targetBean = targetBean;
        this.eventMethod = eventMethod;
    }

    @Override
    public boolean async() {
        return this.async;
    }

    public void setAsync(boolean async) {
        this.async = async;
    }

    @Override
    public boolean allowConcurrent() {
        return this.allowConcurrent;
    }

    public void setAllowConcurrent(boolean allowConcurrent) {
        this.allowConcurrent = allowConcurrent;
    }

    @Override
    public boolean supportsSourceType(Class<?> sourceType) {
        return sourceType == this.eventType;
    }

    @Override
    public Class<EVENT> getEventType() {
        return eventType;
    }

    @Override
    public void onEvent(EVENT event) {
        ReflectionHelper.invokeMethod(targetBean, eventMethod, event);
    }
}
