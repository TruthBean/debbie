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
import com.truthbean.core.concurrent.NamedThreadFactory;
import com.truthbean.core.util.StringUtils;
import com.truthbean.debbie.concurrent.PooledExecutor;
import com.truthbean.debbie.concurrent.ScheduledPooledExecutor;
import com.truthbean.debbie.concurrent.ScheduledThreadPooledExecutor;
import com.truthbean.debbie.concurrent.ThreadPooledExecutor;
import com.truthbean.debbie.core.ApplicationContext;
import com.truthbean.logger.util.DateTimeHelper;

import java.time.ZonedDateTime;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.concurrent.ThreadFactory;

/**
 * Base implementation of {@link TaskAction} providing thread-pool-based
 * task execution with support for fixed-rate, delayed and cron-scheduled
 * tasks.
 * <p>
 * Subclasses supply a {@linkplain #getTaskThreadName() thread name} and
 * a {@linkplain #getLogger() logger}. Tasks are collected in
 * {@link #taskList} and dispatched by {@link #doTask()}.
 *
 * @author TruthBean/Rogar·Q
 * @since 0.5.7
 */
public abstract class AbstractTaskAction implements TaskAction {
    /** thread factory for task execution threads */
    private final ThreadFactory namedThreadFactory = new NamedThreadFactory(getTaskThreadName(), true);
    /** single-thread pool for dispatching tasks */
    private final PooledExecutor taskThreadPool = new ThreadPooledExecutor(1, 1, namedThreadFactory);
    /** scheduled pool for fixed-rate / cron tasks, sized to CPU count */
    private final ScheduledPooledExecutor scheduledPooledExecutor = new ScheduledThreadPooledExecutor(Runtime.getRuntime().availableProcessors(), namedThreadFactory);

    /** the application context, set via {@link #setApplicationContext} */
    protected ApplicationContext applicationContext;

    /** registered task descriptors */
    protected final Set<TaskInfo> taskList = new LinkedHashSet<>();

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    /** Returns the thread name prefix for task execution threads. */
    protected abstract String getTaskThreadName();

    /**
     * Dispatches all registered tasks: synchronous tasks run on the
     * {@code taskThreadPool}, async tasks on the global thread pool, and
     * scheduled/cron tasks on the {@code scheduledPooledExecutor}.
     */
    @Override
    public void doTask() {
        final ThreadPooledExecutor executor = applicationContext.getGlobalBeanFactory().factory("threadPooledExecutor");
        final Set<TaskInfo> taskSet = new LinkedHashSet<>(this.taskList);
        taskThreadPool.execute(() -> {
            getLogger().trace("do task....");
            for (TaskInfo taskInfo : taskSet) {
                doTask(executor, taskInfo);
            }
        });
    }

    private void doTask(final ThreadPooledExecutor executor, final TaskInfo taskInfo) {
        DebbieTaskConfig config = taskInfo.getTaskConfig();
        if (!config.isAsync()) {
            doTask(taskInfo, config);
        } else {
            try {
                executor.execute(() -> {
                    try {
                        doTask(taskInfo, config);
                    } catch (Throwable ex) {
                        getLogger().error("", ex);
                    }
                });
            } catch (Exception e) {
                getLogger().error("", e);
            }
        }
    }

    private void doTask(final TaskInfo taskInfo, final DebbieTaskConfig annotation) {
        var fixedRate = annotation.getFixedRate();
        var cron = annotation.getCron();
        long delay = annotation.getInitialDelay();
        if (fixedRate > -1) {
            if (delay <= 0) {
                delay = 0;
            }
            final long finalDelay = delay;
            taskInfo.getTaskRunnableIfPresent(getLogger(), timerTask -> {
                scheduledPooledExecutor.scheduleAtFixedRate(() -> {
                    if (!applicationContext.isExiting()) {
                        timerTask.run(applicationContext);
                    }
                }, finalDelay, fixedRate);
            });
        } else if (fixedRate == -1 && delay > -1) {
            final long finalDelay = delay;
            taskInfo.getTaskRunnableIfPresent(getLogger(), timerTask -> {
                scheduledPooledExecutor.schedule(() -> {
                    if (!applicationContext.isExiting()) {
                        timerTask.run(applicationContext);
                    }
                }, finalDelay);
            });
        } else if (StringUtils.hasText(cron)) {
            // 如果使用quartz的方式，则不用CronExpression的实现
            boolean enable = applicationContext.getDefaultEnvironment().getBooleanValue("debbie.quartz.enable", false);
            if (!enable) {
                var cronExpression = new CronExpression(cron);
                scheduleCronTask(taskInfo, cronExpression);
            }
        } else {
            taskInfo.accept();
        }
    }

    private void scheduleCronTask(TaskInfo taskInfo, CronExpression cronExpression) {
        ZonedDateTime now = ZonedDateTime.now();
        ZonedDateTime nextTime = cronExpression.nextTimeAfter(now);
        if (nextTime == null) {
            getLogger().warn("No next execution time found for cron expression: " + cronExpression);
            return;
        }
        long delay = nextTime.toInstant().toEpochMilli() - now.toInstant().toEpochMilli();
        final long finalDelay = Math.max(delay, 0);
        getLogger().trace(() -> "Schedule cron task '" + taskInfo.getTaskName()
                + "' next execution at " + nextTime.format(DateTimeHelper.LONG_FORMATTER) + " (delay " + finalDelay + "ms)");
        scheduledPooledExecutor.schedule(() -> {
            if (!applicationContext.isExiting()) {
                taskInfo.getTaskRunnableIfPresent(getLogger(), timerTask -> {
                    try {
                        timerTask.run(applicationContext);
                    } catch (Throwable ex) {
                        getLogger().error("cron task(" + taskInfo.getTaskName() + ") error", ex);
                    }
                });
                // Schedule the next execution after this one completes
                scheduleCronTask(taskInfo, cronExpression);
            }
        }, finalDelay);
    }

    /** Shuts down the scheduled and task thread pools. */
    @Override
    public void stop() {
        scheduledPooledExecutor.destroy();
        scheduledPooledExecutor.destroy();
        taskThreadPool.destroy();
    }

    /** Returns the logger for this task action. */
    protected abstract Logger getLogger();
}
