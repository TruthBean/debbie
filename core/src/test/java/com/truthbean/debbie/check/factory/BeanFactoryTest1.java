package com.truthbean.debbie.check.factory;

import com.truthbean.Console;
import com.truthbean.debbie.bean.BeanInfo;
import com.truthbean.debbie.bean.BeanInfoManager;
import com.truthbean.debbie.bean.BeanInjection;
import com.truthbean.debbie.bean.GlobalBeanFactory;
import com.truthbean.debbie.boot.ApplicationBootContext;
import com.truthbean.debbie.boot.DebbieApplication;
import com.truthbean.debbie.check.bean.TestSimpleBean;
import com.truthbean.debbie.core.ApplicationContext;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Supplier;

/**
 * @author TruthBean/Rogar·Q
 * @since 0.5.5
 */
public class BeanFactoryTest1 {

    public static void main(String[] args) {
        DebbieApplication.create(args)
                .then(context -> inject1AndFactory(context));
    }

    private static void inject1AndFactory(ApplicationBootContext applicationContext) {
        inject1(applicationContext);
        factory(applicationContext.getGlobalBeanFactory());
    }

    private static void inject1(ApplicationBootContext applicationContext) {
        BeanInfo<TestSimpleBean> beanInfo = new BeanInfo<>() {
            @Override
            public Class<?> getBeanClass() {
                return TestSimpleBean.class;
            }

            @Override
            public Set<String> getAllName() {
                Set<String> names = new HashSet<>();
                names.add("testSimpleBean1");
                return names;
            }

            @Override
            public Supplier<TestSimpleBean> supply(ApplicationContext context) {
                return TestSimpleBean::new;
            }

            @Override
            public BeanInfo<TestSimpleBean> copy() {
                return this;
            }
        };
        BeanInfoManager beanInfoManager = applicationContext.getBeanInfoManager();
        beanInfoManager.registerBeanInfo(beanInfo);
    }

    private static void factory(GlobalBeanFactory globalBeanFactory) {
        Console.println("1---------------------------------------------");
        TestSimpleBean testSimpleBean1 = globalBeanFactory.factory("testSimpleBean1");
        Console.println(testSimpleBean1);
        Console.println("2---------------------------------------------");
        TestSimpleBean testSimpleBean2 = globalBeanFactory.factory(TestSimpleBean.class);
        Console.println(testSimpleBean2);
        Console.println("3---------------------------------------------");
        BeanInjection<TestSimpleBean> beanInjection = new BeanInjection<>(TestSimpleBean.class);
        TestSimpleBean testSimpleBean3 = globalBeanFactory.factory(beanInjection);
        Console.println(testSimpleBean3);
        Console.println("4---------------------------------------------");
        TestSimpleBean testSimpleBean4 = globalBeanFactory.factoryWithoutProxy(TestSimpleBean.class);
        Console.println(testSimpleBean4);
        Console.println("5---------------------------------------------");
        TestSimpleBean testSimpleBean5 = globalBeanFactory.factory("testSimpleBean1", TestSimpleBean.class);
        Console.println(testSimpleBean5);
        Console.println("---------------------------------------------");
    }
}
