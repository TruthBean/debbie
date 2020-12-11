/**
 * Copyright (c) 2020 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *         http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.internal;

import com.truthbean.Logger;
import com.truthbean.debbie.boot.AbstractApplication;
import com.truthbean.debbie.boot.DebbieApplication;
import com.truthbean.debbie.core.ApplicationContext;
import com.truthbean.debbie.properties.DebbieConfigurationCenter;
import com.truthbean.logger.LoggerFactory;

import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.time.Duration;
import java.time.Instant;

/**
 * @author truthbean
 * @since 0.0.2
 */
class SimpleApplicationFactory extends AbstractApplication {
    private final Logger logger = LoggerFactory.getLogger(SimpleApplicationFactory.class);

    SimpleApplicationFactory() {
        super();
    }

    @Override
    public DebbieApplication init(DebbieConfigurationCenter configurationCenter, ApplicationContext applicationContext, ClassLoader classLoader) {
        super.setLogger(logger);
        return this;
    }

    @Override
    protected void start(Instant beforeStartTime, String... args) {
        super.printStartTime();
        postBeforeStart();
    }

    @Override
    public void exit(Instant beforeStartTime, String... args) {
        super.printExitTime();
    }
}
