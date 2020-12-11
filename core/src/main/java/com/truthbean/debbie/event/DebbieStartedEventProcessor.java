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

import com.truthbean.debbie.bean.GlobalBeanFactory;
import com.truthbean.debbie.concurrent.NamedThreadFactory;
import com.truthbean.debbie.concurrent.ThreadPooledExecutor;
import com.truthbean.debbie.core.ApplicationContext;

import java.util.List;
import java.util.concurrent.ThreadFactory;

/**
 * @author TruthBean/Rogar·Q
 * @since 0.0.2
 * Created on 2020-05-01 11:32
 */
public class DebbieStartedEventProcessor {

    private final ApplicationContext applicationContext;

    private final ThreadPooledExecutor multicastEventThreadPool;
    private final ThreadPooledExecutor startedEventThreadPool;

    public DebbieStartedEventProcessor(final ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;

        ThreadFactory namedThreadFactory = new NamedThreadFactory("DebbieMulticastEvent", true);
        this.multicastEventThreadPool = new ThreadPooledExecutor(1, 1, namedThreadFactory);

        namedThreadFactory = new NamedThreadFactory("DebbieStartedEvent", true);
        this.startedEventThreadPool = new ThreadPooledExecutor(10, 128, namedThreadFactory);
    }

    public synchronized void multicastEvent() {
        final DebbieStartedEvent startedEvent = new DebbieStartedEvent(this);
        startedEvent.setApplicationContext(applicationContext);
        var globalBeanFactory = applicationContext.getGlobalBeanFactory();
        final List<GenericStartedEventListener<DebbieStartedEvent>> beanInfoList = globalBeanFactory.getBeanList(GenericStartedEventListener.class, true);
        this.multicastEventThreadPool.execute(() -> {
            if (beanInfoList != null) {
                for (GenericStartedEventListener<DebbieStartedEvent> startedEventListener : beanInfoList) {
                    Class<DebbieStartedEvent> eventType = startedEventListener.getEventType();
                    if (eventType == DebbieStartedEvent.class) {
                        if (startedEventListener.async()) {
                            startedEventThreadPool.execute(() -> startedEventListener.onEvent(startedEvent));
                        } else if (startedEventListener.allowConcurrent()) {
                            synchronized (this) {
                                startedEventListener.onEvent(startedEvent);
                            }
                        } else {
                            startedEventListener.onEvent(startedEvent);
                        }
                    } else {
                        DebbieStartedEvent event = globalBeanFactory.factory(eventType);
                        if (startedEventListener.async()) {
                            startedEventThreadPool.execute(() -> startedEventListener.onEvent(event));
                        } else if (startedEventListener.allowConcurrent()) {
                            synchronized (this) {
                                startedEventListener.onApplicationEvent(event);
                            }
                        } else
                            startedEventListener.onEvent(event);
                    }
                }
            }
        });
    }

    public void stopAll() {
        multicastEventThreadPool.destroy();
        startedEventThreadPool.destroy();
    }
}
