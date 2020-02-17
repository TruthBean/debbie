package com.truthbean.debbie.bean.lifecycle;

import com.truthbean.debbie.bean.BeanComponent;
import com.truthbean.debbie.bean.BeanDestroy;
import com.truthbean.debbie.bean.BeanInit;

/**
 * @author TruthBean/Rogar·Q
 * @since 0.0.2
 * Created on 2020-02-11 11:54
 */
@BeanComponent
public class LifecycleBeanTest {

    @BeanInit
    public static LifecycleBeanTest init() {
        System.out.println("init");
        return new LifecycleBeanTest();
    }

    @BeanDestroy
    public static void destroy() {
        System.out.println("destroy");
    }
}
