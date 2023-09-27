package com.truthbean.debbie.check.bean;

import com.truthbean.debbie.bean.BeanInfoManager;
import com.truthbean.debbie.bean.BeanInject;
import com.truthbean.debbie.bean.ConfigurationMethodBeanFactory;
import com.truthbean.debbie.core.ApplicationContext;
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
        applicationContext.registerSingleBean(TestSimpleBean.class, new TestSimpleBean(), "emptyBean001", "emptyBean002");
        Object emptyBean001_1 = applicationContext.factory("emptyBean001");
        System.out.println(emptyBean001_1);
        Object emptyBean001_2 = applicationContext.factory("emptyBean001");
        System.out.println(emptyBean001_2);
        applicationContext.registerSingleBean(TestSimpleBean.class, new TestSimpleBean(), "emptyBean002");
        Object emptyBean002_1 = applicationContext.factory("emptyBean002");
        System.out.println(emptyBean002_1);
        Object emptyBean002_2 = applicationContext.factory("emptyBean002");
        System.out.println(emptyBean002_2);
    }

    @Test
    public void testConfigurationMethodBean(@BeanInject ApplicationContext applicationContext) throws NoSuchMethodException {
        BeanInfoManager beanInfoManager = applicationContext.getBeanInfoManager();
        TestSimpleBean config = new TestSimpleBean();
        ConfigurationMethodBeanFactory<TestSimpleBean, Object> factory = new ConfigurationMethodBeanFactory<>(() -> config, TestSimpleBean.class.getMethod("method"));
        beanInfoManager.registerBeanInfo(factory);
        Object method_1 = applicationContext.factory("method");
        System.out.println(method_1);
        Object method_2 = applicationContext.factory("method");
        System.out.println(method_2);
    }
}
