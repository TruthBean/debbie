package com.truthbean.debbie.bean;

import com.truthbean.debbie.bean.inter.Abc;
import com.truthbean.debbie.boot.DebbieApplicationFactory;
import demo.raw.NoBeanTest;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * @author TruthBean/Rogar·Q
 * @since Created on 2020-06-23 23:15.
 */
class InjectedBeanFactoryTest {

    @Test
    void factory() {
        DebbieApplicationContext applicationContext = DebbieApplicationFactory.configure(BeanConfigurationRegisterTest.class);
        DebbieBeanInfoFactory beanInfoFactory = applicationContext.getDebbieBeanInfoFactory();
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
        DebbieApplicationContext applicationContext = DebbieApplicationFactory.configure(BeanConfigurationRegisterTest.class);
        GlobalBeanFactory globalBeanFactory = applicationContext.getGlobalBeanFactory();

        NoBeanTest test = globalBeanFactory.factoryByNoBean(NoBeanTest.class);
        System.out.println(test);
    }
}