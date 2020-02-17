package com.truthbean.debbie.test;

import com.truthbean.debbie.bean.BeanFactoryHandler;
import com.truthbean.debbie.boot.AbstractApplicationFactory;
import com.truthbean.debbie.boot.DebbieApplication;
import com.truthbean.debbie.task.DebbieTaskFactory;
import com.truthbean.debbie.properties.DebbieConfigurationFactory;
import com.truthbean.debbie.task.DebbieTaskStarter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author truthbean
 * @since 0.0.2
 */
public class MockApplicationFactory extends AbstractApplicationFactory {
    private final Logger logger = LoggerFactory.getLogger(MockApplicationFactory.class);

    @Override
    public DebbieApplication factory(DebbieConfigurationFactory factory, BeanFactoryHandler beanFactoryHandler) {
        return new DebbieApplication() {
            @Override
            protected void start(long beforeStartTime, String... args) {
                DebbieTaskStarter taskStarter = new DebbieTaskStarter();
                taskStarter.start(beanFactoryHandler);
                this.beforeStart(logger, beanFactoryHandler);
                logger.info("application start time spends " + (System.currentTimeMillis() - beforeStartTime) + "ms");
                Runtime.getRuntime().addShutdownHook(new Thread(() -> exit(args)));
                DebbieTaskFactory taskFactory = beanFactoryHandler.factory("taskFactory");
                taskFactory.doTask();
            }

            @Override
            public void exit(String... args) {
                beforeExit(beanFactoryHandler, args);
                logger.info("application exit...");
            }
        };
    }

}