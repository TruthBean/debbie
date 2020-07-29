package com.truthbean.debbie.check.bean;

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
import com.truthbean.debbie.data.transformer.DataTransformer;
import org.junit.jupiter.api.*;

@DebbieBootApplication(scan = @DebbieScan(basePackages = "com.truthbean.debbie"))
public class BeanConfigurationRegisterTest {

    private static ApplicationFactory factory;

    @BeforeAll
    static void setUp() {
        factory = ApplicationFactory.configure(BeanConfigurationRegisterTest.class);
    }

    @AfterAll
    static void tearDown() {
        factory.release();
    }

    @Test
    void register() {
        ApplicationContext context = factory.getApplicationContext();
        GlobalBeanFactory beanFactory = context.getGlobalBeanFactory();
        DataTransformer<Integer, Character> bean = beanFactory.factory("dataTransformer");
        System.out.println(bean.reverse('a'));

        System.out.println(beanFactory.factory("hehe").toString());
        System.out.println(beanFactory.factory("狄青").toString());
        System.out.println(beanFactory.factory("幽灵").toString());

        PropertiesConfigurationTest test = beanFactory.factory(PropertiesConfigurationTest.class);
        System.out.println(test.getHehe());
        System.out.println("------------------------------------------------------------------------------");

        System.out.println(beanFactory.factory(ABean.class));
        System.out.println("------------------------------------------------------------------------------");
        System.out.println(beanFactory.factory(BBean.class));
        System.out.println("------------------------------------------------------------------------------");
        System.out.println(beanFactory.factory(CBean.class));
        System.out.println("------------------------------------------------------------------------------");
        ABCBean abcBean = beanFactory.factory(ABCBean.class);
        System.out.println(abcBean);
        System.out.println(abcBean.getaBean());
        System.out.println(abcBean.getbBean());
        System.out.println(abcBean.getcBean());

        System.out.println("===============================================================================");
        System.out.println("===============================================================================");

        System.out.println(beanFactory.factory(A.class));
        System.out.println("------------------------------------------------------------------------------");
        System.out.println(beanFactory.factory(B.class));
        System.out.println("------------------------------------------------------------------------------");
        System.out.println(beanFactory.factory(C.class));
        System.out.println("------------------------------------------------------------------------------");
        Abc abc = beanFactory.factory(Abc.class);
        System.out.println(abc);
        System.out.println(abc.getaBean());
        System.out.println(abc.getbBean());
        System.out.println(abc.getcBean());

    }
}