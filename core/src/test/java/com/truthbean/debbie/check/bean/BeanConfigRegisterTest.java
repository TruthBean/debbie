package com.truthbean.debbie.check.bean;

import com.truthbean.Console;
import com.truthbean.debbie.bean.BeanInject;
import com.truthbean.debbie.bean.BeanScanConfiguration;
import com.truthbean.debbie.bean.DebbieScan;
import com.truthbean.debbie.bean.inter.*;
import com.truthbean.debbie.bean.lifecycle.LifecycleBeanTest;
import com.truthbean.debbie.boot.DebbieApplication;
import com.truthbean.debbie.check.properties.PropertiesConfigurationTest;
import com.truthbean.debbie.test.annotation.DebbieApplicationTest;
import com.truthbean.transformer.DataTransformer;
// import com.truthbean.debbie.test.annotation.DebbieApplicationTest;
import org.junit.jupiter.api.Test;

import java.util.Optional;

@DebbieApplicationTest(scan =
    @DebbieScan(basePackages = {"com.truthbean.debbie.check.bean",
            "com.truthbean.debbie.bean.inter", "com.truthbean.debbie.bean.lifecycle"}))
class BeanConfigRegisterTest {

    static {
        System.setProperty(DebbieApplication.DISABLE_DEBBIE, "false");
    }

    @Test
    void testAbc() {
        C c = new CImpl();
        B b = new BImpl(c);
        A a = new AImpl(b, c);
        Abc abc = new AbcImpl(a, b, c);
        Console.println(abc);

        CBean cBean = new CBean();
        ABean aBean = new ABean(cBean);
        BBean bBean = new BBean(aBean, cBean);
        cBean.setABean(bBean);
        cBean.setBBean(aBean);
        Console.println(bBean);
    }

    @Test
    public void testParams(@BeanInject(name = "我是谁") Object 我是谁) {
        Console.println(我是谁);
    }

    @Test
    void testCycle(@BeanInject ABean aBean, @BeanInject BBean bBean, @BeanInject CBean cBean) {
        Console.println(cBean);
        Console.println(aBean);
        Console.println(bBean);
    }

    @Test
    void register(@BeanInject("dataTransformer") DataTransformer<Integer, Character> dataTransformer,
                  @BeanInject("hehe") Object hehe,
                  @BeanInject("狄青") Object 狄青,
                  @BeanInject("幽灵") Object 幽灵,
                  // @BeanInject(require = false) Optional<PropertiesConfigurationTest> optional,
                  @BeanInject BBean bBean,
                  @BeanInject ABean aBean,
                  @BeanInject CBean cBean,
                  @BeanInject ABCBean abcBean,
                  @BeanInject A a, @BeanInject B b, @BeanInject C c,
                  @BeanInject Abc abc,
                  @BeanInject DemoBeanComponent demoBeanComponent) {
        Console.println(dataTransformer.reverse('a'));
        Console.println(hehe);
        Console.println(狄青);
        Console.println(幽灵);
        Console.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
        // optional.ifPresent(test -> Console.println(test.getHehe()));
        Console.println("------------------------------------------------------------------------------");
        Console.println(bBean);
        Console.println(aBean);
        Console.println(cBean);
        Console.println("------------------------------------------------------------------------------");
        Console.println(abcBean);
        Console.println(abcBean.getaBean());
        Console.println(abcBean.getbBean());
        Console.println(abcBean.getcBean());
        Console.println("===============================================================================");
        Console.println("===============================================================================");
        Console.println(a);
        Console.println("------------------------------------------------------------------------------");
        Console.println(b);
        Console.println("------------------------------------------------------------------------------");
        Console.println(c);
        Console.println("------------------------------------------------------------------------------");
        Console.println(abc);
        Console.println("------------------------------------------------------------------------------");
        Console.println(abc.getaBean());
        Console.println("------------------------------------------------------------------------------");
        Console.println(abc.getbBean());
        Console.println("------------------------------------------------------------------------------");
        Console.println(abc.getcBean());
        Console.println("------------------------------------------------------------------------------");

        Console.println(demoBeanComponent.getUuid());
        Console.println(demoBeanComponent.getDemo1().getUuid());
        Console.println(demoBeanComponent.getDemo2().getUuid());
    }

    @Test
    void demo(@BeanInject DemoBeanComponent demoBeanComponent) {
        Console.println(demoBeanComponent.getUuid());
        DemoBeanComponent.Demo2 demo1 = demoBeanComponent.getDemo1();
        demo1.setId((byte) 1, "111");
        Console.println(demo1.getUuid());

        DemoBeanComponent.Demo2 demo2 = demoBeanComponent.getDemo2();
        demo2.setId((byte) 2, "222");
        Console.println(demo2.getUuid());
    }

    @Test
    void lifecycle(@BeanInject LifecycleBeanTest test, @BeanInject InjectTest injectTest) {
        Console.println(injectTest);
    }

    @Test
    void getConfiguration(@BeanInject BeanScanConfiguration configuration) {
        Console.println(configuration);
    }

}
