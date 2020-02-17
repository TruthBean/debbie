package com.truthbean.debbie.se;

import com.truthbean.debbie.bean.BeanFactoryHandler;
import com.truthbean.debbie.boot.AbstractApplicationFactory;
import com.truthbean.debbie.boot.DebbieApplication;
import com.truthbean.debbie.properties.DebbieConfigurationFactory;
import com.truthbean.debbie.task.DebbieTaskFactory;
import com.truthbean.debbie.task.DebbieTaskStarter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author truthbean
 * @since 0.0.2
 */
public class NoWebApplicationFactory extends AbstractApplicationFactory {
    private final Logger logger = LoggerFactory.getLogger(NoWebApplicationFactory.class);

    @Override
    public DebbieApplication factory(DebbieConfigurationFactory factory, BeanFactoryHandler beanFactoryHandler) {
        return new DebbieApplication() {
            private volatile DebbieTaskFactory taskFactory;
            @Override
            protected void start(long beforeStartTime, String... args) {
                new DebbieTaskStarter().start(beanFactoryHandler);
                this.beforeStart(logger, beanFactoryHandler);
                logger.info("application start time spends " + (System.currentTimeMillis() - beforeStartTime) + "ms");
                Runtime.getRuntime().addShutdownHook(new Thread(() -> exit(args)));

                taskFactory = beanFactoryHandler.factory("taskFactory");
                taskFactory.doTask();
            }

            @Override
            public void exit(String... args) {
                while (taskFactory.isTaskRunning()) {
                    // do nothing
                }
                beforeExit(beanFactoryHandler, args);
                logger.info("application exit...");
            }
        };
    }

}
