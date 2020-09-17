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

import com.truthbean.debbie.bean.BeanInitialization;
import com.truthbean.debbie.bean.DebbieBeanInfo;
import com.truthbean.debbie.boot.DebbieModuleStarter;
import com.truthbean.debbie.core.ApplicationContext;
import org.quartz.Scheduler;
import org.quartz.impl.StdSchedulerFactory;

/**
 * @author TruthBean/Rogar·Q
 * @since 0.1.0
 * Created on 2020-08-11 09:35
 */
public class DebbieCronModuleStarter implements DebbieModuleStarter {

    static {
        System.setProperty(StdSchedulerFactory.PROPERTIES_FILE, "application.properties");
    }

    @Override
    public int getOrder() {
        return 9;
    }

    @Override
    public void registerBean(ApplicationContext applicationContext, BeanInitialization beanInitialization) {
        DebbieBeanInfo<Scheduler> schedulerBeanInfo = new DebbieBeanInfo<>(Scheduler.class);
        schedulerBeanInfo.addBeanName("scheduler");
        schedulerBeanInfo.setBeanFactory(new SchedulerBeanFactory());
        beanInitialization.initSingletonBean(schedulerBeanInfo);
    }
}
