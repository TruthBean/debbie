/**
 * Copyright (c) 2020 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 * http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.task;

import com.truthbean.Logger;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;

/**
 * @author TruthBean/Rogar·Q
 * @since 0.2.0
 * Created on 2020-12-21 11:26
 */
public class TaskInfo {
    private DebbieTaskConfig taskConfig;
    private TaskRunnable taskRunnable;

    private Consumer<TaskInfo> consumer;

    public TaskInfo() {
    }

    public TaskInfo(TaskRunnable taskRunnable, DebbieTaskConfig taskConfig) {
        this.taskRunnable = taskRunnable;
        this.taskConfig = taskConfig;
    }

    public DebbieTaskConfig getTaskConfig() {
        return taskConfig;
    }

    public void setTaskConfig(DebbieTaskConfig taskConfig) {
        this.taskConfig = taskConfig;
    }

    public TaskRunnable getTaskExecutor() {
        return taskRunnable;
    }

    public void setTaskExecutor(TaskRunnable taskRunnable) {
        this.taskRunnable = taskRunnable;
    }

    public Consumer<TaskInfo> getConsumer() {
        return consumer;
    }

    public void setConsumer(Consumer<TaskInfo> consumer) {
        this.consumer = consumer;
    }

    public void accept() {
        this.consumer.accept(this);
    }

    public Optional<TaskRunnable> getTaskRunnable() {
        return Optional.ofNullable(this.taskRunnable);
    }

    public void setTaskRunnable(TaskRunnable taskRunnable) {
        this.taskRunnable = taskRunnable;
    }

    public void setTaskRunnableIfPresent(Logger logger) {
        var fixedRate = taskConfig.getFixedRate();
        if (fixedRate > DebbieTask.NO_RATE) {
            this.taskRunnable = new Task(logger);
        }
    }

    public void getTaskRunnableIfPresent(Consumer<Runnable> action) {
        if (taskRunnable != null) {
            action.accept(taskRunnable);
        }
    }

    public void getTaskRunnableIfPresent(Logger logger, Consumer<Runnable> action) {
        if (taskRunnable != null) {
            action.accept(taskRunnable);
        } else {
            var fixedRate = taskConfig.getFixedRate();
            if (fixedRate > DebbieTask.NO_RATE) {
                this.taskRunnable = new Task(logger);
                action.accept(taskRunnable);
            }
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TaskInfo taskInfo = (TaskInfo) o;
        return Objects.equals(taskRunnable, taskInfo.taskRunnable);
    }

    @Override
    public int hashCode() {
        return Objects.hash(taskRunnable);
    }

    public class Task implements TaskRunnable {

        public final Logger logger;
        public Task(Logger logger) {
            this.logger = logger;
        }

        @Override
        public String getName() {
            return "taskRunnable";
        }

        @Override
        public void run() {
            try {
                TaskInfo.this.accept();
            } catch (Throwable ex) {
                logger.error("error in timerTask. ", ex);
            }
        }
    }
}
