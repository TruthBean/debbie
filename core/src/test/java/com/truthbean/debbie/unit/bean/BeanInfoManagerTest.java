package com.truthbean.debbie.unit.bean;

import com.truthbean.debbie.bean.BeanFactory;
import com.truthbean.debbie.bean.BeanInfo;
import com.truthbean.debbie.bean.BeanInfoManager;
import com.truthbean.debbie.boot.ApplicationBootContext;
import com.truthbean.debbie.boot.DebbieApplication;
import com.truthbean.debbie.check.bean.TestBean;
import com.truthbean.debbie.check.bean.TestBeanInterface1;
import com.truthbean.debbie.check.bean.TestBeanInterface2;
import com.truthbean.debbie.check.bean.TestSimpleBean;
import com.truthbean.debbie.core.ApplicationContext;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Supplier;

/**
 * @author TruthBean/Rogar·Q
 * @since 0.5.5
 */
public class BeanInfoManagerTest {

    private void inject1(ApplicationBootContext applicationContext) {
        BeanInfo<TestSimpleBean> beanInfo1 = new BeanInfo<>() {
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

            @Override
            public String toString() {
                return super.toString() + "#testSimpleBean1";
            }
        };
        BeanInfo<TestSimpleBean> beanInfo2 = new BeanInfo<>() {
            @Override
            public Class<?> getBeanClass() {
                return TestSimpleBean.class;
            }

            @Override
            public Set<String> getAllName() {
                Set<String> names = new HashSet<>();
                names.add("testSimpleBean2");
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

            @Override
            public String toString() {
                return super.toString() + "#testSimpleBean2";
            }
        };
        BeanInfo<TestSimpleBean> beanInfo = new BeanInfo<>() {
            @Override
            public Class<?> getBeanClass() {
                return TestSimpleBean.class;
            }

            @Override
            public Set<String> getAllName() {
                Set<String> names = new HashSet<>();
                names.add(TestSimpleBean.class.getName());
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

            @Override
            public String toString() {
                return super.toString() + "#" + TestSimpleBean.class.getName();
            }
        };
        BeanInfoManager beanInfoManager = applicationContext.getBeanInfoManager();
        beanInfoManager.registerBeanInfo(beanInfo1);
        beanInfoManager.registerBeanInfo(beanInfo2);
        beanInfoManager.registerBeanInfo(beanInfo);
    }

    private void inject2(ApplicationBootContext applicationContext) {
        applicationContext.registerSingleBean(TestSimpleBean.class, new TestSimpleBean(), "TestSimpleBean");
        applicationContext.registerSingleBean(TestSimpleBean.class, new TestSimpleBean(), "TestSimpleBean2");
    }

    private void inject3(ApplicationBootContext applicationContext) {
        applicationContext.registerSingleBean(TestBean.class, new TestBean(), "TestBean");
        applicationContext.registerSingleBean(TestBeanInterface1.class, new TestBean(), "TestBeanInterface1");
        applicationContext.registerSingleBean(TestBeanInterface2.class, new TestBean(), "TestBeanInterface2");
    }

    @Test
    public void getBeanInfo() {
        DebbieApplication.create()
                .then(context -> {
                    inject1(context);
                    inject2(context);
                    inject3(context);

                    BeanInfoManager beanInfoManager = context.getBeanInfoManager();
                    BeanInfo<TestSimpleBean> testSimpleBean1 = beanInfoManager.getBeanInfo("testSimpleBean1", null, true);
                    System.out.println(testSimpleBean1);
                    testSimpleBean1 = beanInfoManager.getBeanInfo(TestSimpleBean.class.getName(), TestSimpleBean.class, true);
                    System.out.println(testSimpleBean1);
                    BeanFactory<TestSimpleBean> beanFactory = beanInfoManager.getBeanFactory("TestSimpleBean", null, true);
                    System.out.println(beanFactory);
                    beanFactory = beanInfoManager.getBeanFactory("TestSimpleBean2", TestSimpleBean.class, true);
                    System.out.println(beanFactory);
                    System.out.println("--------------------------------");
                    BeanInfo<TestBean> testBean = beanInfoManager.getBeanInfo("TestBeanInterface1", null, true);
                    System.out.println(testBean);
                    testBean = beanInfoManager.getBeanInfo("TestBeanInterface2", null, true);
                    System.out.println(testBean);
                    testBean = beanInfoManager.getBeanInfo("TestBean", TestBean.class, true);
                    System.out.println(testBean);
                    BeanInfo<TestBeanInterface1> testBean1 = beanInfoManager.getBeanInfo(null, TestBeanInterface1.class, true);
                    System.out.println(testBean1);
                    testBean = beanInfoManager.getBeanInfo(null, TestBean.class, true);
                    System.out.println(testBean);
                    System.out.println("----------");
                    BeanFactory<TestBean> testFactory = beanInfoManager.getBeanFactory("TestBeanInterface1", null, true);
                    System.out.println(testFactory);
                    testFactory = beanInfoManager.getBeanFactory("TestBeanInterface2", null, true);
                    System.out.println(testFactory);
                    testFactory = beanInfoManager.getBeanFactory("TestBean", TestBean.class, true);
                    System.out.println(testFactory);
                    testFactory = beanInfoManager.getBeanFactory(null, TestBean.class, true);
                    System.out.println(testFactory);
                    BeanFactory<TestBeanInterface1> testFactory1 = beanInfoManager.getBeanFactory(null, TestBeanInterface1.class, true);
                    System.out.println(testFactory1);
                    // must error
                    testSimpleBean1 = beanInfoManager.getBeanInfo(null, TestSimpleBean.class, true);
                    System.out.println(testSimpleBean1);
                });
    }
}
