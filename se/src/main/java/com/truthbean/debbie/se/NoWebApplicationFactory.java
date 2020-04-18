package com.truthbean.debbie.se;

import com.truthbean.debbie.bean.BeanFactoryHandler;
import com.truthbean.debbie.boot.AbstractApplicationFactory;
import com.truthbean.debbie.boot.DebbieApplication;
import com.truthbean.debbie.properties.DebbieConfigurationFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author truthbean
 * @since 0.0.2
 */
public class NoWebApplicationFactory extends AbstractApplicationFactory {
    private final Logger logger = LoggerFactory.getLogger(NoWebApplicationFactory.class);

    @Override
    public DebbieApplication factory(DebbieConfigurationFactory factory, final BeanFactoryHandler beanFactoryHandler,
                                     ClassLoader classLoader) {
        return new DebbieApplication() {
            private volatile boolean exiting = false;

            @Override
            protected void start(long beforeStartTime, String... args) {
                this.beforeStart(logger, beanFactoryHandler);
                logger.info("application start time spends " + (System.currentTimeMillis() - beforeStartTime) + "ms");
                Runtime.getRuntime().addShutdownHook(new Thread(() -> exit(args)));
            }

            @Override
            public void exit(String... args) {
                while (!exiting) {
                    exiting = true;
                    logger.info("application exiting...");
                    beforeExit(beanFactoryHandler, args);
                }
            }
        };
    }

}
