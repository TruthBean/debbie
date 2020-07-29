package com.truthbean.debbie.check.bean;

import com.truthbean.debbie.bean.BeanInfoFactory;
import com.truthbean.debbie.bean.DebbieBeanInfo;
import com.truthbean.debbie.bean.GlobalBeanFactory;
import com.truthbean.debbie.bean.InjectedBeanFactory;
import com.truthbean.debbie.bean.inter.Abc;
import com.truthbean.debbie.core.ApplicationContext;
import com.truthbean.debbie.core.ApplicationFactory;
import demo.raw.NoBeanTest;
import org.junit.jupiter.api.Test;

/**
 * @author TruthBean/Rogar·Q
 * @since Created on 2020-06-23 23:15.
 */
class InjectedBeanFactoryTest {

    @Test
    void factory() {
        ApplicationFactory applicationFactory = ApplicationFactory.configure(BeanConfigurationRegisterTest.class);
        ApplicationContext applicationContext = applicationFactory.getApplicationContext();
        BeanInfoFactory beanInfoFactory = applicationContext.getDebbieBeanInfoFactory();
        InjectedBeanFactory injectedBeanFactory = applicationContext.getInjectedBeanFactory();
        GlobalBeanFactory globalBeanFactory = applicationContext.getGlobalBeanFactory();

        DebbieBeanInfo<Abc> beanInfo = beanInfoFactory.getBeanInfo("abc", Abc.class, true);
        System.out.println("------------------------------------------------------------------------------");
        Abc factory = globalBeanFactory.factory(Abc.class);
        System.out.println(factory.toString());
        System.out.println("------------------------------------------------------------------------------");

        factory = injectedBeanFactory.factory(beanInfo);
        System.out.println(factory.toString());

        Abc abc = globalBeanFactory.factory(Abc.class);
        System.out.println(abc.toString());
    }

    @Test
    void factoryNoBean() {
        ApplicationFactory applicationFactory = ApplicationFactory.configure(BeanConfigurationRegisterTest.class);
        ApplicationContext applicationContext = applicationFactory.getApplicationContext();

        GlobalBeanFactory globalBeanFactory = applicationContext.getGlobalBeanFactory();

        NoBeanTest test = globalBeanFactory.factoryByNoBean(NoBeanTest.class);
        System.out.println(test);
    }
}