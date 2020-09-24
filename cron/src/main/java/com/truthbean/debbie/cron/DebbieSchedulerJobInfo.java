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

import com.truthbean.debbie.lang.Callback;
import com.truthbean.debbie.task.DebbieTask;
import com.truthbean.debbie.task.MethodTaskInfo;

import java.lang.reflect.Method;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * @author TruthBean/Rogar·Q
 * @since 0.1.0
 * Created on 2020-08-11 09:53
 */
public class DebbieSchedulerJobInfo extends MethodTaskInfo {
    public DebbieSchedulerJobInfo() {
    }

    public DebbieSchedulerJobInfo(MethodTaskInfo taskInfo) {
        super(taskInfo.getTaskBeanClass(), taskInfo.getTaskBean(), taskInfo.getTaskMethod(),
                taskInfo.getTaskAnnotation(), taskInfo.getConsumer());
    }

    public String getGroupName() {
        return getTaskBeanClass().getName();
    }

    public String getJobName() {
        return "job-" + getTaskMethod().getName();
    }

    public String getTriggerName() {
        return "trigger-" + getTaskMethod().getName();
    }
}