/**
 * Copyright (c) 2020 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 * http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.cron;

import com.truthbean.Logger;
import com.truthbean.debbie.bean.BeanCreatedException;
import com.truthbean.debbie.bean.BeanFactory;
import com.truthbean.debbie.bean.GlobalBeanFactory;
import com.truthbean.debbie.properties.BaseProperties;
import com.truthbean.logger.LoggerFactory;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.impl.StdSchedulerFactory;

/**
 * @author TruthBean/Rogar·Q
 * @since 0.1.0
 * Created on 2020-08-11 09:39
 */
public class SchedulerBeanFactory implements BeanFactory<Scheduler> {
    private volatile Scheduler scheduler;
    @Override
    public Scheduler getBean() {
        if (scheduler == null) {
            synchronized (SchedulerBeanFactory.class) {
                if (scheduler == null) {
                    BaseProperties properties = new BaseProperties();
                    properties.addProperty("org.quartz.threadPool.threadCount", "10");
                    try {
                        StdSchedulerFactory factory = new StdSchedulerFactory(properties.getProperties());
                        this.scheduler = factory.getScheduler();
                    } catch (SchedulerException e) {
                        LOGGER.error("", e);
                        throw new BeanCreatedException(e);
                    }
                }
            }
        }
        return this.scheduler;
    }

    @Override
    public Class<Scheduler> getBeanType() {
        return Scheduler.class;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

    @Override
    public void destroy() {
        if (this.scheduler != null) {
            try {
                if (this.scheduler.isStarted() && !this.scheduler.isShutdown())
                    this.scheduler.shutdown(true);
            } catch (SchedulerException e) {
                LOGGER.error("", e);
            }
        }
    }

    @Override
    public void setGlobalBeanFactory(GlobalBeanFactory globalBeanFactory) {

    }

    private static final Logger LOGGER = LoggerFactory.getLogger(SchedulerBeanFactory.class);
}
