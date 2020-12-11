package com.truthbean.debbie.check.bean;

import com.truthbean.Logger;
import com.truthbean.debbie.bean.*;
import com.truthbean.debbie.bean.inter.Abc;
import com.truthbean.debbie.core.ApplicationContext;
import com.truthbean.debbie.core.ApplicationFactory;
import com.truthbean.logger.LoggerFactory;
import demo.raw.NoBeanTest;
import org.junit.jupiter.api.Test;

/**
 * @author TruthBean/Rogar·Q
 * @since Created on 2020-06-23 23:15.
 */
class InjectedBeanFactoryTest {

    @Test
    void configure() {
        for (int i = 0; i < 2; i++) {
            ApplicationFactory applicationFactory = ApplicationFactory.configure(BeanConfigurationRegisterTest.class);
            applicationFactory.release();
            System.out.println("-------------------------------------------------------------");
        }
    }

    @Test
    void factory() {
        for (int i = 0; i < 10; i++) {
            System.out.println(i);
            ApplicationFactory applicationFactory = ApplicationFactory.configure(BeanConfigurationRegisterTest.class);
            ApplicationContext applicationContext = applicationFactory.getApplicationContext();
            BeanInfoFactory beanInfoFactory = applicationContext.getBeanInfoFactory();
            InjectedBeanFactory injectedBeanFactory = applicationContext.getInjectedBeanFactory();
            GlobalBeanFactory globalBeanFactory = applicationContext.getGlobalBeanFactory();

            BeanInfo<Abc> beanInfo = beanInfoFactory.getBeanInfo("abc", Abc.class, true);
            System.out.println("------------------------------------------------------------------------------");
            Abc factory = globalBeanFactory.factory(Abc.class);
            System.out.println(factory.toString());
            System.out.println("------------------------------------------------------------------------------");

            factory = injectedBeanFactory.factory(beanInfo);
            System.out.println(factory.toString());

            Abc abc = globalBeanFactory.factory(Abc.class);
            System.out.println(abc.toString());
            applicationFactory.release();
        }
    }

    @Test
    void factoryNoBean() {
        for (int i = 0; i < 100; i++) {
            new Thread(() -> {
                var applicationFactory = ApplicationFactory.configure(BeanConfigurationRegisterTest.class);
                var applicationContext = applicationFactory.getApplicationContext();

                var globalBeanFactory = applicationContext.getGlobalBeanFactory();

                var test = globalBeanFactory.factoryByNoBean(NoBeanTest.class);
                applicationFactory.release();
                logger.info(() -> String.valueOf(test));
            }).start();
        }
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            logger.error("", e);
        }
    }

    public static final Logger logger = LoggerFactory.getLogger(InjectedBeanFactoryTest.class);
}