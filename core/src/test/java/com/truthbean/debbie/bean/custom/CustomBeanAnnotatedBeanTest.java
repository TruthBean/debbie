package com.truthbean.debbie.bean.custom;

import com.truthbean.Logger;
import com.truthbean.debbie.bean.BeanInject;
import com.truthbean.debbie.bean.GlobalBeanFactory;
import com.truthbean.debbie.boot.DebbieApplication;
import com.truthbean.debbie.core.ApplicationContext;
import com.truthbean.debbie.core.ApplicationFactory;
import com.truthbean.LoggerFactory;
import com.truthbean.debbie.lang.Callable;
import com.truthbean.debbie.lang.Callback;
import com.truthbean.debbie.reflection.ReflectionConfigurer;
import com.truthbean.debbie.task.DebbieTask;
import com.truthbean.debbie.test.annotation.DebbieApplicationTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * @author TruthBean/Rogar·Q
 * @since Created on 2020-07-03 17:14.
 */
@DebbieApplicationTest
class CustomBeanAnnotatedBeanTest {

    static {
        System.setProperty(DebbieApplication.DISABLE_DEBBIE, "false");
        System.setProperty("logging.level.root", "debug");
        System.setProperty("logging.level.com.truthbean", "trace");
        System.setProperty(ReflectionConfigurer.ENABLE_KEY, "true");
        System.setProperty("com.truthbean.debbie.core.test.enable", "true");
    }

    @Inject
    private CustomBeanAnnotatedBean customBeanAnnotatedBean;

    @DebbieTask
    public void task() {
        System.out.println(customBeanAnnotatedBean.getA());
    }

    public static void main(String[] args) {
        CustomBeanAnnotatedBeanTest test = new CustomBeanAnnotatedBeanTest();
        DebbieApplication.run(test, args);
    }

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
                try {
                    LOGGER.debug("???????????????????????????????????????????????????????????????????????????????????????????????");
                    ApplicationFactory applicationFactory = ApplicationFactory.create(CustomBeanAnnotatedBeanTest.class);
                    ApplicationContext context = applicationFactory.getApplicationContext();
                    GlobalBeanFactory globalBeanFactory = context.getGlobalBeanFactory();
                    CustomBeanAnnotatedBean bean = globalBeanFactory.factory(CustomBeanAnnotatedBean.class);
                    LOGGER.debug(String.valueOf(bean.getA()));
                    applicationFactory.release();
                } catch (Exception e) {
                    LOGGER.error(e.getMessage(), e);
                }
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