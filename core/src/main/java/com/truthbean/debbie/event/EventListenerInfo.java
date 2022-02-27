/**
 * Copyright (c) 2022 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 * http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.event;

import com.truthbean.debbie.bean.BeanInfo;

import java.util.Objects;

/**
 * @author TruthBean/Rogar·Q
 * @since 0.2.0
 * Created on 2020-12-21 16:52
 */
public class EventListenerInfo {
    private String eventListenerName;
    private Class<? extends AbstractDebbieEvent> eventClass;
    private DebbieEventListener<? extends AbstractDebbieEvent> eventListener;
    private BeanInfo<? extends DebbieEventListener<? extends AbstractDebbieEvent>> beanInfo;

    public String getEventListenerName() {
        return eventListenerName;
    }

    public void setEventListenerName(String eventListenerName) {
        this.eventListenerName = eventListenerName;
    }

    public Class<? extends AbstractDebbieEvent> getEventClass() {
        return eventClass;
    }

    public void setEventClass(Class<? extends AbstractDebbieEvent> eventClass) {
        this.eventClass = eventClass;
    }

    public DebbieEventListener<? extends AbstractDebbieEvent> getEventListener() {
        return eventListener;
    }

    public void setEventListener(DebbieEventListener<? extends AbstractDebbieEvent> eventListener) {
        this.eventListener = eventListener;
    }

    public BeanInfo<? extends DebbieEventListener<? extends AbstractDebbieEvent>> getBeanInfo() {
        return beanInfo;
    }

    public void setBeanInfo(BeanInfo<? extends DebbieEventListener<? extends AbstractDebbieEvent>> beanInfo) {
        this.beanInfo = beanInfo;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        EventListenerInfo that = (EventListenerInfo) o;
        return Objects.equals(eventListenerName, that.eventListenerName) && Objects.equals(eventClass, that.eventClass) && Objects.equals(eventListener, that.eventListener) && Objects.equals(beanInfo, that.beanInfo);
    }

    @Override
    public int hashCode() {
        return Objects.hash(eventListenerName, eventClass, eventListener, beanInfo);
    }
}
