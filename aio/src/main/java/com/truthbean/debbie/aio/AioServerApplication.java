/**
 * Copyright (c) 2023 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 * http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.aio;

import com.truthbean.debbie.bean.BeanFactory;
import com.truthbean.debbie.boot.ApplicationArgs;
import com.truthbean.debbie.boot.DebbieApplication;
import com.truthbean.core.concurrent.NamedThreadFactory;
import com.truthbean.debbie.concurrent.PooledExecutor;
import com.truthbean.core.concurrent.ThreadLoggerUncaughtExceptionHandler;
import com.truthbean.debbie.concurrent.ThreadPooledExecutor;
import com.truthbean.debbie.core.ApplicationContext;
import com.truthbean.debbie.environment.Environment;
import com.truthbean.debbie.properties.PropertiesConfigurationBeanFactory;
import com.truthbean.debbie.server.AbstractWebServerApplication;

import com.truthbean.Logger;
import com.truthbean.LoggerFactory;

import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.*;

/**
 * @author TruthBean/Rogar·Q
 * @since 0.0.2
 * Created on 2019-12-17 19:55
 */
public class AioServerApplication extends AbstractWebServerApplication {

    private final ConcurrentMap<String, RealAioServerRunner> runnerMap = new ConcurrentHashMap<>();

    @Override
    public boolean isEnable(Environment environment) {
        return super.isEnable(environment) && environment.getBooleanValue(AioServerProperties.ENABLE_KEY, true);
    }

    @SuppressWarnings("Unchecked")
    @Override
    public DebbieApplication init(ApplicationContext applicationContext, ClassLoader classLoader) {
        LOGGER.info("com.truthbean.debbie.aio.AioServerApplication is not enable.");
        BeanFactory<AioServerConfiguration> beanFactory =
                applicationContext.getBeanInfoManager().getBeanFactory(null, AioServerConfiguration.class, false);
        if (beanFactory == null) {
            return null;
        }
        if (beanFactory instanceof PropertiesConfigurationBeanFactory<?, ?> propertiesConfigurationBeanFactory) {
            Collection<?> configurations = propertiesConfigurationBeanFactory.factoryBeans(applicationContext);
            for (Object configuration : configurations) {
                AioServerConfiguration aioServerConfiguration = (AioServerConfiguration) configuration;
                if (aioServerConfiguration.isEnable()) {
                    runnerMap.put(aioServerConfiguration.getUuid(), new RealAioServerRunner(applicationContext, aioServerConfiguration)
                            .init(applicationContext, aioServerConfiguration));
                }
            }
        }

        super.setLogger(LOGGER);
        return this;
    }

    private final ThreadFactory namedThreadFactory = new NamedThreadFactory("aio-server-application-")
            .setUncaughtExceptionHandler(new ThreadLoggerUncaughtExceptionHandler());
    private final PooledExecutor singleThreadPool = new ThreadPooledExecutor(1, 1, namedThreadFactory);

    @Override
    protected void start(Instant beforeStartTime, ApplicationArgs args) {
        runnerMap.forEach((uuid, runner) -> {
            runner.printMessage((configuration) -> {
                LOGGER.debug(() -> "aio server config(" + configuration.getName() + ") uri: http://" + configuration.getHost() + ":" + configuration.getPort());
                printlnWebUrl(LOGGER, configuration.getPort());
            });
            printStartTime();
            postBeforeStart();
            singleThreadPool.execute(runner);
        });
    }

    @Override
    protected void exit(Instant beforeStartTime, ApplicationArgs args) {
        LOGGER.debug(() -> "destroy running thread");
        printExitTime();
        singleThreadPool.destroy();
        runnerMap.clear();
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(AioServerApplication.class);
}
