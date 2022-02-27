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
import com.truthbean.debbie.bean.BeanClosure;
import com.truthbean.debbie.bean.BeanFactory;

/**
 * @author TruthBean/Rogar·Q
 * @since 0.2.0
 * Created on 2020-12-21 17:25
 */
public interface EventListenerBeanManager extends EventMulticaster, BeanClosure {

    <EL extends DebbieEventListener<E>, E extends AbstractDebbieEvent> void addEventListener(Class<E> eventType,
                          BeanFactory<EL> listenerBeanFactory);

    void addEventListener(Class<? extends AbstractDebbieEvent> eventType,
                          BeanInfo<? extends DebbieEventListener<? extends AbstractDebbieEvent>> eventListenerBeanInfo);

    void addEventListenerInfo(EventListenerInfo eventListenerInfo);

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
     * Remove a listener from the notification list.
     * @param listenerBeanFactory the listener factory to remove
     */
    void removeEventListener(BeanFactory<? extends DebbieEventListener<? extends AbstractDebbieEvent>> listenerBeanFactory);

    /**
     * Remove a listener bean from the notification list.
     * @param listenerBeanName the name of the listener bean to add
     */
    void removeEventListener(String listenerBeanName);
}
