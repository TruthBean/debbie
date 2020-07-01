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

import com.truthbean.debbie.bean.DebbieApplicationContext;
import com.truthbean.debbie.bean.GlobalBeanFactory;
import com.truthbean.debbie.concurrent.NamedThreadFactory;
import com.truthbean.debbie.concurrent.ThreadPooledExecutor;

import java.util.List;
import java.util.concurrent.ThreadFactory;

/**
 * @author TruthBean/Rogar·Q
 * @since 0.0.2
 * Created on 2020-05-01 11:32
 */
public class DebbieStartedEventProcessor {

    private final DebbieApplicationContext applicationContext;

    private final ThreadPooledExecutor multicastEventThreadPool;
    private final ThreadPooledExecutor startedEventThreadPool;

    public DebbieStartedEventProcessor(final DebbieApplicationContext applicationContext) {
        this.applicationContext = applicationContext;

        ThreadFactory namedThreadFactory = new NamedThreadFactory("DebbieMulticastEvent", true);
        this.multicastEventThreadPool = new ThreadPooledExecutor(1, 1, namedThreadFactory);

        namedThreadFactory = new NamedThreadFactory("DebbieStartedEvent", true);
        this.startedEventThreadPool = new ThreadPooledExecutor(10, 128, namedThreadFactory);
    }

    public void multicastEvent() {
        final DebbieStartedEvent startedEvent = new DebbieStartedEvent(this, applicationContext);
        GlobalBeanFactory globalBeanFactory = applicationContext.getGlobalBeanFactory();
        final List<AbstractDebbieStartedEventListener> beanInfoList = globalBeanFactory.getBeanList(AbstractDebbieStartedEventListener.class);
        this.multicastEventThreadPool.execute(() -> {
            if (beanInfoList != null) {
                for (AbstractDebbieStartedEventListener startedEventListener : beanInfoList) {
                    if (startedEventListener.async()) {
                        startedEventThreadPool.execute(() -> startedEventListener.onEvent(startedEvent));
                    } else
                        startedEventListener.onEvent(startedEvent);
                }
            }
        });
    }

    public void stopAll() {
        multicastEventThreadPool.destroy();
        startedEventThreadPool.destroy();
    }
}
