package com.truthbean.debbie.check.bean;

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
        System.out.println(abc);

        CBean cBean = new CBean();
        ABean aBean = new ABean(cBean);
        BBean bBean = new BBean(aBean, cBean);
        cBean.setABean(bBean);
        cBean.setBBean(aBean);
        System.out.println(bBean);
    }

    @Test
    public void testParams(@BeanInject(name = "我是谁") Object 我是谁) {
        System.out.println(我是谁);
    }

    @Test
    void testCycle(@BeanInject ABean aBean, @BeanInject BBean bBean, @BeanInject CBean cBean) {
        System.out.println(cBean);
        System.out.println(aBean);
        System.out.println(bBean);
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
        System.out.println(dataTransformer.reverse('a'));
        System.out.println(hehe);
        System.out.println(狄青);
        System.out.println(幽灵);
        System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
        // optional.ifPresent(test -> System.out.println(test.getHehe()));
        System.out.println("------------------------------------------------------------------------------");
        System.out.println(bBean);
        System.out.println(aBean);
        System.out.println(cBean);
        System.out.println("------------------------------------------------------------------------------");
        System.out.println(abcBean);
        System.out.println(abcBean.getaBean());
        System.out.println(abcBean.getbBean());
        System.out.println(abcBean.getcBean());
        System.out.println("===============================================================================");
        System.out.println("===============================================================================");
        System.out.println(a);
        System.out.println("------------------------------------------------------------------------------");
        System.out.println(b);
        System.out.println("------------------------------------------------------------------------------");
        System.out.println(c);
        System.out.println("------------------------------------------------------------------------------");
        System.out.println(abc);
        System.out.println("------------------------------------------------------------------------------");
        System.out.println(abc.getaBean());
        System.out.println("------------------------------------------------------------------------------");
        System.out.println(abc.getbBean());
        System.out.println("------------------------------------------------------------------------------");
        System.out.println(abc.getcBean());
        System.out.println("------------------------------------------------------------------------------");

        System.out.println(demoBeanComponent.getUuid());
        System.out.println(demoBeanComponent.getDemo1().getUuid());
        System.out.println(demoBeanComponent.getDemo2().getUuid());
    }

    @Test
    void demo(@BeanInject DemoBeanComponent demoBeanComponent) {
        System.out.println(demoBeanComponent.getUuid());
        DemoBeanComponent.Demo2 demo1 = demoBeanComponent.getDemo1();
        demo1.setId((byte) 1, "111");
        System.out.println(demo1.getUuid());

        DemoBeanComponent.Demo2 demo2 = demoBeanComponent.getDemo2();
        demo2.setId((byte) 2, "222");
        System.out.println(demo2.getUuid());
    }

    @Test
    void lifecycle(@BeanInject LifecycleBeanTest test, @BeanInject InjectTest injectTest) {
        System.out.println(injectTest);
    }

    @Test
    void getConfiguration(@BeanInject BeanScanConfiguration configuration) {
        System.out.println(configuration);
    }

}
