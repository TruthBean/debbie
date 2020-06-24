package com.truthbean.debbie.bean;

import com.truthbean.debbie.bean.inter.Abc;
import com.truthbean.debbie.bean.inter.AbcImpl;
import com.truthbean.debbie.bean.inter.C;
import com.truthbean.debbie.bean.inter.DemoBeanComponent;
import com.truthbean.debbie.boot.DebbieApplicationFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author TruthBean/Rogar·Q
 * @since Created on 2020-06-23 23:15.
 */
class InjectedBeanFactoryTest {

    private BeanFactoryHandler beanFactoryHandler;
    private InjectedBeanFactory injectedBeanFactory;
    private BeanDependenceProcessor beanDependenceProcessor;

    @BeforeEach
    public void before() {
        beanFactoryHandler = DebbieApplicationFactory.configure(BeanConfigurationRegisterTest.class);
        injectedBeanFactory = new InjectedBeanFactory();
        injectedBeanFactory.setBeanFactoryHandler(beanFactoryHandler);
        beanDependenceProcessor = new BeanDependenceProcessor();
        beanDependenceProcessor.setBeanFactoryHandler(beanFactoryHandler);
    }

    @Test
    void factory() {
        DebbieBeanInfo<Abc> beanInfo = beanFactoryHandler.getBeanInfo("abc", Abc.class, true);
        Abc factory = injectedBeanFactory.factory(beanInfo, beanDependenceProcessor);
        System.out.println(factory.toString());
    }
}