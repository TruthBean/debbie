package com.truthbean.debbie.bean;

import com.truthbean.debbie.bean.inter.A;
import com.truthbean.debbie.bean.inter.Abc;
import com.truthbean.debbie.bean.inter.B;
import com.truthbean.debbie.bean.inter.C;
import com.truthbean.debbie.boot.DebbieApplicationFactory;
import com.truthbean.debbie.boot.DebbieBootApplication;
import com.truthbean.debbie.data.transformer.DataTransformer;
import com.truthbean.debbie.properties.PropertiesConfigurationTest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

@DebbieBootApplication(scan = @DebbieScan(basePackages = "com.truthbean.debbie"))
class BeanConfigurationRegisterTest {

    private DebbieApplicationFactory factory;

    @BeforeEach
    void setUp() {
        factory = DebbieApplicationFactory.configure(BeanConfigurationRegisterTest.class);
    }

    @AfterEach
    void tearDown() {
        factory.release();
    }

    @Test
    void register() {

        BeanFactoryContext applicationContext = factory.getBeanFactoryHandler();
        DataTransformer<Integer, Character> bean = applicationContext.factory("dataTransformer");
        System.out.println(bean.reverse('a'));

        System.out.println(applicationContext.factory("hehe").toString());
        System.out.println(applicationContext.factory("狄青").toString());
        System.out.println(applicationContext.factory("幽灵").toString());

        PropertiesConfigurationTest test = applicationContext.factory(PropertiesConfigurationTest.class);
        System.out.println(test.getHehe());
        System.out.println("------------------------------------------------------------------------------");

        System.out.println(applicationContext.factory(ABean.class));
        System.out.println("------------------------------------------------------------------------------");
        System.out.println(applicationContext.factory(BBean.class));
        System.out.println("------------------------------------------------------------------------------");
        System.out.println(applicationContext.factory(CBean.class));
        System.out.println("------------------------------------------------------------------------------");
        ABCBean abcBean = applicationContext.factory(ABCBean.class);
        System.out.println(abcBean);
        System.out.println(abcBean.getaBean());
        System.out.println(abcBean.getbBean());
        System.out.println(abcBean.getcBean());

        System.out.println("===============================================================================");
        System.out.println("===============================================================================");

        System.out.println(applicationContext.factory(A.class));
        System.out.println("------------------------------------------------------------------------------");
        System.out.println(applicationContext.factory(B.class));
        System.out.println("------------------------------------------------------------------------------");
        System.out.println(applicationContext.factory(C.class));
        System.out.println("------------------------------------------------------------------------------");
        Abc abc = applicationContext.factory(Abc.class);
        System.out.println(abc);
        System.out.println(abc.getaBean());
        System.out.println(abc.getbBean());
        System.out.println(abc.getcBean());

    }
}