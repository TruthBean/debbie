package com.truthbean.debbie.bean.custom;

import com.truthbean.debbie.bean.GlobalBeanFactory;
import com.truthbean.debbie.core.ApplicationContext;
import com.truthbean.debbie.core.ApplicationFactory;
import org.junit.jupiter.api.Test;

/**
 * @author TruthBean/Rogar·Q
 * @since Created on 2020-07-03 17:14.
 */
class CustomBeanAnnotatedBeanTest {

    @Test
    void getA() {
//        DebbieApplicationFactory applicationFactory = DebbieApplicationFactory.configure(CustomBeanAnnotatedBeanTest.class);
//        GlobalBeanFactory globalBeanFactory = applicationFactory.getGlobalBeanFactory();
        for (int i = 0; i < 10; i++) {
            new Thread(() -> {
                ApplicationFactory applicationFactory = ApplicationFactory.configure(CustomBeanAnnotatedBeanTest.class);
                ApplicationContext context = applicationFactory.getApplicationContext();
                GlobalBeanFactory globalBeanFactory = context.getGlobalBeanFactory();
                CustomBeanAnnotatedBean bean = globalBeanFactory.factory(CustomBeanAnnotatedBean.class);
                System.out.println(bean.getA());
            }).start();
        }

        try {
            Thread.sleep(4000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("-----------------------------------------------------------------------------------------");

    }
}