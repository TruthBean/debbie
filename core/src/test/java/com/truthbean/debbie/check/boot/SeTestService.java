package com.truthbean.debbie.check.boot;

import com.truthbean.debbie.bean.BeanComponent;
import com.truthbean.Logger;
import com.truthbean.logger.LoggerFactory;

import java.util.UUID;

@BeanComponent
public class SeTestService {

    public String getUuid() {
        for (int i = 0; i < 10; i++) {
            /*try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }*/
            logger.info(String.valueOf(i));
        }
        return UUID.randomUUID().toString();
    }

    private static final Logger logger = LoggerFactory.getLogger(SeTestRunner.class);
}
