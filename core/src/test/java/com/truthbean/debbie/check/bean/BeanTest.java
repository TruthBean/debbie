package com.truthbean.debbie.check.bean;

import com.truthbean.debbie.bean.*;
import com.truthbean.debbie.core.ApplicationContext;
import com.truthbean.debbie.proxy.BeanProxyType;
import com.truthbean.debbie.test.annotation.DebbieApplicationTest;
import org.junit.jupiter.api.Test;

/**
 * @author TruthBean
 * @since 0.5.5
 * Created on 2022/04/11 18:08.
 */
@DebbieApplicationTest
public class BeanTest {

    @Test
    public void testSimpleBean(@BeanInject ApplicationContext applicationContext) {
        BeanInfoManager beanInfoManager = applicationContext.getBeanInfoManager();
        GlobalBeanFactory globalBeanFactory = applicationContext.getGlobalBeanFactory();
        beanInfoManager.registerBeanInfo(new SimpleBeanFactory<>(new TestSimpleBean(), TestSimpleBean.class, BeanProxyType.JDK, "emptyBean001", "emptyBean002"));
        Object emptyBean001_1 = globalBeanFactory.factory("emptyBean001");
        System.out.println(emptyBean001_1);
        Object emptyBean001_2 = globalBeanFactory.factory("emptyBean001");
        System.out.println(emptyBean001_2);
        beanInfoManager.registerBeanInfo(new SimpleBeanFactory<>(new TestSimpleBean(), TestSimpleBean.class, BeanProxyType.JDK, "emptyBean002"));
        Object emptyBean002_1 = globalBeanFactory.factory("emptyBean002");
        System.out.println(emptyBean002_1);
        Object emptyBean002_2 = globalBeanFactory.factory("emptyBean002");
        System.out.println(emptyBean002_2);
    }

    @Test
    public void testConfigurationMethodBean(@BeanInject ApplicationContext applicationContext) throws NoSuchMethodException {
        BeanInfoManager beanInfoManager = applicationContext.getBeanInfoManager();
        GlobalBeanFactory globalBeanFactory = applicationContext.getGlobalBeanFactory();
        TestSimpleBean config = new TestSimpleBean();
        ConfigurationMethodBeanFactory<TestSimpleBean, Object> factory = new ConfigurationMethodBeanFactory<>(() -> config, TestSimpleBean.class.getMethod("method"));
        beanInfoManager.registerBeanInfo(factory);
        Object method_1 = globalBeanFactory.factory("method");
        System.out.println(method_1);
        Object method_2 = globalBeanFactory.factory("method");
        System.out.println(method_2);
    }
}
