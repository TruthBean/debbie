package com.truthbean.debbie.se;

import com.truthbean.debbie.bean.BeanComponent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;

@BeanComponent
public class SeTestService {

    public String getUuid() {
        for (int i = 0; i < 1000; i++) {
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
