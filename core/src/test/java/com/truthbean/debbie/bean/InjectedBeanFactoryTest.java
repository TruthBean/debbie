package com.truthbean.debbie.bean;

import com.truthbean.debbie.bean.inter.Abc;
import com.truthbean.debbie.boot.DebbieApplicationFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * @author TruthBean/Rogar·Q
 * @since Created on 2020-06-23 23:15.
 */
class InjectedBeanFactoryTest {

    private BeanFactoryContext applicationContext;
    private InjectedBeanFactory injectedBeanFactory;

    @BeforeEach
    public void before() {
        applicationContext = DebbieApplicationFactory.configure(BeanConfigurationRegisterTest.class);
        injectedBeanFactory = new InjectedBeanFactory();
        injectedBeanFactory.setBeanFactoryContext(applicationContext);
    }

    @Test
    void factory() {
        DebbieBeanInfo<Abc> beanInfo = applicationContext.getBeanInfo("abc", Abc.class, true);
        System.out.println("------------------------------------------------------------------------------");
        // Abc factory1 = applicationContext.factory(Abc.class);
        // System.out.println(factory1.toString());
        System.out.println("------------------------------------------------------------------------------");

        Abc factory = injectedBeanFactory.factory(beanInfo);
        System.out.println(factory.toString());

        // Abc abc = applicationContext.factoryBean(Abc.class);
        // System.out.println(abc.toString());
    }
}