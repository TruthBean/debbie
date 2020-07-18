/**
 * Copyright (c) 2020 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *         http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.concurrent;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author truthbean/Rogar·Q
 * @since 0.0.2
 * Created on 2020-04-03 14:04.
 */
public class NamedThreadFactory implements ThreadFactory {
    private static final AtomicInteger poolNumber = new AtomicInteger(1);
    private final ThreadGroup group;
    private final AtomicInteger threadNumber = new AtomicInteger(1);
    private final String namePrefix;
    private final boolean fixedName;

    private Boolean daemon;
    private Integer priority;

    public NamedThreadFactory() {
        SecurityManager s = System.getSecurityManager();
        group = (s != null) ? s.getThreadGroup() : Thread.currentThread().getThreadGroup();
        namePrefix = "debbie-" + poolNumber.getAndIncrement() + "-thread-";
        this.fixedName = false;
    }

    public NamedThreadFactory(String prefix) {
        this(prefix, false);
    }

    public NamedThreadFactory(String name, boolean fixedName) {
        SecurityManager s = System.getSecurityManager();
        group = (s != null) ? s.getThreadGroup() : Thread.currentThread().getThreadGroup();
        if (fixedName) {
            if (name == null || name.isBlank())
                name = "debbie-fixedName";
            namePrefix = name;
        } else {
            if (name == null || name.isBlank())
                name = "debbie-";
            namePrefix = name + poolNumber.getAndIncrement() + "-thread-";
        }
        this.fixedName = fixedName;
    }

    public NamedThreadFactory(String prefix, ThreadGroup group) {
        this(prefix, group, false);
    }

    public NamedThreadFactory(String name, ThreadGroup group, boolean fixedName) {
        if (group == null) {
            SecurityManager s = System.getSecurityManager();
            this.group = (s != null) ? s.getThreadGroup() : Thread.currentThread().getThreadGroup();
        } else {
            this.group = group;
        }
        if (fixedName) {
            if (name == null || name.isBlank())
                name = "debbie-fixedName";
            namePrefix = name;
        } else {
            if (name == null || name.isBlank())
                name = "debbie-";
            namePrefix = name + poolNumber.getAndIncrement() + "-thread-";
        }
        this.fixedName = fixedName;
    }

    public Boolean getDaemon() {
        return daemon;
    }

    public void setDaemon(Boolean daemon) {
        this.daemon = daemon;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }

    @Override
    public Thread newThread(Runnable r) {
        Thread thread;
        if (fixedName) {
            thread = new Thread(group, r, namePrefix, 0);
        } else
            thread = new Thread(group, r, namePrefix + threadNumber.getAndIncrement(), 0);
        if (daemon != null) {
            thread.setDaemon(daemon);
        } else if (thread.isDaemon()) {
            thread.setDaemon(false);
        }

        if (priority != null) {
            thread.setPriority(priority);
        } else if (thread.getPriority() != Thread.NORM_PRIORITY)
            thread.setPriority(Thread.NORM_PRIORITY);
        return thread;
    }
}
