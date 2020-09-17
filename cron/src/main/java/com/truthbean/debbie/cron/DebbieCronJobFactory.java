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
import com.truthbean.logger.LoggerFactory;
import org.quartz.*;
import org.quartz.simpl.PropertySettingJobFactory;
import org.quartz.spi.TriggerFiredBundle;

/**
 * @author TruthBean/Rogar·Q
 * @since 0.1.0
 * Created on 2020-08-11 12:31
 */
public class DebbieCronJobFactory extends PropertySettingJobFactory {

    private final DebbieSchedulerJobInfo jobInfo;
    public DebbieCronJobFactory(DebbieSchedulerJobInfo jobInfo) {
        this.jobInfo = jobInfo;
    }

    @Override
    public Job newJob(TriggerFiredBundle bundle, Scheduler scheduler) throws SchedulerException {

        JobDetail jobDetail = bundle.getJobDetail();
        Class<? extends Job> jobClass = jobDetail.getJobClass();

        Job job;
        try {
            if(LOGGER.isDebugEnabled()) {
                LOGGER.debug(
                        "Producing instance of Job '" + jobDetail.getKey() +
                                "', class=" + jobClass.getName());
            }
            if (jobClass == SchedulerJobProxy.class) {
                job = new SchedulerJobProxy(jobInfo);
            } else {
                job = jobClass.getConstructor().newInstance();
            }
        } catch (Exception e) {
            throw new SchedulerException(
                    "Problem instantiating class '"
                            + jobDetail.getJobClass().getName() + "'", e);
        }

        JobDataMap jobDataMap = new JobDataMap();
        jobDataMap.putAll(scheduler.getContext());
        jobDataMap.putAll(bundle.getJobDetail().getJobDataMap());
        jobDataMap.putAll(bundle.getTrigger().getJobDataMap());

        setBeanProps(job, jobDataMap);

        return job;
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(DebbieCronJobFactory.class);
}
