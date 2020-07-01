/**
 * Copyright (c) 2020 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *         http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.boot;

import com.truthbean.debbie.bean.DebbieApplicationContext;
import com.truthbean.debbie.properties.DebbieConfigurationFactory;
import com.truthbean.Logger;
import com.truthbean.logger.LoggerFactory;

import java.lang.management.ManagementFactory;

/**
 * @author truthbean
 * @since 0.0.2
 */
public class SimpleApplicationFactory extends AbstractApplicationFactory {
    private final Logger logger = LoggerFactory.getLogger(SimpleApplicationFactory.class);

    @Override
    public DebbieApplication factory(DebbieConfigurationFactory factory, final DebbieApplicationContext applicationContext,
                                     ClassLoader classLoader) {
        return new AbstractDebbieApplication(logger, applicationContext) {

            @Override
            protected void start(long beforeStartTime, String... args) {
                double uptime = ManagementFactory.getRuntimeMXBean().getUptime();
                logger.info(() -> "application start time spends " + (System.currentTimeMillis() - beforeStartTime) +
                        "ms ( JVM running for "  + uptime + "ms )");
                postBeforeStart();
            }

            @Override
            public void exit(long beforeStartTime, String... args) {
                logger.trace(() -> "application running time spends " + (System.currentTimeMillis() - beforeStartTime) + "ms");
            }
        };
    }

}
