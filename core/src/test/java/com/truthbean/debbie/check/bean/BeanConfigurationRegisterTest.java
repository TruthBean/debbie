package com.truthbean.debbie.check.bean;

import com.truthbean.Console;
import com.truthbean.debbie.bean.DebbieScan;
import com.truthbean.debbie.bean.GlobalBeanFactory;
import com.truthbean.debbie.bean.inter.A;
import com.truthbean.debbie.bean.inter.Abc;
import com.truthbean.debbie.bean.inter.B;
import com.truthbean.debbie.bean.inter.C;
import com.truthbean.debbie.boot.DebbieBootApplication;
import com.truthbean.debbie.check.properties.PropertiesConfigurationTest;
import com.truthbean.debbie.core.ApplicationContext;
import com.truthbean.debbie.core.ApplicationFactory;
import com.truthbean.transformer.DataTransformer;
import org.junit.jupiter.api.*;

@DebbieBootApplication(scan = @DebbieScan(basePackages = "com.truthbean.debbie"))
public class BeanConfigurationRegisterTest {

    private static volatile ApplicationFactory factory;

    @BeforeAll
    static void setUp() {
    }

    @AfterAll
    static void tearDown() {
        if (factory != null)
            factory.release();
    }

    @Test
    void register() {
        for (int i = 0; i < 100; i++) {
            new Thread(() -> {
                factory = ApplicationFactory.configure(BeanConfigurationRegisterTest.class);
                ApplicationContext context = factory.getApplicationContext();
                GlobalBeanFactory beanFactory = context.getGlobalBeanFactory();
                DataTransformer<Integer, Character> bean = beanFactory.factory("dataTransformer");
                Console.println(bean.reverse('a'));

                Console.println(beanFactory.factory("hehe").toString());
                Console.println(beanFactory.factory("狄青").toString());
                Console.println(beanFactory.factory("幽灵").toString());

                PropertiesConfigurationTest test = beanFactory.factory(PropertiesConfigurationTest.class);
                Console.println(test.getHehe());
                Console.println("------------------------------------------------------------------------------");

                Console.println(beanFactory.factory(BBean.class));
                Console.println("------------------------------------------------------------------------------");
                Console.println(beanFactory.factory(ABean.class));
                Console.println("------------------------------------------------------------------------------");
                Console.println(beanFactory.factory(CBean.class));
                Console.println("------------------------------------------------------------------------------");
                ABCBean abcBean = beanFactory.factory(ABCBean.class);
                Console.println(abcBean);
                Console.println(abcBean.getaBean());
                Console.println(abcBean.getbBean());
                Console.println(abcBean.getcBean());

                Console.println("===============================================================================");
                Console.println("===============================================================================");

                Console.println(beanFactory.factory(A.class));
                Console.println("------------------------------------------------------------------------------");
                Console.println(beanFactory.factory(B.class));
                Console.println("------------------------------------------------------------------------------");
                Console.println(beanFactory.factory(C.class));
                Console.println("------------------------------------------------------------------------------");
                Abc abc = beanFactory.factory(Abc.class);
                Console.println(abc);
                Console.println(abc.getaBean());
                Console.println(abc.getbBean());
                Console.println(abc.getcBean());
            }).start();
        }
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}