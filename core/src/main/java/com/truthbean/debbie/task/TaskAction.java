/**
 * Copyright (c) 2026 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 * http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.task;

import com.truthbean.debbie.core.ApplicationContextAware;

/**
 * SPI interface for task actions, loaded via {@code ServiceLoader}.
 * <p>
 * A task action prepares, executes, and stops a set of scheduled or
 * on-demand tasks within the application context.
 *
 * @author TruthBean/Rogar·Q
 * @since 0.1.0
 * Created on 2020-08-11 11:18
 */
public interface TaskAction extends ApplicationContextAware {

    /** Prepares tasks before execution (e.g. scanning, registration). */
    void prepare();

    /** Executes all registered tasks. */
    void doTask();

    /** Stops all running tasks and releases resources. */
    void stop();
}
