package com.truthbean.debbie.bean;

import com.truthbean.debbie.bean.inter.Abc;
import com.truthbean.debbie.boot.DebbieApplicationFactory;
import demo.raw.NoBeanTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * @author TruthBean/Rogar·Q
 * @since Created on 2020-06-23 23:15.
 */
class InjectedBeanFactoryTest {

    private DebbieBeanInfoFactory beanInfoFactory;
    private InjectedBeanFactory injectedBeanFactory;
    private GlobalBeanFactory globalBeanFactory;

    @BeforeEach
    public void before() {
        DebbieApplicationContext applicationContext = DebbieApplicationFactory.configure(BeanConfigurationRegisterTest.class);
        beanInfoFactory = applicationContext.getDebbieBeanInfoFactory();
        injectedBeanFactory = applicationContext.getInjectedBeanFactory();
        globalBeanFactory = applicationContext.getGlobalBeanFactory();
    }

    @Test
    void factory() {
        DebbieBeanInfo<Abc> beanInfo = beanInfoFactory.getBeanInfo("abc", Abc.class, true);
        System.out.println("------------------------------------------------------------------------------");
        // Abc factory1 = applicationContext.factory(Abc.class);
        // System.out.println(factory1.toString());
        System.out.println("------------------------------------------------------------------------------");

        Abc factory = injectedBeanFactory.factory(beanInfo);
        System.out.println(factory.toString());

        // Abc abc = applicationContext.factoryBean(Abc.class);
        // System.out.println(abc.toString());
    }

    @Test
    void factoryNoBean() {
        NoBeanTest test = globalBeanFactory.factoryByNoBean(NoBeanTest.class);
        System.out.println(test);
    }
}