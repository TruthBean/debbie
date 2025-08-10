/**
 * Copyright (c) 2025 TruthBean(Rogar·Q)
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

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.concurrent.ThreadFactory;

/**
 * @author TruthBean/Rogar·Q
 * @since 0.5.7
 */
public abstract class AbstractTaskAction implements TaskAction {
    private final ThreadFactory namedThreadFactory = new NamedThreadFactory(getTaskThreadName(), true);
    private final PooledExecutor taskThreadPool = new ThreadPooledExecutor(1, 1, namedThreadFactory);
    private final ScheduledPooledExecutor scheduledPooledExecutor = new ScheduledThreadPooledExecutor(Runtime.getRuntime().availableProcessors(), namedThreadFactory);

    protected ApplicationContext applicationContext;

    protected final Set<TaskInfo> taskList = new LinkedHashSet<>();

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    protected abstract String getTaskThreadName();

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
            // todo cron
        } else {
            taskInfo.accept();
        }
    }

    @Override
    public void stop() {
        scheduledPooledExecutor.destroy();
        scheduledPooledExecutor.destroy();
        taskThreadPool.destroy();
    }

    protected abstract Logger getLogger();
}
