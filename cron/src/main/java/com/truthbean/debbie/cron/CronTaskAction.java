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
import com.truthbean.debbie.bean.GlobalBeanFactory;
import com.truthbean.debbie.core.ApplicationContext;
import com.truthbean.debbie.task.MethodTaskInfo;
import com.truthbean.debbie.task.TaskAction;
import com.truthbean.debbie.util.StringUtils;
import com.truthbean.logger.LoggerFactory;
import org.quartz.*;

/**
 * @author TruthBean/Rogar·Q
 * @since 0.1.0
 * Created on 2020-08-11 11:27
 */
public class CronTaskAction implements TaskAction {
    private GlobalBeanFactory globalBeanFactory;

    @Override
    public void prepare(MethodTaskInfo taskInfo) {
        var cron = taskInfo.getTaskAnnotation().cron();
        if (StringUtils.isBlank(cron))
            return;

        DebbieSchedulerJobInfo jobInfo = new DebbieSchedulerJobInfo(taskInfo);
        Scheduler scheduler = globalBeanFactory.factory(Scheduler.class);
        JobDetail job = JobBuilder.newJob(SchedulerJobProxy.class)
                .withIdentity(jobInfo.getJobName(), jobInfo.getGroupName())
                .build();

        CronTrigger cronTrigger = TriggerBuilder.newTrigger()
                .withIdentity(jobInfo.getTriggerName(), jobInfo.getGroupName())
                .withSchedule(CronScheduleBuilder.cronSchedule(cron))
                .build();

        try {
            scheduler.setJobFactory(new DebbieCronJobFactory(jobInfo));
            scheduler.scheduleJob(job, cronTrigger);
        } catch (SchedulerException e) {
            LOGGER.error("", e);
        }
    }

    @Override
    public void doTask() {
        Scheduler scheduler = globalBeanFactory.factory(Scheduler.class);
        try {
            scheduler.start();
        } catch (SchedulerException e) {
            LOGGER.error("", e);
        }
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) {
        globalBeanFactory = applicationContext.getGlobalBeanFactory();
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(CronTaskAction.class);
}
