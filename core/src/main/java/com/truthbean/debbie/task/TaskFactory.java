/**
 * Copyright (c) 2025 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 * http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.task;

import com.truthbean.debbie.bean.*;
import com.truthbean.debbie.concurrent.*;
import com.truthbean.debbie.core.ApplicationContext;
import com.truthbean.debbie.core.ApplicationContextAware;
import com.truthbean.debbie.reflection.*;
import com.truthbean.Logger;
import com.truthbean.debbie.spi.SpiLoader;
import com.truthbean.LoggerFactory;

import java.util.Set;

/**
 * @author TruthBean
 * @since 0.0.2
 */
public class TaskFactory implements TaskRegister, ApplicationContextAware, BeanClosure {

    private final Set<TaskAction> taskActions;
    private final DebbieTaskAction debbieTaskAction;

    TaskFactory() {
        var classLoader = ClassLoaderUtils.getClassLoader(TaskAction.class);
        this.taskActions = SpiLoader.loadProviderSet(TaskAction.class, classLoader);
        this.debbieTaskAction = new DebbieTaskAction();
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) {
        this.debbieTaskAction.setApplicationContext(applicationContext);
        for (TaskAction taskAction : this.taskActions) {
            taskAction.setApplicationContext(applicationContext);
        }
    }

    @Override
    public TaskRegister registerTask(TaskInfo taskInfo) {
        debbieTaskAction.registerTask(taskInfo);
        return this;
    }

    public void prepare() {
        debbieTaskAction.prepare();
        for (TaskAction taskAction : taskActions) {
            taskAction.prepare();
        }
    }

    public void doTask() {
        debbieTaskAction.doTask();
        for (TaskAction taskAction : taskActions) {
            taskAction.doTask();
        }
    }

    @Override
    public void destruct(ApplicationContext applicationContext) {
        synchronized (TaskFactory.class) {
            LOGGER.info("destroy tasks bean");
            debbieTaskAction.stop();
            for (TaskAction taskAction : taskActions) {
                taskAction.stop();
            }
        }
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(TaskFactory.class);
}
