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
        factory = new DebbieApplicationFactory(BeanConfigurationRegisterTest.class);
        factory.config(BeanConfigurationRegisterTest.class);
        factory.callStarter();
    }

    @AfterEach
    void tearDown() {
        factory.release();
    }

    @Test
    void register() {

        BeanFactoryHandler beanFactoryHandler = factory.getBeanFactoryHandler();
        DataTransformer<Integer, Character> bean = beanFactoryHandler.factory("dataTransformer");
        System.out.println(bean.reverse('a'));

        System.out.println(beanFactoryHandler.factory("hehe").toString());
        System.out.println(beanFactoryHandler.factory("狄青").toString());
        System.out.println(beanFactoryHandler.factory("幽灵").toString());

        PropertiesConfigurationTest test = beanFactoryHandler.factory(PropertiesConfigurationTest.class);
        System.out.println(test.getHehe());
        System.out.println("------------------------------------------------------------------------------");

        System.out.println(beanFactoryHandler.factory(ABean.class));
        System.out.println("------------------------------------------------------------------------------");
        System.out.println(beanFactoryHandler.factory(BBean.class));
        System.out.println("------------------------------------------------------------------------------");
        System.out.println(beanFactoryHandler.factory(CBean.class));
        System.out.println("------------------------------------------------------------------------------");
        ABCBean abcBean = beanFactoryHandler.factory(ABCBean.class);
        System.out.println(abcBean);
        System.out.println(abcBean.getaBean());
        System.out.println(abcBean.getbBean());
        System.out.println(abcBean.getcBean());

        System.out.println("===============================================================================");
        System.out.println("===============================================================================");

        System.out.println(beanFactoryHandler.factory(A.class));
        System.out.println("------------------------------------------------------------------------------");
        System.out.println(beanFactoryHandler.factory(B.class));
        System.out.println("------------------------------------------------------------------------------");
        System.out.println(beanFactoryHandler.factory(C.class));
        System.out.println("------------------------------------------------------------------------------");
        Abc abc = beanFactoryHandler.factory(Abc.class);
        System.out.println(abc);
        System.out.println(abc.getaBean());
        System.out.println(abc.getbBean());
        System.out.println(abc.getcBean());

    }
}