package com.truthbean.debbie.boot;

import com.truthbean.debbie.bean.BeanFactoryHandler;
import com.truthbean.debbie.properties.DebbieConfigurationFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author truthbean
 * @since 0.0.2
 */
public class SimpleApplicationFactory extends AbstractApplicationFactory {
    private final Logger logger = LoggerFactory.getLogger(SimpleApplicationFactory.class);

    @Override
    public DebbieApplication factory(DebbieConfigurationFactory factory, final BeanFactoryHandler beanFactoryHandler,
                                     ClassLoader classLoader) {
        return new AbstractDebbieApplication(logger, beanFactoryHandler) {

            @Override
            protected void start(long beforeStartTime, String... args) {
                logger.info("application start time spends " + (System.currentTimeMillis() - beforeStartTime) + "ms");
            }

            @Override
            public void exit(long beforeStartTime, String... args) {
                if (logger.isTraceEnabled()) {
                    logger.trace("application running time spends " + (System.currentTimeMillis() - beforeStartTime) + "ms");
                }
            }
        };
    }

}
