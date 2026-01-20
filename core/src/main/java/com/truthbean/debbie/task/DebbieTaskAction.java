/**
 * Copyright (c) 2026 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *         http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.task;

import com.truthbean.Logger;
import com.truthbean.LoggerFactory;

/**
 * @author TruthBean/Rogar·Q
 * @since 0.5.7
 */
public class DebbieTaskAction extends AbstractTaskAction {

    @Override
    protected String getTaskThreadName() {
        return "DebbieTask";
    }

    @Override
    public void prepare() {

    }

    void registerTask(TaskInfo taskInfo) {
        taskInfo.setConsumer(this::doTask);
        taskList.add(taskInfo);
    }

    @Override
    protected Logger getLogger() {
        return LOGGER;
    }

    private void doTask(TaskInfo taskInfo) {
        if (applicationContext.isExiting()) {
            taskInfo.setRunning(false);
            return;
        }
        taskInfo.setRunning(true);
        taskInfo.getTaskExecutor().run(applicationContext);
        taskInfo.setRunning(false);
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(DebbieTaskAction.class);
}
