package com.truthbean.debbie.boot;

import com.truthbean.debbie.bean.BeanComponent;
import com.truthbean.debbie.bean.BeanInject;
import com.truthbean.debbie.bean.BeanType;
import com.truthbean.debbie.task.DebbieTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@BeanComponent(lazy = false, type = BeanType.SINGLETON)
public class SeTestRunner {

    @BeanInject
    private SeTestService seTestService;

    @DebbieTask(async = false)
    public void printId() {
        for (int i = 0; i < 10; i++) {
            logger.info(seTestService.getUuid());
        }
    }

    @DebbieTask(async = true)
    public void sleep() {
        for (int i = 0; i < 10; i++) {
            logger.info("sleep : " + i);
        }
    }

    private static final Logger logger = LoggerFactory.getLogger(SeTestRunner.class);
}
