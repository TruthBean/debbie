/**
 * Copyright (c) 2021 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 * http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.event;

import com.truthbean.Logger;
import com.truthbean.LoggerFactory;
import com.truthbean.debbie.concurrent.NamedThreadFactory;
import com.truthbean.debbie.concurrent.ThreadPooledExecutor;
import com.truthbean.debbie.core.ApplicationContext;

import java.util.List;
import java.util.Set;
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

    @SuppressWarnings({"unchecked", "rawtypes"})
    public synchronized void multicastDebbieStartedEvent() {
        final DebbieStartedEvent startedEvent = new DebbieStartedEvent(this);
        startedEvent.setApplicationContext(applicationContext);
        var globalBeanFactory = applicationContext.getGlobalBeanFactory();
        final Set<GenericStartedEventListener> beanInfoList = globalBeanFactory.getBeanList(GenericStartedEventListener.class);
        this.multicastEventThreadPool.execute(() -> {
            if (beanInfoList != null) {
                for (GenericStartedEventListener startedEventListener : beanInfoList) {
                    Class<?> eventType = startedEventListener.getEventType();
                    if (eventType == DebbieStartedEvent.class) {
                        if (startedEventListener.async()) {
                            startedEventThreadPool.execute(() -> {
                                try {
                                    startedEventListener.onEvent(startedEvent);
                                } catch (Throwable e) {
                                    Throwable cause = e.getCause();
                                    if (cause == null) {
                                        cause = e;
                                    }
                                    LOGGER.error("DebbieStartedEvent(" + startedEvent + ") on listener error. ", cause);
                                }
                            });
                        } else if (startedEventListener.allowConcurrent()) {
                            synchronized (this) {
                                try {
                                    startedEventListener.onEvent(startedEvent);
                                } catch (Throwable e) {
                                    Throwable cause = e.getCause();
                                    if (cause == null) {
                                        cause = e;
                                    }
                                    LOGGER.error("DebbieStartedEvent(" + startedEvent + ") on listener error. ", cause);
                                }
                            }
                        } else {
                            try {
                                startedEventListener.onEvent(startedEvent);
                            } catch (Throwable e) {
                                Throwable cause = e.getCause();
                                if (cause == null) {
                                    cause = e;
                                }
                                LOGGER.error("DebbieStartedEvent(" + startedEvent + ") on listener error. ", cause);
                            }
                        }
                    } else {
                        DebbieStartedEvent event = (DebbieStartedEvent) globalBeanFactory.factory(eventType);
                        if (startedEventListener.async()) {
                            try {
                                startedEventThreadPool.execute(() -> startedEventListener.onEvent(event));
                            } catch (Throwable e) {
                                Throwable cause = e.getCause();
                                if (cause == null) {
                                    cause = e;
                                }
                                LOGGER.error("DebbieStartedEvent(" + startedEvent + ") on listener error. ", cause);
                            }
                        } else if (startedEventListener.allowConcurrent()) {
                            synchronized (this) {
                                try {
                                    startedEventListener.onApplicationEvent(event);
                                } catch (Throwable e) {
                                    Throwable cause = e.getCause();
                                    if (cause == null) {
                                        cause = e;
                                    }
                                    LOGGER.error("DebbieStartedEvent(" + startedEvent + ") on listener error. ", cause);
                                }
                            }
                        } else {
                            try {
                                startedEventListener.onEvent(event);
                            } catch (Throwable e) {
                                Throwable cause = e.getCause();
                                if (cause == null) {
                                    cause = e;
                                }
                                LOGGER.error("DebbieStartedEvent(" + startedEvent + ") on listener error. ", cause);
                            }
                        }
                    }
                }
            }
        });
    }

    public void stopAll() {
        multicastEventThreadPool.destroy();
        startedEventThreadPool.destroy();
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(DebbieStartedEventProcessor.class);
}
