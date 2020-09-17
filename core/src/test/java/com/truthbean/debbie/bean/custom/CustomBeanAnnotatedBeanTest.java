package com.truthbean.debbie.bean.custom;

import com.truthbean.Logger;
import com.truthbean.debbie.bean.GlobalBeanFactory;
import com.truthbean.debbie.core.ApplicationContext;
import com.truthbean.debbie.core.ApplicationFactory;
import com.truthbean.logger.LoggerFactory;
import org.junit.jupiter.api.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * @author TruthBean/Rogar·Q
 * @since Created on 2020-07-03 17:14.
 */
class CustomBeanAnnotatedBeanTest {

    @Test
    void getA() {
        // ApplicationFactory applicationFactory = ApplicationFactory.configure(CustomBeanAnnotatedBeanTest.class);
        // ApplicationContext context = applicationFactory.getApplicationContext();
        // GlobalBeanFactory globalBeanFactory = context.getGlobalBeanFactory();
        final var total = 10;
        final ExecutorService service = Executors.newFixedThreadPool(total);
        final CountDownLatch countDownLatch = new CountDownLatch(total);
        for (int i = 0; i < total; i++) {
            service.execute(() -> {
                LOGGER.debug("???????????????????????????????????????????????????????????????????????????????????????????????");
                ApplicationFactory applicationFactory = ApplicationFactory.configure(CustomBeanAnnotatedBeanTest.class);
                ApplicationContext context = applicationFactory.getApplicationContext();
                GlobalBeanFactory globalBeanFactory = context.getGlobalBeanFactory();
                CustomBeanAnnotatedBean bean = globalBeanFactory.factory(CustomBeanAnnotatedBean.class);
                LOGGER.debug(String.valueOf(bean.getA()));
                applicationFactory.release();
                countDownLatch.countDown();
            });
        }

        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            LOGGER.error("", e);
        }
        service.shutdown();
        LOGGER.info("-----------------------------------------------------------------------------------------");

    }

    public static final Logger LOGGER = LoggerFactory.getLogger(CustomBeanAnnotatedBeanTest.class);
}